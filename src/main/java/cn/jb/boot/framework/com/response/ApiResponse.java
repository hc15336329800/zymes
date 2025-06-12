package cn.jb.boot.framework.com.response;

import lombok.Data;


//  新增  返回格式处理

@Data
public class ApiResponse<T> {
	private String code;
	private String msg;
	private T data;

	public ApiResponse() {}

	public ApiResponse(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public ApiResponse(String code, String msg, T data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}
}
