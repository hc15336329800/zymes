package cn.jb.boot.framework.service;


import cn.jb.boot.framework.com.entity.TokenCacheObj;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户token权限
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2019
 * @Date 2019年12月9日 下午5:19:49
 */
public interface AuthService {

    /**
     * 登陆授权
     *
     * @param token 用户token
     * @param cache 缓存对象
     */
    void addToken(String token, TokenCacheObj cache);

    /**
     * 判断token是否有效
     *
     * @param token token
     * @return token是否有效
     * @Description
     * @Date 2019年12月9日 下午5:20:09
     */
    boolean userAuth(String token);

    /**
     * 从header获取用户
     *
     * @param request 请求
     * @return
     * @author: xyb
     * @date: 2020-08-25 20:03:03
     */
    String uidByRequest(HttpServletRequest request);

    String token(HttpServletRequest request);

    TokenCacheObj userByRequest(HttpServletRequest request);
}
