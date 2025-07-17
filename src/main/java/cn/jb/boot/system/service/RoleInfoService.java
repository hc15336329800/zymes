package cn.jb.boot.system.service;


import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.system.entity.RoleInfo;
import cn.jb.boot.system.vo.request.RoleInfoCreateRequest;
import cn.jb.boot.system.vo.request.RoleInfoPageRequest;
import cn.jb.boot.system.vo.request.RoleInfoRequest;
import cn.jb.boot.system.vo.request.RoleInfoUpdateRequest;
import cn.jb.boot.system.vo.request.RoleMenuAddRequest;
import cn.jb.boot.system.vo.request.RoleMenuQueryRequest;
import cn.jb.boot.system.vo.response.RoleInfoPageResponse;
import cn.jb.boot.system.vo.response.RoleInfoResponse;
import cn.jb.boot.system.vo.response.UserRoleResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 角色信息表 服务类
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-04-11 20:40:45
 */
public interface RoleInfoService extends IService<RoleInfo> {

    /**
     * 获取角色已有的菜单
     *
     * @param roleCode 角色编码
     * @return 菜单信息
     */
    List<String> roleCheckedMenu(RoleMenuQueryRequest roleCode);

    /**
     * /**保存角色的菜单
     *
     * @param params 请求参数
     */
    void saveRoleMenu(RoleMenuAddRequest params);

    /**
     * 新增角色
     *
     * @param request 请求参数
     */
    void addRole(RoleInfoCreateRequest request);

    /**
     * 修改角色
     *
     * @param params 请求参数
     */
    void updateRole(RoleInfoUpdateRequest params);

    /**
     * 删除角色
     *
     * @param roleId 角色id
     */
    void deleteRole(String roleId);

    /**
     * 查看角色详情
     *
     * @param roleId 角色id
     * @return 角色信息
     */
    RoleInfoResponse detail(String roleId);

    /**
     * 分页查询角色信息
     *
     * @param request 请求参数
     * @return 分页信息
     */
    BaseResponse<List<RoleInfoPageResponse>> pageList(BaseRequest<RoleInfoPageRequest> request);

    /**
     * 查询所有的用户角色
     *
     * @return
     */
    List<UserRoleResponse> userRoles();
}
