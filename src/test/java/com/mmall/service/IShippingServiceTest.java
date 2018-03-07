package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.domain.Shipping;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class IShippingServiceTest extends BaseTest{

	@Autowired
	private IShippingService shippingService;


	@Test
	public void add() throws Exception {

	}

	@Test
	public void del() throws Exception {
	}

	@Test
	public void update() throws Exception {
	}

	@Test
	public void select() throws Exception {
		ServerResponse<Shipping> ret = shippingService.select(13, 4);
		Assert.assertTrue(ret.isSuccess());
	}

	@Test
	public void list() throws Exception {
		ServerResponse<PageInfo> ret = shippingService.list(13, 1 , 5);
		Assert.assertTrue(ret.isSuccess());
		Assert.assertEquals(1 , ret.getData().getSize());
	}

}