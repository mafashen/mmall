package com.mmall.common;

public enum RoleEnum {

	USER(0 , "普通用户"),
	ADMIN(1 , "管理员") ,
	;

	int code ;
	String value ;

	private RoleEnum (int code , String value){
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

	public static RoleEnum idOf(int id){
		for (RoleEnum roleEnum : values()) {
			if (roleEnum.getCode() == id){
				return roleEnum;
			}
		}
		return null;
	}
}
