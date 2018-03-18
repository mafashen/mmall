package com.mmall.controller.portal;

import com.mmall.common.ServerResponse;
import com.mmall.common.ResponseCode;
import com.mmall.domain.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.KvCacheManage;
import com.mmall.util.SessionUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

	private static final int LOGIN_EXPIRE = 30 * 60;

	@Autowired
	private IUserService userService;
	@Autowired
	private KvCacheManage kvCacheManage;
	@Autowired
	private SessionUtil sessionUtil;

	@RequestMapping(value = "/login.do")
	public ServerResponse<User> login(@RequestParam("username") String username ,
									  @RequestParam("password") String password ,
									  HttpSession session , HttpServletResponse response){
		ServerResponse<User> ret = userService.login(username, password);
		if (ret.isSuccess()){
//			session.setAttribute(Const.CURRENT_USER , ret.getData());
			sessionUtil.setLoginCookie(response , session.getId());
			kvCacheManage.setObject(session.getId() , ret.getData() , LOGIN_EXPIRE);
		}
		return ret;
	}

	@RequestMapping("/logout.do")
	public ServerResponse logout(HttpServletRequest request , HttpServletResponse response){
//		session.removeAttribute(Const.CURRENT_USER);
		String loginToken = sessionUtil.getLoginCookie(request);
		kvCacheManage.del(loginToken);
		sessionUtil.deleteLoginCookie(response);
		return ServerResponse.Success();
	}

	@RequestMapping(value = "/register.do" , method = RequestMethod.POST)
	public ServerResponse<String> register(@Valid User user , BindingResult bindingResult){
		if (bindingResult.hasErrors()){
			return ServerResponse.Failure(bindingResult.getFieldError().toString());
		}
		return userService.register(user);
	}

	@RequestMapping(value = "get_user_info.do")
	@ResponseBody
	public ServerResponse<User> getUserInfo(HttpSession session , HttpServletRequest request){
//		User user = (User) session.getAttribute(Const.CURRENT_USER);
		User user = sessionUtil.checkLogin(request);
		if (user != null){
			return ServerResponse.Success(user);
		}
		return ServerResponse.Failure("用户未登录,无法获取当前用户的信息");
	}


	@RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetGetQuestion(HttpSession session , String username){
		return userService.selectQuestion(username);
	}


	@RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
		return userService.checkAnswer(username,question,answer);
	}


	@RequestMapping(value = "forget_reset_password.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
		return userService.forgetResetPassword(username,passwordNew,forgetToken);
	}



	@RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> resetPassword(HttpServletRequest request,String passwordOld,String passwordNew){
		User user = sessionUtil.checkLogin(request);
		if(user == null){
			return ServerResponse.Failure("用户未登录");
		}
		return userService.resetPassword(passwordOld,passwordNew,user);
	}


	@RequestMapping(value = "update_information.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> update_information(HttpServletRequest request,User user){
		User currentUser = sessionUtil.checkLogin(request);
		String loginCookie = sessionUtil.getLoginCookie(request);
		if(currentUser == null){
			return ServerResponse.Failure("用户未登录");
		}
		user.setId(currentUser.getId());
		user.setUsername(currentUser.getUsername());
		ServerResponse<User> response = userService.updateInformation(user);
		if(response.isSuccess()){
			response.getData().setUsername(currentUser.getUsername());
			//session.setAttribute(Const.CURRENT_USER,response.getData());
			kvCacheManage.setObject(loginCookie , user , LOGIN_EXPIRE);
		}
		return response;
	}

	@RequestMapping(value = "get_information.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> get_information(HttpServletRequest request){
		User currentUser = sessionUtil.checkLogin(request);
		if(currentUser == null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode(),"未登录,需要强制登录status=10");
		}
		return userService.getInformation(currentUser.getId());
	}
}
