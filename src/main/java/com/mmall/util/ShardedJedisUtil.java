package com.mmall.util;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ShardedJedis;

@Component
public class ShardedJedisUtil {

	@Autowired
	private ShardedJedisTemplate template;

	public void setObject(final String key , Object object ){
		final String data = JSON.toJSONString(object);
		template.execute(new ShardedJedisTemplateCallback<Integer>() {
			@Override
			public Integer doInRedis(ShardedJedis jedis) {
				jedis.set(key, data);
				return 0;
			}
		});
	}

	public void setObject(final String key , Object object , final int expire){
		final String data = JSON.toJSONString(object);
		template.execute(new ShardedJedisTemplateCallback<Integer>() {
			@Override
			public Integer doInRedis(ShardedJedis jedis) {
				jedis.setex(key, expire, data);
				return 0;
			}
		});
	}

	public <T> T getObject(final String key , Class<T> clz){
		String data = template.execute(new ShardedJedisTemplateCallback<String>() {
			@Override
			public String doInRedis(ShardedJedis jedis) {
				return jedis.get(key);
			}
		});
		if (data != null){
			return  JSON.parseObject(data, clz);
		}
		return null;
	}

	public void setEx(){

	}

	public void expire(){

	}

	public void getSet(){

	}

	public void setIfNotExist(){

	}

	public void exist(){

	}
}
