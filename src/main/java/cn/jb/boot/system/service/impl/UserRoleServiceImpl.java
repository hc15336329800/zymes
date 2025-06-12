package cn.jb.boot.system.service.impl;


import cn.jb.boot.system.entity.RoleInfo;
import cn.jb.boot.system.entity.UserRole;
import cn.jb.boot.system.mapper.RoleInfoMapper;
import cn.jb.boot.system.mapper.UserRoleMapper;
import cn.jb.boot.system.service.UserRoleService;
import cn.jb.boot.util.PojoUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户角色表 服务实现类
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-04-11 20:40:45
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Resource
    private UserRoleMapper mapper;
    @Resource
    private RoleInfoMapper roleInfoMapper;

    @Override
    public List<String> userRoles(String userId) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId);
        List<UserRole> roles = this.list(wrapper);
        return PojoUtil.toList(roles, UserRole::getRoleCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUserRoles(String userId, List<String> roleIds) {
        deleteUserRoles(userId);
        if (CollectionUtils.isNotEmpty(roleIds)) {
            List<RoleInfo> roleInfos = roleInfoMapper.selectList(new LambdaQueryWrapper<RoleInfo>().in(RoleInfo::getRoleCode, roleIds));
            List<UserRole> roles = new ArrayList<>();
            roleInfos.forEach(r -> {
                UserRole role = new UserRole();
                role.setRoleCode(r.getRoleCode());
                role.setRoleName(r.getRoleName());
                role.setUserId(userId);
                roles.add(role);
            });
            this.saveBatch(roles);
        }
    }

    @Override
    public void deleteUserRoles(String userId) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId);
        this.remove(wrapper);
    }
}
