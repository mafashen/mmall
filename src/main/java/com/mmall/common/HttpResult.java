package com.mmall.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@Getter
@Setter
/**HTTP接口统一返回此对象*/
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class HttpResult<T> implements Serializable{

	private static final long serialVersionUID = 672916116763084820L;

	private int code;
	private boolean status;
	private String msg;
	private T data;

	public HttpResult() {
	}

	public static HttpResult Success(){
		HttpResult ret = new HttpResult();
		ret.setStatus(true);
		ret.setCode(ResponseCode.SUCCESS.getCode());
		return ret;
	}

	public static HttpResult Success(int code){
		HttpResult ret = new HttpResult();
		ret.setStatus(true);
		ret.setCode(code);
		return ret;
	}

	public static <T> HttpResult<T> Success(T data){
		HttpResult ret = new HttpResult();
		ret.setStatus(true);
		ret.setCode(ResponseCode.SUCCESS.getCode());
		ret.setData(data);
		return ret;
	}

	public static <T> HttpResult<T> Success(int code , T data){
		HttpResult ret = new HttpResult();
		ret.setStatus(true);
		ret.setCode(code);
		ret.setData(data);
		return ret;
	}

	public static <T> HttpResult<T> Success(int code , String msg , T data){
		HttpResult ret = new HttpResult();
		ret.setStatus(true);
		ret.setCode(code);
		ret.setMsg(msg);
		ret.setData(data);
		return ret;
	}

	public static HttpResult Failure(){
		HttpResult ret = new HttpResult();
		ret.setStatus(false);
		ret.setCode(ResponseCode.FAILURE.getCode());
		return ret;
	}

	public static HttpResult Failure(String msg){
		HttpResult ret = new HttpResult();
		ret.setStatus(false);
		ret.setCode(ResponseCode.FAILURE.getCode());
		ret.setMsg(msg);
		return ret;
	}

	public static HttpResult Failure(int code){
		HttpResult ret = new HttpResult();
		ret.setStatus(false);
		ret.setCode(code);
		return ret;
	}

	public static HttpResult Failure(int code , String msg){
		HttpResult ret = new HttpResult();
		ret.setStatus(false);
		ret.setCode(code);
		ret.setMsg(msg);
		return ret;
	}

	public static <T> HttpResult<T> Failure(int code , String msg , T data){
		HttpResult ret = new HttpResult();
		ret.setStatus(false);
		ret.setCode(code);
		ret.setMsg(msg);
		ret.setData(data);
		return ret;
	}

	public boolean isStatus(){
		return status;
	}

	public static HttpResult convert(ServerResponse ret){
		HttpResult httpResult = new HttpResult();
		httpResult.setMsg(ret.getMsg());
		httpResult.setCode(ret.getStatus());
		httpResult.setData(ret.getData());
		httpResult.setStatus(ret.isSuccess());
		return httpResult;
	}
}


