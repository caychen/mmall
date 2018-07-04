package org.com.cay.mmall.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

//保证序列化json的时候，如果value是null的对象，key则不进行序列化
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {

	private int status;
	private String msg;
	private T data;

	private ServerResponse(int status) {
		this.status = status;
	}

	private ServerResponse(int status, T data) {
		this.status = status;
		this.data = data;
	}

	private ServerResponse(int status, String msg) {
		this.status = status;
		this.msg = msg;
	}

	private ServerResponse(int status, String msg, T data) {
		this.status = status;
		this.msg = msg;
		this.data = data;
	}

	@JsonIgnore
	//使之不在json序列化结果集中
	public boolean isSuccess() {
		return this.status == ResponseCode.SUCCESS.getCode();
	}

	public int getStatus() {
		return status;
	}

	public String getMsg() {
		return msg;
	}

	public T getData() {
		return data;
	}

	public static <T> ServerResponse<T> createBySuccess() {
		return new ServerResponse<>(ResponseCode.SUCCESS.getCode());
	}

	public static <T> ServerResponse<T> createBySuccessMessage(String msg) {
		return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), msg);
	}

	public static <T> ServerResponse<T> createBySuccess(T data) {
		return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), data);
	}

	public static <T> ServerResponse<T> createBySuccess(String msg, T data) {
		return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), msg, data);
	}

	public static <T> ServerResponse<T> createByError() {
		return new ServerResponse<>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getDesc());
	}

	public static <T> ServerResponse<T> createByErrorMessage(String errorMsg) {
		return new ServerResponse<>(ResponseCode.ERROR.getCode(), errorMsg);
	}

	public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode, String errorMsg) {
		return new ServerResponse<>(errorCode, errorMsg);
	}
}
