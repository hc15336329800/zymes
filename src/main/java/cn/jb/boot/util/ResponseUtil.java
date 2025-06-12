package cn.jb.boot.util;

import cn.jb.boot.framework.com.response.ApiResponse;
import cn.jb.boot.framework.com.response.BaseResponse;

//返回格式  新增
//		{
//				"code": "200",
//				"msg": "操作成功",
//				"data": {
//				"id": "1697438xxxxxx",
//				"orderNo": "SO20240518001",
//				"custName": "华为",
//				"itemNo": "ITEM-001"
//				}
//				}
public class ResponseUtil {

	public static <T> ApiResponse<T> ok() {
		return new ApiResponse<>("200", "操作成功");
	}

	public static <T> ApiResponse<T> ok(T data) {
		return new ApiResponse<>("200", "操作成功", data);
	}

	public static <T> ApiResponse<T> fail(String msg) {
		return new ApiResponse<>("500", msg);
	}

	public static <T> ApiResponse<T> fail(String code, String msg) {
		return new ApiResponse<>(code, msg);
	}
}
