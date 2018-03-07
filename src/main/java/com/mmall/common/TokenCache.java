package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * token缓存工具类
 */
@Service
public class TokenCache {

	private Logger logger = LoggerFactory.getLogger(TokenCache.class);

	private LoadingCache<String , String> cache = CacheBuilder.newBuilder()
			.initialCapacity(1000)
			.maximumSize(10000)
			.expireAfterAccess(1, TimeUnit.HOURS)
			.build(new CacheLoader() {

				@Override
				public Object load(Object o) throws Exception {
					return "null";
				}
			});

	public void setCache(String key , String value){
		cache.put(key , value);
	}

	public String getCache(String key){
		String value = null;
		try{
			value = cache.get(key);
			if ("null".equals(value)){
				return null;
			}
		}catch (Exception e ){
			logger.error("get cache failed : {}" , e.getMessage());
		}
		return value;
	}
}
