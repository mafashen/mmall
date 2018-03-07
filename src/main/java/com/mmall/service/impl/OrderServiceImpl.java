package com.mmall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.OrderStatusEnum;
import com.mmall.common.PayPlatformEnum;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.common.StatusEnum;
import com.mmall.dao.CartMapper;
import com.mmall.dao.OrderItemMapper;
import com.mmall.dao.OrderMapper;
import com.mmall.dao.PayInfoMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.dao.ShippingMapper;
import com.mmall.domain.Cart;
import com.mmall.domain.Order;
import com.mmall.domain.OrderItem;
import com.mmall.domain.PayInfo;
import com.mmall.domain.Product;
import com.mmall.domain.Shipping;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.OrderItemVO;
import com.mmall.vo.OrderProductVO;
import com.mmall.vo.OrderVO;
import com.mmall.vo.ShippingVO;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import jodd.bean.BeanCopy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class OrderServiceImpl implements IOrderService {

	private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private OrderItemMapper orderItemMapper;

	@Autowired
	private PayInfoMapper payInfoMapper;

	@Autowired
	private ShippingMapper shippingMapper;

	@Autowired
	private ProductMapper productMapper;

	@Autowired
	private CartMapper cartMapper;

	@Autowired
	@Qualifier("transactionTemplate")
	private TransactionTemplate transactionTemplate;


	// 支付宝当面付2.0服务
	private static AlipayTradeService tradeService;

	static {
		/** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
		 *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
		 */
		Configs.init("zfbinfo.properties");

		/** 使用Configs提供的默认参数
		 *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
		 */
		tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
	}

	@Override
	public ServerResponse pay(Long orderNo, Integer userId, String path) {
		if (orderNo == null || userId == null || path == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
		if (order == null){
			return ServerResponse.Failure("没有查询到该用户对应的订单:"+orderNo);
		}
		Map<String , String> resultMap = new HashMap<>();
		resultMap.put("orderNo",String.valueOf(order.getOrderNo()));
		// (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
		// 需保证商户系统端不能重复，建议通过数据库sequence生成，
		String outTradeNo = order.getOrderNo().toString();

		// (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店消费”
		String subject = "happymmall扫码支付,订单号:" + order.getOrderNo();

		// (必填) 订单总金额，单位为元，不能超过1亿元
		// 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
		String totalAmount = order.getPayment().toString();

		// (可选，根据需要决定是否使用) 订单可打折金额，可以配合商家平台配置折扣活动，如果订单部分商品参与打折，可以将部分商品总价填写至此字段，默认全部商品可打折
		// 如果该值未传入,但传入了【订单总金额】,【不可打折金额】 则该值默认为【订单总金额】- 【不可打折金额】
		//        String discountableAmount = "1.00"; //

		// (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
		// 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
		String undiscountableAmount = "0.0";

		// 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
		// 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
		String sellerId = "";

		// 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品3件共20.00元"
		String body = "订单:"+order.getOrderNo() + "总计"+ order.getPayment().toString()+"元";

		// 商户操作员编号，添加此参数可以为商户操作员做销售统计
		String operatorId = "test_operator_id";

		// (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
		String storeId = "test_store_id";

		// 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
		String providerId = "2088100200300400500";
		ExtendParams extendParams = new ExtendParams();
		extendParams.setSysServiceProviderId(providerId);

		// 支付超时，线下扫码交易定义为5分钟
		String timeoutExpress = "120m";

		// 商品明细列表，需填写购买商品详细信息，
		List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

		List<OrderItem> orderItems = orderItemMapper.selectByUserIdAndOrderNo(userId, orderNo);
		if(orderItems != null && !orderItems.isEmpty()){
			for (OrderItem orderItem : orderItems) {
				// 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
				GoodsDetail goods1 = GoodsDetail.newInstance(orderItem.getProductId().toString(),
						orderItem.getProductName(),
						BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue() , new Double(100).doubleValue()).longValue(),
						orderItem.getQuantity());
				// 创建好一个商品后添加至商品明细列表
				goodsDetailList.add(goods1);
			}
		}

		// 创建扫码支付请求builder，设置请求参数
		AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
				.setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
				.setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
				.setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
				.setTimeoutExpress(timeoutExpress)
				.setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
				.setGoodsDetailList(goodsDetailList);

		// 调用tradePay方法获取当面付应答
		AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
		switch (result.getTradeStatus()) {
			case SUCCESS:
				AlipayTradePrecreateResponse response = result.getResponse();
				dumpResponse(response);

				File folder = new File(path);
				if(!folder.exists()){
					folder.setWritable(true);
					folder.mkdirs();
				}
				// 需要修改为运行机器上的路径
				String qrPath = String.format(path+"/qr-%s.png", response.getOutTradeNo());
				String qrFileName = String.format("qr-%s.png",response.getOutTradeNo());
				ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
				//上传二维码至FTP服务器
				File targetFile = new File(path,qrFileName);
				try {
					//FTPUtil.upload2FTP(Lists.newArrayList(targetFile));
				} catch (Exception e) {
					logger.error("上传二维码异常",e);
				}
				logger.info("qrPath:" + qrPath);
				String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFile.getName();
				resultMap.put("qrUrl",qrUrl);
				return ServerResponse.Success(resultMap);

			case FAILED:
				logger.error("支付宝预下单失败!!!");
				return ServerResponse.Failure("支付宝预下单失败!!!");

			case UNKNOWN:
				logger.error("系统异常，预下单状态未知!!!");
				return ServerResponse.Failure("系统异常，预下单状态未知!!!");

			default:
				logger.error("不支持的交易状态，交易返回异常!!!");
				return ServerResponse.Failure("不支持的交易状态，交易返回异常!!!");
		}
	}

	// 简单打印应答
	private void dumpResponse(AlipayResponse response) {
		if (response != null) {
			logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
			if (StringUtils.isNotEmpty(response.getSubCode())) {
				logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
						response.getSubMsg()));
			}
			logger.info("body:" + response.getBody());
		}
	}

	@Override
	public ServerResponse alipayCallBack(Map<String, String> params){
		String out_trade_no = params.get("out_trade_no");
		if (StringUtils.isNotBlank(out_trade_no)){
			try{
				Order order = orderMapper.selectByOrderNo(Long.parseLong(out_trade_no));
				if (order != null){
					String tradeNo = params.get("trade_no");
					String tradeStatus = params.get("trade_status");
					String totalAmount = params.get("total_amount");
					//已经付过款,已发货,订单完成,订单关闭情况忽略
					if (order.getStatus() >= OrderStatusEnum.PAID.getCode()){
						return ServerResponse.Failure("支付宝重复调用");
					}
					//验证交易金额是否一致
					if (totalAmount == null || BigDecimalUtil.sub(order.getPayment().doubleValue() , Double.parseDouble(totalAmount)).doubleValue() != 0D){
						return ServerResponse.Failure("交易金额不一致");
					}
					if (Const.AlipayCallback.RESPONSE_SUCCESS.equals(tradeStatus)){
						//已经支付,更新订单状态
						order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
						order.setStatus(OrderStatusEnum.PAID.getCode());
						orderMapper.updateByPrimaryKeySelective(order);
					}
					//持久化PayInfo
					PayInfo payInfo = new PayInfo();
					payInfo.setUserId(order.getUserId());
					payInfo.setOrderNo(order.getOrderNo());
					payInfo.setPayPlatform(PayPlatformEnum.ALIPAY.getCode());
					payInfo.setPlatformNumber(tradeNo);
					payInfo.setPlatformStatus(tradeStatus);
					payInfoMapper.insert(payInfo);

					return ServerResponse.Success();
				}

			}catch (NumberFormatException e){
				return ServerResponse.Failure("非快乐慕商城的订单,回调忽略");
			}
		}
		return ServerResponse.Failure("非快乐慕商城的订单,回调忽略");
	}

	@Override
	public ServerResponse queryOrderPayStatus(Integer userId, Long orderNo){
		if (orderNo == null || userId == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
		if (order == null){
			return ServerResponse.Failure("没有查询到该用户对应的订单:"+orderNo);
		}
		return order.getStatus() > OrderStatusEnum.PAID.getCode() ? ServerResponse.Success() : ServerResponse.Failure();
	}

	@Override
	public ServerResponse createOrder(Integer userId, Integer shippingId) {
		if (userId == null || shippingId == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		//计算购物车中checked总价 , 检查商品状态
		final List<Cart> carts = cartMapper.selectByUserIdAndChecked(userId);
		ServerResponse cartOrderItemRet = getCartOrderItem(userId, carts);
		if (!cartOrderItemRet.isSuccess()){
			return cartOrderItemRet;
		}
		final List<OrderItem> orderItems = (List<OrderItem>) cartOrderItemRet.getData();
		//总价
		BigDecimal totalPrice = new BigDecimal("0");
		for (OrderItem orderItem : orderItems) {
			totalPrice = BigDecimalUtil.add(totalPrice.doubleValue(), orderItem.getTotalPrice().doubleValue());
		}

		//生成订单 , 插入order表 , 生成订单号
		final Order order = new Order();
		final long orderNo = System.currentTimeMillis() + new Random().nextInt(10);
		order.setOrderNo(orderNo);
		order.setPostage(0);
		order.setUserId(userId);
		order.setPayment(totalPrice);
		order.setShippingId(shippingId);
		order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
		order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());

		return transactionTemplate.execute(new TransactionCallback<ServerResponse>() {
			@Override
			public ServerResponse doInTransaction(TransactionStatus transactionStatus) {
				//发货时间,付款时间
				int insert = orderMapper.insert(order);
				if (insert <= 0){
					return ServerResponse.Failure("生成订单错误");
				}
				for (OrderItem orderItem : orderItems) {
					orderItem.setOrderNo(orderNo);
				}
				//插入orderItem表
				orderItemMapper.batchInsert(orderItems);
				//TODO 减库存 事务控制
				for (OrderItem orderItem : orderItems) {
					Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
					product.setStock(product.getStock() - orderItem.getQuantity());
					productMapper.updateByPrimaryKeySelective(product);
				}
				//清空购物车
				for (Cart cart : carts) {
					cartMapper.deleteByPrimaryKey(cart.getId());
				}
				//组装VO返回给前端
				return ServerResponse.Success(assemblingOrderVO(order , orderItems));
			}
		});
	}

	private OrderVO assemblingOrderVO(Order order, List<OrderItem> orderItems) {
		OrderVO orderVO = new OrderVO();
		orderVO.setOrderNo(order.getOrderNo());
		orderVO.setPayment(order.getPayment());
		orderVO.setPostage(order.getPostage());
		orderVO.setShippingId(order.getShippingId());
		orderVO.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
		orderVO.setPaymentTypeDesc(Const.PaymentTypeEnum.ONLINE_PAY.getValue());
		orderVO.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
		orderVO.setStatusDesc(Const.OrderStatusEnum.NO_PAY.getValue());
		orderVO.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
		orderVO.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
		orderVO.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
		orderVO.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
		orderVO.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
		orderVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

		orderVO.setOrderItemVoList(assemblingOrderItemVO(orderItems));

		Shipping shipping = shippingMapper.selectByPrimaryKey(order.getUserId(), order.getShippingId());
		if (shipping != null){
			orderVO.setReceiverName(shipping.getReceiverName());
			orderVO.setShippingVo(assemblingShippingVO(shipping));
		}

		return orderVO;
	}

	private ShippingVO assemblingShippingVO(Shipping shipping) {
		ShippingVO vo = new ShippingVO();
		new BeanCopy(shipping , vo).copy();
		return vo;
	}

	private List<OrderItemVO> assemblingOrderItemVO(List<OrderItem> orderItems){
		List<OrderItemVO> orderItemVOS = new LinkedList<>();
		if (orderItems == null || orderItems.isEmpty()){
			return orderItemVOS;
		}
		for (OrderItem orderItem : orderItems) {
			OrderItemVO vo = new OrderItemVO();
			new BeanCopy(orderItem , vo).copy();
			vo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));

			orderItemVOS.add(vo);
		}
		return orderItemVOS;
	}

	private ServerResponse getCartOrderItem(Integer userId, List<Cart> carts) {
		if (carts == null || carts.isEmpty()){
			return ServerResponse.Failure("购物车中没有选中的商品");
		}
		List<OrderItem> orderItems = new ArrayList<>();
		//检查商品状态
		for (Cart cart : carts) {
			Product product = productMapper.selectByPrimaryKey(cart.getProductId());
			if (product == null){
				return ServerResponse.Failure("商品" + cart.getProductId() + "不存在");
			}
			if (product.getStatus() != StatusEnum.ONLINE.getCode()){
				return ServerResponse.Failure("商品" + cart.getProductId() + "不是在售状态");
			}
			if (product.getStock() < cart.getQuantity()){
				return ServerResponse.Failure("商品" + cart.getProductId() + "库存不足");
			}

			OrderItem item = new OrderItem();
			item.setUserId(userId);
			item.setQuantity(cart.getQuantity());
			item.setProductId(cart.getProductId());
			item.setProductName(product.getName());
			item.setProductImage(product.getMainImage());
			item.setCurrentUnitPrice(product.getPrice());
			item.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cart.getQuantity()));

			orderItems.add(item);
		}
		return ServerResponse.Success(orderItems);
	}

	@Override
	public ServerResponse<String> cancel(Integer userId, Long orderNo) {
		if (userId == null || orderNo == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
		if (order == null){
			return ServerResponse.Failure("此用户不存在该订单号对应的订单:" + orderNo);
		}
		order.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
		int update = orderMapper.updateByPrimaryKeySelective(order);
		if (update > 0){
			return ServerResponse.Success("取消订单成功");
		}
		return ServerResponse.Failure("取消订单失败");
	}

	@Override
	public ServerResponse getOrderCartProduct(Integer userId) {
		if (userId == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		List<Cart> carts = cartMapper.selectByUserIdAndChecked(userId);
		ServerResponse cartOrderItemRet = getCartOrderItem(userId, carts);
		if (!cartOrderItemRet.isSuccess()){
			return cartOrderItemRet;
		}
		List<OrderItem> orderItems = (List<OrderItem>) cartOrderItemRet.getData();
		BigDecimal payment = new BigDecimal("0");
		for (OrderItem orderItem : orderItems) {
			payment = BigDecimalUtil.add(orderItem.getTotalPrice().doubleValue(), payment.doubleValue());
		}

		OrderProductVO vo = new OrderProductVO();
		vo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
		vo.setOrderItemVoList(assemblingOrderItemVO(orderItems));
		vo.setProductTotalPrice(payment);

		return ServerResponse.Success(vo);
	}

	@Override
	public ServerResponse<OrderVO> getOrderDetail(Integer userId, Long orderNo) {
		if (userId == null || orderNo == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
		if (order != null){
			List<OrderItem> orderItems = orderItemMapper.selectByUserIdAndOrderNo(userId, orderNo);
			return ServerResponse.Success(assemblingOrderVO(order, orderItems));
		}
		return ServerResponse.Failure("此用户不存在该订单号对应的订单:" + orderNo);
	}

	@Override
	public ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize) {
		if (userId == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		List<Order> orders = orderMapper.selectByUserId(userId);
		PageHelper.startPage(pageNum, pageSize);
		PageInfo pageInfo = new PageInfo(orders);
		pageInfo.setList(assemblingOrderVOList(userId , orders));
		return ServerResponse.Success(pageInfo);
	}

	private List<OrderVO> assemblingOrderVOList(Integer userId , List<Order> orders){
		List<OrderVO> orderVOS = new LinkedList<>();
		List<OrderItem> orderItems = null;
		for (Order order : orders) {
			if (userId != null){
				orderItems = orderItemMapper.selectByUserIdAndOrderNo(userId, order.getOrderNo());
			}else{
				orderItems = orderItemMapper.selectByOrderNo(order.getOrderNo());
			}
			orderVOS.add(assemblingOrderVO(order, orderItems));
		}
		return orderVOS;
	}


	/***后台****/


	@Override
	public ServerResponse<PageInfo> manageList(int pageNum, int pageSize) {
		List<Order> orders = orderMapper.selectAllOrders();
		PageHelper.startPage(pageNum, pageSize);
		PageInfo pageInfo = new PageInfo(orders);
		pageInfo.setList(assemblingOrderVOList(null , orders));
		return ServerResponse.Success(pageInfo);
	}

	@Override
	public ServerResponse<OrderVO> manageDetail(Long orderNo) {
		if(orderNo == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		Order order = orderMapper.selectByOrderNo(orderNo);
		if (order != null){
			List<OrderItem> orderItems = orderItemMapper.selectByOrderNo(orderNo);
			return ServerResponse.Success(assemblingOrderVO(order , orderItems));
		}
		return ServerResponse.Failure("不存在此订单号对应的订单:"+orderNo);
	}

	@Override
	public ServerResponse<PageInfo> manageSearch(Long orderNo, int pageNum, int pageSize) {
		if(orderNo == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		Order order = orderMapper.selectByOrderNo(orderNo);
		if (order != null){
			List<OrderItem> orderItems = orderItemMapper.selectByOrderNo(orderNo);
			OrderVO orderVO = assemblingOrderVO(order, orderItems);

			PageHelper.startPage(pageNum, pageSize);
			PageInfo pageInfo = new PageInfo(Arrays.asList(orderVO));
			pageInfo.setList(Arrays.asList(orderVO));
			return ServerResponse.Success(pageInfo);
		}
		return ServerResponse.Failure("不存在此订单号对应的订单:"+orderNo);
	}

	@Override
	public ServerResponse<String> manageSendGoods(Long orderNo) {
		if(orderNo == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		Order order = orderMapper.selectByOrderNo(orderNo);
		if (order != null){
			if (order.getStatus() == Const.OrderStatusEnum.PAID.getCode()){
				order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
				order.setSendTime(new Date());
				int update = orderMapper.updateByPrimaryKeySelective(order);
				if (update > 0){
					return ServerResponse.Success("发货成功");
				}
				return ServerResponse.Failure("发货失败");
			}
		}
		return ServerResponse.Failure("不存在此订单号对应的订单:"+orderNo);
	}
}
