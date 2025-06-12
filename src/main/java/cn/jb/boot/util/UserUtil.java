package cn.jb.boot.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.jb.boot.framework.com.Constants;
import cn.jb.boot.framework.com.entity.TokenCacheObj;
import cn.jb.boot.framework.config.SpringContextUtil;
import cn.jb.boot.framework.service.AuthService;
import cn.jb.boot.system.service.UserRoleService;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 用户信息
 *
 * @Author: xyb
 * @Description:
 * @Date: 2022-11-14 下午 10:06
 **/
public class UserUtil {

    private static AuthService AUTH = SpringContextUtil.getBeanByClass(AuthService.class);

    private static final UserRoleService ROLE_SERVICE = SpringUtil.getBean(UserRoleService.class);


    /**
     * 用户名称
     *
     * @return
     */
    public static String userName() {
        return user().getUserName();
    }

    public static String token() {
        return AUTH.token(null);
    }


    public static TokenCacheObj user() {
        return AUTH.userByRequest(null);
    }

    /**
     * 用户id 主键
     *
     * @return
     */
    public static String uid() {
        return user().getUid();
    }


    /**
     * 是否管理员
     *
     * @return
     */
    public static boolean isAdmin() {
        List<String> roles = ROLE_SERVICE.userRoles(uid());
        if (CollectionUtils.isNotEmpty(roles)) {
            return roles.contains(Constants.JS_ADMIN);
        }
        return true;
    }

    /**
     * 数据权限 判断当前用户的角色 返回查询的数据权限
     *
     * @return 数据权限
     */
    public static String userDataRole() {
        return isAdmin() ? Constants.STATUS_01 : Constants.STATUS_00;
    }
}
