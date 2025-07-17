package cn.jb.boot.framework.exception;

import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.com.response.ErrorInfo;
import cn.jb.boot.framework.enums.JbEnum;
import cn.jb.boot.util.StringUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;

/**
 * 全局错误处理，json请求返回json格式，需要按照要求改一下ErrorInfo
 * 可以按照异常类型捕捉处理
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2019
 * @Date 2019年12月5日 上午11:33:54
 */
@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {

    public Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 在controller里面内容执行之前，
     * 校验一些参数不匹配啊，
     * Get post方法不对啊之类的
     *
     * @param request
     * @param e
     * @return
     * @throws Exception
     * @Description
     * @Date 2019年12月5日 上午11:15:21
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public BaseResponse<String> jsonHandler(HttpServletRequest request, Exception e) {
        logger.info("GlobalExceptionHandler.jsonHandler start");
        BaseResponse<String> errorVo = new BaseResponse();
        ErrorInfo r = new ErrorInfo();
        errorVo.setTxStatus(JbEnum.FAIL.getCode());
        r.setCode(String.valueOf(e.hashCode()));
        r.setMessage("服务有点问题，请稍候！");
        r.setUrl(request.getRequestURI());
        log(e, request);
        errorVo.setErrorInfo(r);
        return errorVo;
    }

    @ResponseBody
    @ExceptionHandler(CavException.class)
    public BaseResponse<String> cavException(HttpServletRequest request, CavException e) {
        logger.info("MethodArgumentNotValidException start");
        BaseResponse<String> errorVo = new BaseResponse();
        ErrorInfo r = new ErrorInfo();
        errorVo.setTxStatus(JbEnum.FAIL.getCode());
        r.setCode(JbEnum.FAIL.getCode());
        r.setMessage(e.getCode());
        r.setUrl(request.getRequestURI());
        log(e, request);
        errorVo.setErrorInfo(r);
        return errorVo;
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<String> argumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        logger.info("MethodArgumentNotValidException start");
        BaseResponse<String> errorVo = new BaseResponse();
        ErrorInfo r = new ErrorInfo();
        errorVo.setTxStatus(JbEnum.FAIL.getCode());
        StringBuilder msg = new StringBuilder();
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        if (CollectionUtils.isNotEmpty(errors)) {
            for (ObjectError oe : errors) {
                if (StringUtils.isNotBlank(msg)) {
                    msg.append(StringUtil.COMMA);
                }
                msg.append(oe.getDefaultMessage());
            }
        }
        String code = String.valueOf(e.hashCode());
        r.setCode(code);
        r.setMessage(msg.toString());
        r.setUrl(request.getRequestURI());
        log(e, request);
        errorVo.setErrorInfo(r);
        return errorVo;
    }

    /**
     * 打印错误消息
     *
     * @param ex
     * @param request
     * @Description
     * @Date 2019年12月5日 上午11:16:54
     */
    public void log(Exception ex, HttpServletRequest request) {
        logger.error("************************异常开始*******************************");
        logger.error("请求地址：{}", request.getRequestURL());
        Enumeration<String> es = request.getParameterNames();
        logger.error("请求参数");
        String key;
        while (es.hasMoreElements()) {
            key = es.nextElement();
            logger.error("{}-------{}", key, request.getParameter(key));
        }
        logger.error("Error : ", ex);
        // 递归打印Cause
        printCause(ex.getCause());
        logger.error("************************异常结束*******************************");
    }

    /**
     * 递归CauseBy
     *
     * @param cause
     */
    private void printCause(Throwable cause) {
        if (cause != null) {
            logger.error("Cause by:");
            StackTraceElement[] error = cause.getStackTrace();
            if (error.length > 0) {
                logger.error(error[0].toString());
            }
            printCause(cause.getCause());
        }
    }

}