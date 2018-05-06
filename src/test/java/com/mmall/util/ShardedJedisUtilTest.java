package com.mmall.util;

import com.mmall.service.BaseTest;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class ShardedJedisUtilTest extends BaseTest{

	@Autowired
	private ShardedJedisUtil shardedJedisUtil;


	@Test
	public void setObject() throws Exception {
		for (int i = 0; i < 10; i++) {
			shardedJedisUtil.setObject("key"+i , "value"+i);
		}
	}

	@Test
	public void getObject() throws Exception {
		String value = shardedJedisUtil.getObject("key1", String.class);
		Assert.assertNotNull(value);
		Map<String ,String> map = new HashMap<>();
		map.put("k1", "v1");
		shardedJedisUtil.setObject("key2" , map);
		map = shardedJedisUtil.getObject("key2", Map.class);
		Assert.assertNotNull(map);
		System.out.println(map.get("k1"));
	}

}