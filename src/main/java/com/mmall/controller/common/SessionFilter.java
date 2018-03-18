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

public class SessionFilter implements Filter{

	@Autowired
	private SessionUtil sessionUtil;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

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
}
