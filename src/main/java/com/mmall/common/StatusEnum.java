package com.mmall.common;

public enum StatusEnum {

	ONLINE(1 , "上架"),
	OFFLINE( 0 , "下架"),
	DELETED( -1 , "已删除"),
	;

	int code ;
	String value ;

	private StatusEnum (int code , String value){
		this.code = code;
		this.value = value;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static StatusEnum idOf(int id){
		for (StatusEnum statusEnum : values()) {
			if (statusEnum.getCode() == id){
				return statusEnum;
			}
		}
		return null;
	}
}
