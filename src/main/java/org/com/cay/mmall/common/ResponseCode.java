package org.com.cay.mmall.common;

public enum ResponseCode {

	SUCCESS(0X0, "SUCCESS"),
	ERROR(0X1, "ERROR"),
	ILLEGAL_ARGUMENT(0X2, "ILLEGAL_ARGUMENT"),
	NEED_LOGIN(0XA, "NEED_LOGIN");

	private int code;
	private final String desc;

	ResponseCode(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}
}
