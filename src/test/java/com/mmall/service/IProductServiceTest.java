package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.domain.Product;
import com.mmall.vo.ProductDetailVo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class IProductServiceTest extends BaseTest{

	@Autowired
	private IProductService productService;


	@Test
	public void saveOrUpdateProduct() throws Exception {
		Product product = new Product();
		product.setId(26);
		product.setStock(9999);
		ServerResponse ret = productService.saveOrUpdateProduct(product);
		Assert.assertTrue(ret.isSuccess());
	}

	@Test
	public void setSaleStatus() throws Exception {
	}

	@Test
	public void manageProductDetail() throws Exception {
	}

	@Test
	public void getProductList() throws Exception {
	}

	@Test
	public void searchProduct() throws Exception {
		ServerResponse<PageInfo> page = productService.searchProduct("iphone", null, 1, 10);
		Assert.assertTrue(page.isSuccess());
		Assert.assertEquals(1,page.getData().getSize());
	}

	@Test
	public void getProductDetail() throws Exception {
		ServerResponse<ProductDetailVo> ret = productService.getProductDetail(26);
		Assert.assertTrue(ret.isSuccess());
		Assert.assertEquals(new Integer(1) , ret.getData().getStatus());
	}

	@Test
	public void getProductByKeywordCategory() throws Exception {
	}

}