package com.mmall.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

	public class CorsIntercept extends HandlerInterceptorAdapter{


		@Override
		public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
			response.addHeader("Access-Control-Allow-Origin" , "*");
			response.addHeader("Access-Control-Allow-Headers", "Authentication");
			response.addHeader("Access-Control-Allow-Methods","POST,GET,HEAD");
			response.addHeader("Access-Control-Max-Age","3600");
			response.addHeader("Access-Control-Allow-Credentials","true");
			return super.preHandle(request, response, handler);
		}
	}
