package com.mmall.common;

import lombok.Getter;

@Getter
public enum ResponseCode {
	SUCCESS(0 , "成功"),
	FAILURE(1 , "失败"),
	PARAM_ERROR(3 , "参数错误"),
	NEED_LOGIN(10 , "需要登录"),
	;

	int code ;
	String msg ;

	ResponseCode(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
