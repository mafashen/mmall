package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.common.RoleEnum;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.domain.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("UserService")
public class UserServiceImpl implements IUserService {

	private static final String TOKEN_CACHE_PREFIX = "token:";

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private TokenCache tokenCache;

	@Override
	public ServerResponse<User> login(String username, String password) {
		if (!userMapper.checkNameExist(username)){
			return ServerResponse.Failure("用户名不存在");
		}
		String pwd = MD5Util.MD5EncodeUtf8(password);
		User user = userMapper.selectByNameAndPwd(username, pwd);
		if (user == null){
			return ServerResponse.Failure("密码错误");
		}
		return ServerResponse.Success(user);
	}

	@Override
	public ServerResponse<String> register(User user) {
		if (userMapper.checkNameExist(user.getUsername())){
			return ServerResponse.Failure("用户名已存在");
		}
		if(userMapper.checkEmailExist(user.getEmail())){
			return ServerResponse.Failure("Email已存在");
		}
		user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
		int insert = userMapper.insert(user);
		if (insert > 0 )
		 	return ServerResponse.Success(user.getUsername());
		return ServerResponse.Failure("注册失败！");
	}

	@Override
	public ServerResponse<String> checkValid(String str, String type) {
		return null;
	}

	@Override
	public ServerResponse<String> selectQuestion(String username) {
		if (!userMapper.checkNameExist(username)){
			return ServerResponse.Failure("用户名不存在");
		}
		String question = userMapper.selectQuestionByUserName(username);
		if (question == null){
			return ServerResponse.Failure("没有设置找回密码的问题");
		}
		return ServerResponse.Success(question);
	}

	@Override
	public ServerResponse<String> checkAnswer(String username, String question, String answer) {
		boolean right = userMapper.checkAnswer(username, question, answer);
		if (right){
			//设置一个token，重置密码时检验token，防止越权
			String forgetToken = UUID.randomUUID().toString();
			tokenCache.setCache(TOKEN_CACHE_PREFIX+username , forgetToken);
			return ServerResponse.Success(forgetToken);
		}
		return ServerResponse.Failure("答案错误！");
	}

	@Override
	public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
		if(StringUtils.isBlank(forgetToken)){
			return ServerResponse.Failure("参数错误,需要token");
		}
		if (!userMapper.checkNameExist(username)){
			return ServerResponse.Failure("用户名不存在");
		}
		String cacheToken = tokenCache.getCache(TOKEN_CACHE_PREFIX + username);
		if (StringUtils.isBlank(cacheToken)){
			return ServerResponse.Failure("token无效");
		}
		if (StringUtils.equals(forgetToken , cacheToken)){
			String md5Pwd = MD5Util.MD5EncodeUtf8(passwordNew);
			boolean ret = userMapper.updatePasswordByUsername(username, md5Pwd);
			if (ret ){
				return ServerResponse.Success("更改密码成功");
			}
		}else{
			return ServerResponse.Failure("token错误");
		}
		return ServerResponse.Failure("更改密码失败");
	}

	@Override
	public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
		//防止横向越权，先检验用户id和密码
		boolean ret = userMapper.checkPassword(user.getId(), MD5Util.MD5EncodeUtf8(passwordOld));
		if (!ret){
			return ServerResponse.Failure("旧密码错误");
		}
		user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
		int update = userMapper.updateByPrimaryKeySelective(user);
		if (update > 0){
			return ServerResponse.Success("更改密码成功");
		}
		return ServerResponse.Failure("更改密码失败");
	}

	@Override
	public ServerResponse<User> updateInformation(User user) {
		//不能修改用户名
		user.setUsername(null);
		//更新前检查用户邮箱唯一
		boolean emailExist = userMapper.checkEmailById(user.getEmail(), user.getId());
		if (emailExist){
			return ServerResponse.Failure("该邮箱已被绑定");
		}
		int updateCount = userMapper.updateByPrimaryKeySelective(user);
		if (updateCount > 0){
			return ServerResponse.Success("个人信息更新成功" , (user));
		}
		return ServerResponse.Failure("个人信息更改失败");
	}

	@Override
	public ServerResponse<User> getInformation(Integer userId) {
		User user = userMapper.selectByPrimaryKey(userId);
		if (user == null){
			return ServerResponse.Failure("找不到用户");
		}
		return ServerResponse.Success((user));
	}

	@Override
	public ServerResponse checkAdminRole(User user) {
		ServerResponse ServerResponse = new ServerResponse();
		ServerResponse.setSuccess(Objects.equals(RoleEnum.ADMIN.getCode(), user.getRole()));
		return ServerResponse;
	}
}
