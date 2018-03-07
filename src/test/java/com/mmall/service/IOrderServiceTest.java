package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class IOrderServiceTest extends BaseTest{

	@Autowired
	private IOrderService orderService;


	@Test
	public void pay() throws Exception {
		ServerResponse ret = orderService.pay(1491753014256L, 1, "");
		Assert.assertTrue(ret.isSuccess());
	}

	@Test
	public void alipayCallBack() throws Exception {
	}

	@Test
	public void queryOrderPayStatus() throws Exception {
	}

	@Test
	public void createOrder() throws Exception {
		ServerResponse ret = orderService.createOrder(21, 30);
		Assert.assertTrue(ret.isSuccess());
		Assert.assertNotNull(ret.getData());
		Assert.assertNull(ret.getMsg());

		OrderVO vo = (OrderVO) ret.getData();
		System.out.println(vo.getOrderNo());
		System.out.println(vo.getOrderItemVoList().size());
	}

	@Test
	public void cancel() throws Exception {
		ServerResponse<String> ret = orderService.cancel(21, 1520002591425L);
		Assert.assertTrue(ret.isSuccess());
	}

	@Test
	public void getOrderCartProduct() throws Exception {
		ServerResponse ret = orderService.getOrderCartProduct(21);
		Assert.assertFalse(ret.isSuccess());
		Assert.assertNotNull(ret.getMsg());
	}

	@Test
	public void getOrderDetail() throws Exception {
		ServerResponse<OrderVO> ret = orderService.getOrderDetail(21, 1520002591425L);
		Assert.assertTrue(ret.isSuccess());
		Assert.assertEquals(1 , ret.getData().getOrderItemVoList().size());
	}

	@Test
	public void getOrderList() throws Exception {
		ServerResponse<PageInfo> ret = orderService.getOrderList(21, 1, 5);
		Assert.assertTrue(ret.isSuccess());
		Assert.assertNull(ret.getMsg());
	}

	@Test
	public void manageList() throws Exception {
	}

	@Test
	public void manageDetail() throws Exception {
	}

	@Test
	public void manageSearch() throws Exception {
	}

	@Test
	public void manageSendGoods() throws Exception {
		ServerResponse<String> ret1 = orderService.manageSendGoods(1520002591425L);
		Assert.assertFalse(ret1.isSuccess());

		ServerResponse<String> ret2 = orderService.manageSendGoods(1492091141269L);
		Assert.assertTrue(ret2.isSuccess());
	}

}