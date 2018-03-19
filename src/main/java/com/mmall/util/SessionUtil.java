package com.mmall.util;

import com.mmall.domain.User;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class SessionUtil {

	@Autowired
	private KvCacheManage kvCacheManage;

	private SessionUtil sessionUtil;

	private static final String LOGIN_COOKIE_NAME = "mmall_login_cookie";

	/**
	 * cookie有效期7天
	 */
	private static final int LOGIN_COOKIE_MAX_AGE = 60 * 60 * 24 * 7;
	private static final int LOGIN_CACHE_MAX_AGE = 30 * 60;


	@PostConstruct
	public void init(){
		sessionUtil = this;
		sessionUtil.kvCacheManage = this.kvCacheManage;
	}

	public void setLoginCookie(HttpServletResponse response , String value){
		CookieUtil.addCookie(response , LOGIN_COOKIE_NAME , value , LOGIN_COOKIE_MAX_AGE);
	}

	public void setLoginCache(String key , User user){
		kvCacheManage.setObject(key , user , LOGIN_CACHE_MAX_AGE);
	}

	public String getLoginCookie(HttpServletRequest request){
		return CookieUtil.getCookie(request, LOGIN_COOKIE_NAME);
	}

	public void deleteLoginCookie(HttpServletResponse response){
		CookieUtil.deleteCookie(response , LOGIN_COOKIE_NAME);
	}

	private void deleteLoginCache(String key){
		kvCacheManage.del(key);
	}

	public void login(HttpServletResponse response , HttpSession session , User user){
		setLoginCookie(response , session.getId());
		kvCacheManage.setObject(session.getId() , user , LOGIN_CACHE_MAX_AGE);
	}

	public void logout(HttpServletRequest request , HttpServletResponse response){
		String loginCookie = getLoginCookie(request);
		if (StringUtils.isNotBlank(loginCookie)){
			deleteLoginCache(loginCookie);
			deleteLoginCookie(response);
		}
	}

	public User checkLogin(HttpServletRequest request){
		String loginCookie = getLoginCookie(request);
		if (StringUtils.isNotBlank(loginCookie)){
			User user = kvCacheManage.getObject(loginCookie, User.class);
			if(user != null){
				return user;
			}
		}
		return null;
	}

	public void refreshLoginExpire(HttpServletRequest request){
		String loginCookie = getLoginCookie(request);
		if (StringUtils.isNotBlank(loginCookie)){
			User user = kvCacheManage.getObject(loginCookie, User.class);
			if(user != null){
				kvCacheManage.expire(loginCookie, LOGIN_CACHE_MAX_AGE);
			}
		}
	}

	@Autowired
	public void setKvCacheManage(KvCacheManage kvCacheManage) {
		this.kvCacheManage = kvCacheManage;
	}
}
