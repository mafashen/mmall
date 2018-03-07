package com.mmall.vo;

import com.mmall.common.RoleEnum;
import com.mmall.domain.User;
import jodd.bean.BeanCopy;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import org.codehaus.jackson.annotate.JsonIgnore;

@Getter
@Setter
public class UserVO {

	private String username;

	private String email;

	private String phone;

	//private Integer role;

	private String roleStr;

	public static UserVO convert2VO(User user){
		UserVO vo = new UserVO();
		BeanCopy.beans(user , vo).copy();
		vo.setRoleStr(RoleEnum.idOf(user.getRole()).getValue());
		return vo;
	}

	public static void main(String[] args){
		User user = new User();
		user.setPassword("123");
		user.setUsername("213");
		user.setQuestion("111");
		user.setCreateTime(new Date());

		UserVO vo = new UserVO();
		new BeanCopy(user , vo).copy();
		System.out.println(vo.getUsername());
	}
}
