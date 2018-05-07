package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.RoleEnum;
import com.mmall.common.ServerResponse;
import com.mmall.domain.User;
import com.mmall.service.IUserService;
import com.mmall.util.SessionUtil;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/manage/user")
public class UserManageController {

	@Autowired
	private IUserService iUserService;
	@Autowired
	private SessionUtil sessionUtil;

	@RequestMapping(value="login.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> login(String username, String password, HttpSession session , HttpServletResponse response){
		ServerResponse<User> ret = iUserService.login(username,password);
		if(ret.isSuccess()){
			User user = ret.getData();
			if(user.getRole() == RoleEnum.ADMIN.getCode()){
				//说明登录的是管理员
//				session.setAttribute(Const.CURRENT_USER,user);
				sessionUtil.login(response , session , ret.getData());
				return ret;
			}else{
				return ServerResponse.Failure("不是管理员,无法登录");
			}
		}
		return ret;
	}

}
