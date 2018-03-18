package com.mmall.util;

import com.mmall.service.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class KvCacheManageTest extends BaseTest {

	@Autowired
	private KvCacheManage kvCacheManage;


	@Test
	public void setObject() throws Exception {
		kvCacheManage.setObject("key1" , "value1");
	}

	@Test
	public void getObject() throws Exception {
		String key1 = kvCacheManage.getObject("key1", String.class);
		Assert.assertNotNull(key1);
		System.out.println(key1);
	}

	@Test
	public void getObject1() throws Exception {

	}

	@Test
	public void setObject1() throws Exception {
		kvCacheManage.setObject("key2" , "value2" , 60*1000);
	}

	@Test
	public void setString() throws Exception {
	}

	@Test
	public void setLong() throws Exception {
	}

	@Test
	public void getString() throws Exception {
	}

	@Test
	public void inc() throws Exception {
	}

	@Test
	public void inc1() throws Exception {
	}

	@Test
	public void getLong() throws Exception {
	}

	@Test
	public void expire() throws Exception {
	}

	@Test
	public void decr() throws Exception {
	}

	@Test
	public void decr1() throws Exception {
	}

	@Test
	public void del() throws Exception {
	}

	@Test
	public void del1() throws Exception {
	}

	@Test
	public void batchGetLong() throws Exception {
	}

	@Test
	public void exists() throws Exception {
		boolean exists = kvCacheManage.exists("key1");
		Assert.assertTrue(exists);
	}

	@Test
	public void sadd() throws Exception {
	}

	@Test
	public void getSmembers() throws Exception {
	}

	@Test
	public void sPop() throws Exception {
	}

	@Test
	public void sRem() throws Exception {
	}

	@Test
	public void setIfNotExists() throws Exception {
		Boolean suc = kvCacheManage.setIfNotExists("key1", 1000);
		Assert.assertFalse(suc);
	}

}