package com.mmall.service;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVO;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class ICartServiceTest extends BaseTest{

	@Autowired
	private ICartService cartService;


	@Test
	public void add() throws Exception {
		ServerResponse<CartVO> ret = cartService.add(21, 27, 2);
		Assert.assertTrue(ret.isSuccess());
		Assert.assertEquals(1 , ret.getData().getCartProductVoList().size());
	}

	@Test
	public void update() throws Exception {
		ServerResponse<CartVO> ret = cartService.update(21, 27, 8888);
		Assert.assertTrue(ret.isSuccess());
		Assert.assertEquals(new Integer(8876), ret.getData().getCartProductVoList().get(1).getQuantity());
	}

	@Test
	public void deleteProduct() throws Exception {
		cartService.deleteProduct(21 , Arrays.asList(27));
	}

	@Test
	public void list() throws Exception {
		ServerResponse<CartVO> ret = cartService.list(21);
		Assert.assertTrue(ret.isSuccess());
		Assert.assertEquals(3, ret.getData().getCartProductVoList().size());
		System.out.println(ret.getData().getCartTotalPrice());
	}

	@Test
	public void selectOrUnSelect() throws Exception {
		ServerResponse<CartVO> ret = cartService.selectOrUnSelect(21, 28, Const.Cart.UNCHECK);
		Assert.assertTrue(ret.isSuccess());
		Assert.assertEquals(3 , ret.getData().getCartProductVoList().size());
	}

	@Test
	public void getCartProductCount() throws Exception {
		ServerResponse<Integer> ret1 = cartService.getCartProductCount(null);
		Assert.assertEquals( new Integer(0) , ret1.getData());

		ServerResponse<Integer> ret2 = cartService.getCartProductCount(21);
		Assert.assertEquals( new Integer(9) , ret2.getData());
	}

}