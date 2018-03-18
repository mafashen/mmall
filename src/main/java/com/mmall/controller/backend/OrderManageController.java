package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ResponseCode;
import com.mmall.common.RoleEnum;
import com.mmall.common.ServerResponse;
import com.mmall.domain.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.util.SessionUtil;
import com.mmall.vo.OrderVO;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manage/order/")
public class OrderManageController {

	@Autowired
	private IOrderService orderService;
	@Autowired
	private IUserService userService;
	@Autowired
	private SessionUtil sessionUtil;

	private ServerResponse checkLoginAndRole(HttpServletRequest request){
		User user = sessionUtil.checkLogin(request);
		if (user == null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode(),"请登录后操作");
		}else if(!Objects.equals(user.getRole() , RoleEnum.ADMIN.getCode())){
			return ServerResponse.Failure("没有权限");
		}
		return ServerResponse.Success();
	}

	@RequestMapping("list.do")
	public ServerResponse<PageInfo> orderList(HttpServletRequest request, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
											  @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
		ServerResponse check = checkLoginAndRole(request);
		if (check.isSuccess()){
			return orderService.manageList(pageNum , pageSize);
		}else{
			return check;
		}
	}

	@RequestMapping("detail.do")
	public ServerResponse<OrderVO> orderDetail(HttpServletRequest request , Long orderNo){
		ServerResponse check = checkLoginAndRole(request);
		if (check.isSuccess()){
			return orderService.manageDetail(orderNo);
		}else{
			return check;
		}
	}

	@RequestMapping("search.do")
	public ServerResponse<PageInfo> orderSearch(HttpServletRequest request ,
												@RequestParam("orderNo") Long orderNo ,
												@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
											  	@RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
		ServerResponse check = checkLoginAndRole(request);
		if (check.isSuccess()){
			return orderService.manageSearch(orderNo , pageNum , pageSize);
		}else{
			return check;
		}
	}

	@RequestMapping("send_goods.do")
	public ServerResponse<String> orderSearch(HttpServletRequest request , @RequestParam("orderNo") Long orderNo ){
		ServerResponse check = checkLoginAndRole(request);
		if (check.isSuccess()){
			return orderService.manageSendGoods(orderNo);
		}else{
			return check;
		}
	}
}
