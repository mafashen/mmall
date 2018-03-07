package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResultCode;
import com.mmall.common.RoleEnum;
import com.mmall.common.ServerResponse;
import com.mmall.domain.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.vo.OrderVO;
import java.util.Objects;
import javax.servlet.http.HttpSession;
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

	private ServerResponse checkLoginAndRole(HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			return ServerResponse.Failure(ResultCode.NEED_LOGIN.getCode(),"请登录后操作");
		}else if(!Objects.equals(user.getRole() , RoleEnum.ADMIN.getCode())){
			return ServerResponse.Failure("没有权限");
		}
		return ServerResponse.Success();
	}

	@RequestMapping("list.do")
	public ServerResponse<PageInfo> orderList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
											  @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
		ServerResponse check = checkLoginAndRole(session);
		if (check.isSuccess()){
			return orderService.manageList(pageNum , pageSize);
		}else{
			return check;
		}
	}

	@RequestMapping("detail.do")
	public ServerResponse<OrderVO> orderDetail(HttpSession session, Long orderNo){
		ServerResponse check = checkLoginAndRole(session);
		if (check.isSuccess()){
			return orderService.manageDetail(orderNo);
		}else{
			return check;
		}
	}

	@RequestMapping("search.do")
	public ServerResponse<PageInfo> orderSearch(HttpSession session,
												@RequestParam("orderNo") Long orderNo ,
												@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
											  	@RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
		ServerResponse check = checkLoginAndRole(session);
		if (check.isSuccess()){
			return orderService.manageSearch(orderNo , pageNum , pageSize);
		}else{
			return check;
		}
	}

	@RequestMapping("send_goods.do")
	public ServerResponse<String> orderSearch(HttpSession session, @RequestParam("orderNo") Long orderNo ){
		ServerResponse check = checkLoginAndRole(session);
		if (check.isSuccess()){
			return orderService.manageSendGoods(orderNo);
		}else{
			return check;
		}
	}
}
