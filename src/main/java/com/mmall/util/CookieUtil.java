package com.mmall.util;

import com.mmall.domain.User;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CookieUtil {

	/**
	 * cookie作用域, 此处设置为一级域名,则二级域名下可以共享cookie
	 */
	private static final String COOKIE_DOMAIN = ".happymmall.com";
	/**
	 * cookie的有效路径
	 */
	private static final String COOKIE_PATH = "/";

	public static void addCookie(HttpServletResponse response , String key , String value , int maxAge){
		Cookie cookie = new Cookie(key , value);
		cookie.setDomain(COOKIE_DOMAIN);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(maxAge);
		cookie.setPath(COOKIE_PATH);
		response.addCookie(cookie);
	}

	public static String getCookie(HttpServletRequest request , String key){
		Cookie[] cookies = request.getCookies();
		if (cookies != null ){
			for (Cookie cookie : cookies) {
				if (StringUtils.equals(key , cookie.getName())){
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	public static void deleteCookie(HttpServletResponse response , String key){
		//加入一个有效期为0的代表删除
		addCookie(response , key , null , 0);
	}

}
