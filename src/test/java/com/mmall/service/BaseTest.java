package com.mmall.service;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-context.xml")
public class BaseTest {

	static long s = 0;

	@Before
	public void before() {
		System.out.println("start--->");
		s = System.currentTimeMillis();
	}

	@After
	public void after() {
		System.out.println("end---->" + (System.currentTimeMillis() - s));
	}
}