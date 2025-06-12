package cn.jb.boot.system.service;


import cn.jb.boot.system.entity.UserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 用户角色表 服务类
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-04-11 20:40:45
 */
public interface UserRoleService extends IService<UserRole> {

    /**
     * 查询用户的角色信息
     *
     * @param userId 用户id
     * @return 角色id
     */
    List<String> userRoles(String userId);

    /**
     * 修改用户角色信息
     *
     * @param userId  用户id
     * @param roleIds 角色id
     * @return
     */
    void addUserRoles(String userId, List<String> roleIds);

    /**
     * 删除用户的角色
     *
     * @param userId
     */
    void deleteUserRoles(String userId);
}
