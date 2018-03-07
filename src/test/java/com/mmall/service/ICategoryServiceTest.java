package com.mmall.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class ICategoryServiceTest extends BaseTest{

	@Autowired
	ICategoryService categoryService;


	@Test
	public void addCategory() throws Exception {
		Assert.assertFalse(categoryService.addCategory(null , null).isSuccess());

		Assert.assertFalse(categoryService.addCategory("test" , -1).isSuccess());

		Assert.assertTrue(categoryService.addCategory("test" , 0).isSuccess());
	}

	@Test
	public void updateCategoryName() throws Exception {
		Assert.assertFalse(categoryService.updateCategoryName( 0 , "test").isSuccess());

		Assert.assertTrue(categoryService.updateCategoryName( 100031 , "测试类目1").isSuccess());
	}

	@Test
	public void getChildrenParallelCategory() throws Exception {
		Assert.assertNull(categoryService.getChildrenParallelCategory(1).getData());

		Assert.assertNotNull(categoryService.getChildrenParallelCategory(0).getData());
	}

	@Test
	public void selectCategoryAndChildrenById() throws Exception {
		Assert.assertEquals(0 ,categoryService.selectCategoryAndChildrenById(1).getData().size());

		Assert.assertEquals( 6 ,categoryService.selectCategoryAndChildrenById(100001).getData().size());
	}

}