package com.mmall.controller;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResultCode;
import com.mmall.common.ServerResponse;
import com.mmall.domain.Shipping;
import com.mmall.domain.User;
import com.mmall.service.IShippingService;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shipping/")
public class ShippingController {

	@Autowired
	private IShippingService shippingService;

	@RequestMapping("add.do")
	public ServerResponse add(HttpSession session , Shipping shipping){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			return ServerResponse.Failure(ResultCode.NEED_LOGIN.getCode() , ResultCode.NEED_LOGIN.getMsg());
		}
		return shippingService.add(user.getId() , shipping);
	}

	@RequestMapping("del.do")
	public ServerResponse delete(HttpSession session , Shipping shipping){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			return ServerResponse.Failure(ResultCode.NEED_LOGIN.getCode() , ResultCode.NEED_LOGIN.getMsg());
		}
		return shippingService.update(user.getId() , shipping);
	}

	@RequestMapping("select.do")
	public ServerResponse<Shipping> select(HttpSession session , Integer shippingId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			return ServerResponse.Failure(ResultCode.NEED_LOGIN.getCode() , ResultCode.NEED_LOGIN.getMsg());
		}
		return shippingService.select(user.getId(), shippingId);
	}

	@RequestMapping("list.do")
	@ResponseBody
	public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
										 @RequestParam(value = "pageSize",defaultValue = "10")int pageSize,
										 HttpSession session){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user ==null){
			return ServerResponse.Failure(ResultCode.NEED_LOGIN.getCode(),ResultCode.NEED_LOGIN.getMsg());
		}
		return shippingService.list(user.getId(),pageNum,pageSize);
	}
}
