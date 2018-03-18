package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.domain.Shipping;
import com.mmall.domain.User;
import com.mmall.service.IShippingService;
import com.mmall.util.SessionUtil;
import javax.servlet.http.HttpServletRequest;
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
	@Autowired
	private SessionUtil sessionUtil;

	@RequestMapping("add.do")
	public ServerResponse add(HttpServletRequest request , Shipping shipping){
		User user = sessionUtil.checkLogin(request);
		if (user == null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getMsg());
		}
		return shippingService.add(user.getId() , shipping);
	}

	@RequestMapping("update.do")
	@ResponseBody
	public ServerResponse update(HttpServletRequest request,Shipping shipping){
		User user = sessionUtil.checkLogin(request);
		if(user ==null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMsg());
		}
		return shippingService.update(user.getId(),shipping);
	}

	@RequestMapping("del.do")
	public ServerResponse delete(HttpServletRequest request , Integer shippingId){
		User user = sessionUtil.checkLogin(request);
		if (user == null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getMsg());
		}
		return shippingService.del(user.getId() , shippingId);
	}

	@RequestMapping("select.do")
	public ServerResponse<Shipping> select(HttpServletRequest request , Integer shippingId){
		User user = sessionUtil.checkLogin(request);
		if (user == null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode() , ResponseCode.NEED_LOGIN.getMsg());
		}
		return shippingService.select(user.getId(), shippingId);
	}

	@RequestMapping("list.do")
	@ResponseBody
	public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
										 @RequestParam(value = "pageSize",defaultValue = "10")int pageSize,
										 HttpServletRequest request){
		User user = sessionUtil.checkLogin(request);
		if(user ==null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getMsg());
		}
		return shippingService.list(user.getId(),pageNum,pageSize);
	}
}
