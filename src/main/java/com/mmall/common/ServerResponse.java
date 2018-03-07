package com.mmall.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@Getter
@Setter
/**服务层统一返回*/
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)	//null的值不会被序列化
public class ServerResponse<T> implements Serializable{

	private static final long serialVersionUID = 672916116763084820L;

	private int code;
	private boolean success;
	private String msg;
	private T data;

	public ServerResponse() {
	}

	public static ServerResponse Success(){
		ServerResponse ret = new ServerResponse();
		ret.setSuccess(true);
		ret.setCode(ResultCode.SUCCESS.getCode());
		return ret;
	}

	public static ServerResponse Success(int code){
		ServerResponse ret = new ServerResponse();
		ret.setSuccess(true);
		ret.setCode(code);
		return ret;
	}

	public static <T> ServerResponse<T> Success(T data){
		ServerResponse ret = new ServerResponse();
		ret.setSuccess(true);
		ret.setCode(ResultCode.SUCCESS.getCode());
		ret.setData(data);
		return ret;
	}

	public static <T> ServerResponse<T> Success(int code , T data){
		ServerResponse ret = new ServerResponse();
		ret.setSuccess(true);
		ret.setCode(code);
		ret.setData(data);
		return ret;
	}

	public static <T> ServerResponse<T> Success(String msg , T data){
		ServerResponse ret = new ServerResponse();
		ret.setSuccess(true);
		ret.setMsg(msg);
		ret.setData(data);
		return ret;
	}

	public static <T> ServerResponse<T> Success(int code , String msg , T data){
		ServerResponse ret = new ServerResponse();
		ret.setSuccess(true);
		ret.setCode(code);
		ret.setMsg(msg);
		ret.setData(data);
		return ret;
	}

	public static ServerResponse Failure(){
		ServerResponse ret = new ServerResponse();
		ret.setSuccess(false);
		ret.setCode(ResultCode.FAILURE.getCode());
		return ret;
	}

	public static ServerResponse Failure(String msg){
		ServerResponse ret = new ServerResponse();
		ret.setSuccess(false);
		ret.setCode(ResultCode.FAILURE.getCode());
		ret.setMsg(msg);
		return ret;
	}

	public static ServerResponse Failure(int code){
		ServerResponse ret = new ServerResponse();
		ret.setSuccess(false);
		ret.setCode(code);
		return ret;
	}

	public static ServerResponse Failure(int code , String msg){
		ServerResponse ret = new ServerResponse();
		ret.setSuccess(false);
		ret.setCode(code);
		ret.setMsg(msg);
		return ret;
	}

	public static <T> ServerResponse<T> Failure(int code , String msg , T data){
		ServerResponse ret = new ServerResponse();
		ret.setSuccess(false);
		ret.setCode(code);
		ret.setMsg(msg);
		ret.setData(data);
		return ret;
	}

	public boolean isSuccess(){
		return success;
	}
	
}
