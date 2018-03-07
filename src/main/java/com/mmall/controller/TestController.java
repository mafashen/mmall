package com.mmall.controller;

import com.mmall.common.HttpResult;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

	@RequestMapping("/test")
	public String test(){
		return "test";
	}

	@RequestMapping(value = "/ply" , method = RequestMethod.POST)
	public HttpResult ply(@RequestParam(value = "polygon") String polygon ,
						  @RequestParam(value = "cityName") String cityName){
		System.out.println(polygon);
		return HttpResult.Success(polygon);
	}
}
