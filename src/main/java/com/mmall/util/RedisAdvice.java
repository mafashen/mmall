package com.mmall.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Component
@Aspect
public class RedisAdvice {

	@Autowired
	private ShardedJedisPool shardedJedisPool;

	@Pointcut("execution(* com.mmall.util.ShardedJedisUtil.*(..))")
	public void point(){}

	@Around("point()")
	public void doInRedis(ProceedingJoinPoint joinPoint){
		ShardedJedis resource = shardedJedisPool.getResource();
		try {
			System.out.println("before");
			joinPoint.proceed();
			System.out.println("after");
		} catch ( Throwable throwable) {
			shardedJedisPool.returnBrokenResource(resource);
		} finally {
			shardedJedisPool.returnResource(resource);
		}
	}
}
