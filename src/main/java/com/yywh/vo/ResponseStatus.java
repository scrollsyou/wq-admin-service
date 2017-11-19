package com.yywh.vo;

import org.springframework.validation.BindingResult;

/**
 * 增删改操作
 * 返回状态
 * @author you 2016年3月25日
 *
 */
public class ResponseStatus<T> {
	public ResponseStatus(){
		super();
	}
	public ResponseStatus(int status, String message) {
		super();
		this.status = status;
		this.message = message;
	}
	public ResponseStatus(int status, String message, T data) {
		super();
		this.status = status;
		this.message = message;
		this.data = data;
	}
	/**
	 * 查询状态 0为操作异常1为正常-1为登陆失效
	 * */
	private int status=1;
	/**
	 * 出错功成功信息
	 * */
	private String message;
	/**
	 * 返回数据
	 */
	private T data;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	/**
	 * 通过校验后取校验不通过的提示
	 * @param br
	 */
	public void validResult(BindingResult result){
		this.message = result.getAllErrors().get(0).getDefaultMessage();
	}
	
}
