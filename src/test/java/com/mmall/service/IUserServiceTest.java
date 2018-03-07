package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.domain.User;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class IUserServiceTest extends BaseTest{

	@Autowired
	IUserService userService;


	@Test
	public void login() throws Exception {
		ServerResponse<User> ret1 = userService.login("admin", "admin1");
		Assert.assertFalse(ret1.isSuccess());

		ServerResponse<User> ret2 = userService.login("admin", "admin");
		Assert.assertTrue(ret2.isSuccess());

		ServerResponse<User> ret3 = userService.login("admin1", "admin");
		Assert.assertFalse(ret3.isSuccess());
	}

	@Test
	public void register() throws Exception {
		User user = new User();
		user.setUsername("admin");
		user.setRole(0);
		ServerResponse<String> ret1 = userService.register(user);
		Assert.assertFalse(ret1.isSuccess());

		user.setUsername("zhangsan");
		user.setEmail("admin@happymmall.com");
		ServerResponse<String> ret2 = userService.register(user);
		Assert.assertFalse(ret2.isSuccess());

		user.setEmail("mafashen@qq.com");
		ServerResponse<String> ret3 = userService.register(user);
		Assert.assertTrue(ret3.isSuccess());
	}

	@Test
	public void selectQuestion() throws Exception {
		ServerResponse ret1 = userService.selectQuestion("admin");
		Assert.assertTrue(ret1.isSuccess());

		ServerResponse ret2 = userService.selectQuestion("admin1");
		Assert.assertFalse(ret2.isSuccess());
	}

	@Test
	public void checkAnswer() throws Exception {
		ServerResponse<String> ret1 = userService.checkAnswer("zhangsan", "问题", "答案");
		Assert.assertTrue(ret1.isSuccess());
		System.out.println(ret1.getData());

		ServerResponse<String> ret2 = userService.checkAnswer("zhangsan", "问题", "答案1");
		Assert.assertFalse(ret2.isSuccess());

		ServerResponse<String> ret3 = userService.checkAnswer("zhangsan1", "问题", "答案");
		Assert.assertFalse(ret3.isSuccess());
	}

	@Test
	public void forgetResetPassword() throws Exception {
		ServerResponse<String> ret = userService.checkAnswer("zhangsan", "问题", "答案");
		Assert.assertTrue(ret.isSuccess());
		System.out.println(ret.getData());
		ServerResponse<String> ret1 = userService.forgetResetPassword("zhangsan", "123", ret.getData());
		Assert.assertTrue(ret1.isSuccess());

		ServerResponse<String> ret2 = userService.forgetResetPassword("zhangsan", "123", "8b8380dd-a8c8-4812-92c4-789a2f966d22");
		Assert.assertFalse(ret2.isSuccess());
	}

	@Test
	public void resetPassword() throws Exception {
		User user = new User();
		user.setId(22);

		ServerResponse<String> ret1 = userService.resetPassword("123", "456", user);
		Assert.assertTrue(ret1.isSuccess());

		ServerResponse<String> ret2 = userService.resetPassword("1234", "456", user);
		Assert.assertFalse(ret2.isSuccess());
	}

	@Test
	public void updateInformation() throws Exception {
		User user = new User();
		user.setId(22);
		user.setUsername("lisi");
		user.setPhone("110");
		ServerResponse<User> ret1 = userService.updateInformation(user);
		Assert.assertTrue(ret1.isSuccess());

		user.setEmail("admin@happymmall.com");
		ServerResponse<User> ret2 = userService.updateInformation(user);
		Assert.assertFalse(ret2.isSuccess());
	}

	@Test
	public void getInformation() throws Exception {
		ServerResponse<User> ret1 = userService.getInformation(22);
		Assert.assertTrue(ret1.isSuccess());

		ServerResponse<User> ret2 = userService.getInformation(23);
		Assert.assertFalse(ret2.isSuccess());
	}

}