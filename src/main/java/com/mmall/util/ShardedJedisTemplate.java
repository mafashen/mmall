package com.mmall.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Component
public class ShardedJedisTemplate {

	@Autowired
	private ShardedJedisPool shardedJedisPool;

	public String execute(ShardedJedisTemplateCallback action){
		ShardedJedis resource = shardedJedisPool.getResource();
		try {
			return action.doInRedis(resource).toString();
		} catch (Exception e) {
			shardedJedisPool.returnBrokenResource(resource);
		}finally {
			shardedJedisPool.returnResource(resource);
		}
		return null;
	}

}
