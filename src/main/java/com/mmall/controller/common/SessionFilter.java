package com.mmall.controller.common;

import com.mmall.util.SessionUtil;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SessionFilter implements Filter{

	private SessionUtil sessionUtil;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-context.xml");
		sessionUtil = (SessionUtil) context.getBean("sessionUtil");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		//登录状态下,每次访问都重新设置redis session有效期
		sessionUtil.refreshLoginExpire((HttpServletRequest) request);
		chain.doFilter(request , response);
	}

	@Override
	public void destroy() {

	}

	public SessionUtil getSessionUtil() {
		return sessionUtil;
	}

	@Autowired
	public void setSessionUtil(SessionUtil sessionUtil) {
		this.sessionUtil = sessionUtil;
	}
}
