package com.mmall.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@Getter
@Setter
/**服务层统一返回*/
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)	//null的值不会被序列化
public class ServerResponse<T> implements Serializable {

	private int status;
	private String msg;
	private T data;

	public ServerResponse(){

	}

	private ServerResponse(int status){
		this.status = status;
	}
	private ServerResponse(int status,T data){
		this.status = status;
		this.data = data;
	}

	private ServerResponse(int status,String msg,T data){
		this.status = status;
		this.msg = msg;
		this.data = data;
	}

	private ServerResponse(int status,String msg){
		this.status = status;
		this.msg = msg;
	}

	@JsonIgnore
	//使之不在json序列化结果当中
	public boolean isSuccess(){
		return this.status == ResponseCode.SUCCESS.getCode();
	}

	public int getStatus(){
		return status;
	}
	public T getData(){
		return data;
	}
	public String getMsg(){
		return msg;
	}


	public static <T> ServerResponse<T> Success(){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
	}

	public static <T> ServerResponse<T> Success(String msg){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
	}

	public static <T> ServerResponse<T> Success(T data){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
	}

	public static <T> ServerResponse<T> Success(String msg,T data){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
	}


	public static <T> ServerResponse<T> Failure(){
		return new ServerResponse<T>(ResponseCode.FAILURE.getCode(),ResponseCode.FAILURE.getMsg());
	}


	public static <T> ServerResponse<T> Failure(String errorMessage){
		return new ServerResponse<T>(ResponseCode.FAILURE.getCode(),errorMessage);
	}

	public static <T> ServerResponse<T> Failure(int errorCode,String errorMessage){
		return new ServerResponse<T>(errorCode,errorMessage);
	}
	
}
