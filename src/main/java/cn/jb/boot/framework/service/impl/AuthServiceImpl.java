package cn.jb.boot.framework.service.impl;

import cn.jb.boot.framework.com.cache.TokenCache;
import cn.jb.boot.framework.com.entity.TokenCacheObj;
import cn.jb.boot.framework.enums.JbEnum;
import cn.jb.boot.framework.exception.CavException;
import cn.jb.boot.framework.service.AuthService;
import cn.jb.boot.util.WebUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


/**
 * 判断token信息
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2019
 * @Date 2019年12月9日 下午5:20:42
 */
@Service
public class AuthServiceImpl implements AuthService {

    private Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    public boolean userAuth(String token) {
        // 从缓存查询
        TokenCacheObj v = TokenCache.get(token);
        // 401
        return v == null || StringUtils.isBlank(v.getToken());
    }

    @Override
    public void addToken(String userId, TokenCacheObj openId) {
        TokenCache.put(userId, openId);
    }

    public TokenCacheObj user(HttpServletRequest request) {
        String token = token(request);
        // 从缓存查询
        TokenCacheObj cache = TokenCache.get(token);
        if (cache == null) {

            throw new CavException("登录失效！");
        }

//		TokenCacheObj cache = new TokenCacheObj("123","123456");

        return cache;
    }

    public String token(HttpServletRequest request) {
        if (request == null) {
            request = WebUtil.request();
        }
        String headerKey = JbEnum.TOKEN_HEADER.getCode();
        String author = StringUtils.trim(request.getHeader(headerKey));
        String token = author;
        if (StringUtils.isNotBlank(author)) {
            String blank = " ";
            if (StringUtils.contains(author, blank)) {
                token = StringUtils.substringAfter(author, blank);
            }
        }
        return token;
    }

    /**
     * 从header获取用户
     *
     * @param request 参数
     */
    @Override
    public String uidByRequest(HttpServletRequest request) {
        TokenCacheObj cache = user(request);
        String uid = cache.getUid();
        if (StringUtils.isBlank(uid)) {
            throw new CavException("用户id为空！");
        }
        return uid;
    }

    @Override
    public TokenCacheObj userByRequest(HttpServletRequest request) {
        TokenCacheObj cache = user(request);
        if (Objects.isNull(cache)) {
            throw new CavException("用户信息未空！");
        }
        return cache;
    }
}
