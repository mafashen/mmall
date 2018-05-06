package com.mmall.controller.common;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

@Component
@Slf4j
public class ExceptionHandler extends AbstractHandlerExceptionResolver {

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse httpServletResponse, Object o, Exception e) {
		StringBuilder sb = new StringBuilder();
		Map<String, String[]> parameterMap = request.getParameterMap();
		if (parameterMap != null && !parameterMap.isEmpty()){
			for (String key : parameterMap.keySet()) {
				sb.append(key);
				sb.append(" = ");
				for (String value : parameterMap.get(key)) {
					sb.append(value);
					sb.append(",");
				}
			}
		}
		log.error("异常拦截器捕获到异常.uri:{},param:{}" , request.getRequestURI() , sb.toString());
		ModelAndView mv = new ModelAndView();
		MappingJacksonJsonView mjjv = new MappingJacksonJsonView();
		Map<String, String> attrMap = new HashMap<>();
		attrMap.put("status", "1");
		attrMap.put("msg", e.getMessage());
		mjjv.setAttributesMap(attrMap);
		mv.setView(mjjv);
		return mv;
	}
}
