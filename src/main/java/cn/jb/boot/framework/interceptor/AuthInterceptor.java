package cn.jb.boot.framework.interceptor;

import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.enums.JbEnum;
import cn.jb.boot.framework.service.AuthService;
import cn.jb.boot.util.JsonUtil;
import cn.jb.boot.util.MsgUtil;
import cn.jb.boot.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 权限拦截器
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2019
 * @Date 2019年12月9日 下午5:15:57
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthService authService;

    private final static Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    /**
     * JOSN 返回消息
     */
    private final static String MSG = "{\"code\" : 401, \"message\" : \"%s\"}";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) {
        String token = authService.token(request);
        if (StringUtils.isBlank(token)) {
            dealErrorReturn(response, JbEnum.MISSING_TOKEN.getCode());
            return false;
        }
        // 无权限
        if (authService.userAuth(token)) {
            dealErrorReturn(response, JbEnum.INVALID_TOKEN.getCode());
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest q, HttpServletResponse p, Object o, ModelAndView m) {

    }

    @Override
    public void afterCompletion(HttpServletRequest q, HttpServletResponse p, Object o, Exception e) {
    }

    /**
     * 无权限的时候校验防止401
     *
     * @param response 消息串
     * @param obj      消息串
     * @Description
     * @Date 2019年12月9日 下午5:16:41
     */
    private void dealErrorReturn(HttpServletResponse response, String obj) {
        BaseResponse<String> result = MsgUtil.error("401", obj);
        response.setCharacterEncoding(StringUtil.UTF8);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        try (PrintWriter writer = response.getWriter()) {
            writer.print(JsonUtil.toJson(result));
        } catch (IOException ex) {
            logger.error("response error", ex);
        }
    }
}
