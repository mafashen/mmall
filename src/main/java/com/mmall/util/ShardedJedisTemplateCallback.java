package com.mmall.util;

import redis.clients.jedis.ShardedJedis;

public interface ShardedJedisTemplateCallback <T>{
	T doInRedis(ShardedJedis jedis);
}
