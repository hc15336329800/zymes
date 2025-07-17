package cn.jb.boot.util;


import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.com.response.ErrorInfo;
import cn.jb.boot.framework.com.response.PagingResponse;
import cn.jb.boot.framework.enums.JbEnum;
import cn.jb.boot.framework.exception.CavException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


/**
 * @author YX
 * @Description 请求和返回封装
 * @Date 2021/8/18 0:07
 */
public class MsgUtil {

    public static <T> T params(BaseRequest<T> request) {
        T t = request.getParams();
        if (t == null) {
            throw new CavException("参数不能为空！");
        }
        return t;
    }

    /**
     * ✅ 新增方法：支持默认构造对象
     */
    public static <T> T params(BaseRequest<T> request, Supplier<T> defaultSupplier) {
        return (request == null || request.getParams() == null)
                ? defaultSupplier.get()
                : request.getParams();
    }


    /**
     * ✅ 新增方法：失败返回
     */
    public static <T> BaseResponse<T> fail(String message) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setTxStatus("01"); // 表示失败
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setCode("01");            // 框架中默认失败码
        errorInfo.setMessage(message);
        response.setErrorInfo(errorInfo);
        return response;
    }



    public static <T> BaseResponse<T> ok(T params) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setData(params);
        return response;
    }

    public static BaseResponse<String> ok() {
        return new BaseResponse<>();
    }

    public static <R> BaseResponse<List<R>> emptyPage(Paging paging) {
        BaseResponse<List<R>> response = new BaseResponse<>();
        PagingResponse page = new PagingResponse();
        page.setPageNum(paging.getPageNum());
        page.setPageSize(paging.getPageSize());
        page.setTotalNum(0);
        response.setPage(page);
        response.setData(new ArrayList<>());
        return response;
    }

    public static BaseResponse<String> error(String code, String msg) {
        BaseResponse<String> response = new BaseResponse<>();
        response.setTxStatus(JbEnum.FAIL.getCode());
        ErrorInfo error = new ErrorInfo();
        error.setCode(code);
        error.setMessage(msg);
        response.setErrorInfo(error);
        return response;
    }
}
