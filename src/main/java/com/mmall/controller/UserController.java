package com.mmall.controller;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.ResultCode;
import com.mmall.domain.User;
import com.mmall.service.IUserService;
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

	@Autowired
	private IUserService userService;

	@RequestMapping(value = "/login.do")
	public ServerResponse<User> login(@RequestParam("username") String username ,
								  @RequestParam("password") String password ,
								  HttpSession session){
		ServerResponse<User> ret = userService.login(username, password);
		if (ret.isSuccess()){
			session.setAttribute(Const.CURRENT_USER , ret.getData());
		}
		return ret;
	}

	@RequestMapping("/logout.do")
	public ServerResponse logout(HttpSession session){
		session.removeAttribute(Const.CURRENT_USER);
		return ServerResponse.Success();
	}

	@RequestMapping(value = "/register.do" , method = RequestMethod.POST)
	public ServerResponse<String> register(@Valid User user , BindingResult bindingResult){
		if (bindingResult.hasErrors()){
			return ServerResponse.Failure(bindingResult.getFieldError().toString());
		}
		return userService.register(user);
	}

	@RequestMapping(value = "get_user_info.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> getUserInfo(HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user != null){
			return ServerResponse.Success(user);
		}
		return ServerResponse.Failure("用户未登录,无法获取当前用户的信息");
	}


	@RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetGetQuestion(HttpSession session , String username){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			return ServerResponse.Failure(ResultCode.NEED_LOGIN.getCode(),"请登录后操作");
		}
		return userService.selectQuestion(user.getUsername());
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
	public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.Failure("用户未登录");
		}
		return userService.resetPassword(passwordOld,passwordNew,user);
	}


	@RequestMapping(value = "update_information.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> update_information(HttpSession session,User user){
		User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
		if(currentUser == null){
			return ServerResponse.Failure("用户未登录");
		}
		user.setId(currentUser.getId());
		user.setUsername(currentUser.getUsername());
		ServerResponse<User> response = userService.updateInformation(user);
		if(response.isSuccess()){
			response.getData().setUsername(currentUser.getUsername());
			session.setAttribute(Const.CURRENT_USER,response.getData());
		}
		return response;
	}

	@RequestMapping(value = "get_information.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> get_information(HttpSession session){
		User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
		if(currentUser == null){
			return ServerResponse.Failure(ResultCode.NEED_LOGIN.getCode(),"未登录,需要强制登录status=10");
		}
		return userService.getInformation(currentUser.getId());
	}
}
