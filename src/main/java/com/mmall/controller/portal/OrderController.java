package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.domain.User;
import com.mmall.service.IOrderService;
import com.mmall.util.SessionUtil;
import com.mmall.vo.OrderVO;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order/")
public class OrderController {

	private Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private IOrderService orderService;
	@Autowired
	private SessionUtil sessionUtil;

	@RequestMapping("pay.do")
	public ServerResponse pay(Long orderNo, HttpServletRequest request){
		User user = sessionUtil.checkLogin(request);
		if(user ==null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getMsg());
		}
		String path = request.getSession().getServletContext().getRealPath("upload");
		return orderService.pay(orderNo,user.getId(),path);
	}

	@RequestMapping("alipay_callback.do")
	public Object callBack(HttpServletRequest request){
		Map<String, String> params = new LinkedHashMap<String, String>() {

			@Override
			public String put(String key, String value) {
				System.out.println(key + " " + value);
				return super.put(key, value);

			}
		};
		Map<String, String[]> parameterMap = request.getParameterMap();
//		for (String key : parameterMap.keySet()) {
//			String[] values = parameterMap.get(key);
//			String valueStr = "";
//			for (String value : values) {
//				valueStr = values.length > 1 ? valueStr + "," + value : valueStr + value;
//			}
//			params.put(key, valueStr);
//		}
		for (Iterator iter = parameterMap.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) parameterMap.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		logger.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());

		//非常重要,验证回调的正确性,是不是支付宝发的.并且呢还要避免重复通知.在通知返回参数列表中，除去sign、sign_type两个参数外，凡是通知返回回来的参数皆是待验签的参数。
		params.remove("sign_type");
		try {
			boolean sign = AlipaySignature.rsaCheckV2(params, Configs.getPublicKey(), "utf-8" , Configs.getSignType());
			if (sign){
				return ServerResponse.Failure("非法请求,验证不通过,再恶意请求我就报警找网警了");
			}
		} catch (AlipayApiException e) {
			logger.error("支付宝回调验证失败:",e);
		}

		//TODO 其他各种业务逻辑验证

		//更新订单状态,判断是否应支付过了
		boolean success = orderService.alipayCallBack(params).isSuccess();
		return success ? Const.AlipayCallback.RESPONSE_SUCCESS : Const.AlipayCallback.RESPONSE_FAILED;
	}

	@RequestMapping("query_order_pay_status.do")
	public ServerResponse<Boolean> QueryOrderPayStatus(HttpServletRequest request , Long orderNo){
		User user = sessionUtil.checkLogin(request);
		if(user ==null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getMsg());
		}
		return ServerResponse.Success(orderService.queryOrderPayStatus(user.getId() , orderNo).isSuccess());
	}

	@RequestMapping("create.do")
	public ServerResponse<OrderVO> createOrder(HttpServletRequest request , Integer shippingId){
		User user = sessionUtil.checkLogin(request);
		if(user ==null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getMsg());
		}
		return orderService.createOrder(user.getId(), shippingId);
	}

	@RequestMapping("cancel.do")
	public ServerResponse<String> cancelOrder(HttpServletRequest request , Long orderNo){
		User user = sessionUtil.checkLogin(request);
		if(user ==null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getMsg());
		}
		return orderService.cancel(user.getId(), orderNo);
	}

	@RequestMapping("get_order_cart_product.do")
	public ServerResponse getOrderCartProduct(HttpServletRequest request){
		User user = sessionUtil.checkLogin(request);
		if(user ==null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getMsg());
		}
		return orderService.getOrderCartProduct(user.getId());
	}

	@RequestMapping("detail.do")
	public ServerResponse getDetail(HttpServletRequest request , Long orderNo){
		User user = sessionUtil.checkLogin(request);
		if(user ==null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getMsg());
		}
		return orderService.getOrderDetail(user.getId() , orderNo);
	}

	@RequestMapping("list.do")
	public ServerResponse list(HttpServletRequest request , @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
		User user = sessionUtil.checkLogin(request);
		if(user ==null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getMsg());
		}
		return orderService.getOrderList(user.getId() , pageNum , pageSize);
	}
}
