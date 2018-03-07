package com.mmall.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {

	private Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

	private static Properties props;

	static {
		props = new Properties();
		try {
			String fileName = "mmall.properties";
			props.load(new InputStreamReader(
					PropertiesUtil.class.getClassLoader().
							getResourceAsStream(fileName) , "utf-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getProperty(String key){
		String value  = props.getProperty(key);
		return StringUtils.isBlank(value) ? null : value.trim();
	}

	public static String getProperty(String key , String defaultValue){
		String value  = props.getProperty(key);
		return StringUtils.isBlank(value) ? defaultValue : value.trim();
	}
}
