package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.domain.Cart;
import com.mmall.domain.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVO;
import com.mmall.vo.CartVO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements ICartService {

	@Autowired
	private CartMapper cartMapper;
	@Autowired
	private ProductMapper productMapper;


	@Override
	public ServerResponse<CartVO> add(Integer userId, Integer productId, Integer count) {
		if (userId == null || productId == null || count == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		com.mmall.domain.Cart cart = cartMapper.selectByUserIdProductId(userId, productId);
		//购物车中已经存在此商品，修改数量
		if (cart != null){
			cart.setQuantity(cart.getQuantity()+count);
			cart.setChecked(Const.Cart.CHECKED);
			cartMapper.updateByPrimaryKeySelective(cart);
		}else {
			//购物车中原不存在此商品，增加
			com.mmall.domain.Cart newCart = new com.mmall.domain.Cart(userId , productId , count , Const.Cart.CHECKED);
			int insert = cartMapper.insert(newCart);
		}
		return list(userId);
	}

	@Override
	public ServerResponse<CartVO> update(Integer userId, Integer productId, Integer count) {
		if (userId == null || productId == null ||  count == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		Cart cart = cartMapper.selectByUserIdProductId(userId, productId);
		if (cart != null){
			cart.setQuantity(count);
		}
		cartMapper.updateByPrimaryKeySelective(cart);
		return list(userId);
	}

	@Override
	public ServerResponse<CartVO> deleteProduct(Integer userId, List<Integer> productIds) {
		if (userId == null || productIds == null || productIds.isEmpty()){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		cartMapper.deleteByUserIdProductId(userId , productIds);
		return list(userId);
	}

	@Override
	public ServerResponse<CartVO> list(Integer userId) {
		return ServerResponse.Success(getBuyLimit(userId));
	}

	@Override
	public ServerResponse<CartVO> selectOrUnSelect(Integer userId, Integer productId, Integer checked) {
		if (userId == null || productId == null || checked == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		cartMapper.setChecked(userId , productId , checked);
		return list(userId);
	}

	@Override
	public ServerResponse<Integer> getCartProductCount(Integer userId) {
		int count = 0;
		if (userId != null){
			count = cartMapper.countByUserId(userId);
		}
		return ServerResponse.Success(new Integer(count));
	}

	private CartVO getBuyLimit(int userId){
		//查询用户购物车
		List<Cart> carts = cartMapper.selectByUserId(userId);
		List<CartProductVO> cartProductVOS = new ArrayList<>();
		BigDecimal totalPrice = new BigDecimal("0");

		for (Cart cart : carts) {
			CartProductVO vo = new CartProductVO();
			Product product = productMapper.selectByPrimaryKey(cart.getProductId());
			if (product != null){
				vo.setProductId(product.getId());
				vo.setProductMainImage(product.getMainImage());
				vo.setProductName(product.getName());
				vo.setProductPrice(product.getPrice());
				vo.setProductStatus(product.getStatus());
				vo.setProductStock(product.getStock());
				//判断库存与购买量关系
				if (product.getStock() < cart.getQuantity()){
					vo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
					vo.setQuantity(product.getStock());
					//更新购物车中的购买数
					Cart update = new Cart();
					update.setId(cart.getId());
					update.setQuantity(product.getStock());
					cartMapper.updateByPrimaryKeySelective(update);
				}else{
					vo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
					vo.setQuantity(cart.getQuantity());
				}
				vo.setProductChecked(cart.getChecked());
				//计算单品总价
				vo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue() , vo.getQuantity()));
			}
			//计算勾选的商品总价
			if (vo.getProductChecked() == Const.Cart.CHECKED){
				totalPrice = BigDecimalUtil.add(totalPrice.doubleValue() , vo.getProductTotalPrice().doubleValue());
			}
			cartProductVOS.add(vo);
		}
		CartVO cartVO = new CartVO();
		cartVO.setCartProductVoList(cartProductVOS);
		cartVO.setCartTotalPrice(totalPrice);
		cartVO.setImageHost(PropertiesUtil.getProperty(Const.IMAGE_HOST));
		cartVO.setAllChecked(isAllChecked(cartProductVOS));
		return cartVO;
	}

	private Boolean isAllChecked(List<CartProductVO> cartProductVOS) {
		for (CartProductVO cartProductVO : cartProductVOS) {
			if (cartProductVO.getProductChecked() != Const.Cart.CHECKED){
				return false;
			}
		}
		return true;
	}

}
