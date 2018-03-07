package com.mmall.controller;

import com.google.common.base.Splitter;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.domain.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVO;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
public class CartController {

	@Autowired
	private ICartService cartService;

	@RequestMapping("/list.do")
	public ServerResponse<CartVO> list(HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getMsg());
		}
		return cartService.list(user.getId());
	}

	@RequestMapping("/add.do")
	public ServerResponse<CartVO> add(HttpSession session , @RequestParam("productId") Integer productId , @RequestParam("count") Integer count){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getMsg());
		}
		return cartService.add(user.getId() , productId , count);
	}

	@RequestMapping("update.do")
	public ServerResponse<CartVO> update(HttpSession session , @RequestParam("productId") Integer productId , @RequestParam("count") Integer count){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getMsg());
		}
		return cartService.update(user.getId() , productId , count);
	}

	@RequestMapping("delete_product.do")
	public ServerResponse<CartVO> deleteProduct(HttpSession session , @RequestParam("productIds") String productIds){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getMsg());
		}
		List<String> idStr = Splitter.on(",").splitToList(productIds);
		List<Integer> ids = new ArrayList<>();
		for (String s : idStr) {
			ids.add(Integer.parseInt(s));
		}
		return cartService.deleteProduct(user.getId() , ids);
	}

	@RequestMapping("select_all.do")
	public ServerResponse<CartVO> selectAll(HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getMsg());
		}
		return cartService.selectOrUnSelect(user.getId() , null , Const.Cart.CHECKED);
	}

	@RequestMapping("un_select_all.do")
	public ServerResponse<CartVO> unSelectAll(HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getMsg());
		}
		return cartService.selectOrUnSelect(user.getId() , null , Const.Cart.UNCHECK);
	}

	@RequestMapping("select.do")
	public ServerResponse<CartVO> selectAll(HttpSession session , Integer productId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getMsg());
		}
		return cartService.selectOrUnSelect(user.getId() , productId , Const.Cart.CHECKED);
	}

	@RequestMapping("un_select.do")
	public ServerResponse<CartVO> unSelectAll(HttpSession session , Integer productId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getMsg());
		}
		return cartService.selectOrUnSelect(user.getId() , productId , Const.Cart.UNCHECK);
	}

	@RequestMapping("get_cart_product_count.do")
	public ServerResponse<Integer> getCartProductCount(HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getMsg());
		}
		return cartService.getCartProductCount(user.getId());
	}
}
