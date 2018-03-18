package com.mmall.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public class CookieUtil {

	/**
	 * cookie作用域, 此处设置为一级域名,则二级域名下可以共享cookie
	 */
	private static final String COOKIE_DOMAIN = ".happymmall.com";
	private static final String LOGIN_COOKIE_NAME = "mmall_login_cookie";
	/**
	 * cookie的有效路径
	 */
	private static final String LOGIN_COOKIE_PATH = "/";
	/**
	 * cookie有效期7天
	 */
	private static final int LOGIN_COOKIE_MAX_AGE = 60 * 60 * 24 * 7;


	public static void addCookie(HttpServletResponse response , String key , String value , String path , int maxAge){
		Cookie cookie = new Cookie(key , value);
		cookie.setDomain(COOKIE_DOMAIN);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(maxAge);
		cookie.setPath(path);
		response.addCookie(cookie);
	}

	public static void setLoginCookie(HttpServletResponse response , String value){
		addCookie(response , LOGIN_COOKIE_NAME , value , LOGIN_COOKIE_PATH , LOGIN_COOKIE_MAX_AGE);
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

	public static String getLoginCookie(HttpServletRequest request){
		return getCookie(request, LOGIN_COOKIE_NAME);
	}

	public static void deleteCookie(HttpServletResponse response , String key){
		//加入一个有效期为0的代表删除
		addCookie(response , key , null , LOGIN_COOKIE_PATH , 0);
	}

	public static void deleteLoginCookie(HttpServletResponse response){
		deleteCookie(response , LOGIN_COOKIE_NAME);
	}
}
