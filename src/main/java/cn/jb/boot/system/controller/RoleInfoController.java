package cn.jb.boot.system.controller;


import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.system.service.RoleInfoService;
import cn.jb.boot.system.vo.request.RoleInfoCreateRequest;
import cn.jb.boot.system.vo.request.RoleInfoPageRequest;
import cn.jb.boot.system.vo.request.RoleInfoRequest;
import cn.jb.boot.system.vo.request.RoleInfoUpdateRequest;
import cn.jb.boot.system.vo.request.RoleMenuAddRequest;
import cn.jb.boot.system.vo.request.RoleMenuQueryRequest;
import cn.jb.boot.system.vo.response.RoleInfoPageResponse;
import cn.jb.boot.system.vo.response.RoleInfoResponse;
import cn.jb.boot.system.vo.response.UserRoleResponse;
import cn.jb.boot.util.MsgUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 前端控制器
 *
 * @author xieyubin
 * @Description
 * @Copyright Copyright (c) 2022
 * @since 2022-07-14 10:18:46
 */
@RestController
@RequestMapping("/api/role/info")
@Tag(name = "角色信息表接口", description = "角色信息表接口")
public class RoleInfoController {

    @Resource
    private RoleInfoService roleInfService;


    /**
     * 角色拥有的菜单
     *
     * @param request 请求参数
     * @return
     */
    @PostMapping("/role_menu")
    @Operation(summary = "角色拥有的菜单")
    public BaseResponse<List<String>> roleMenu(@RequestBody @Valid BaseRequest<RoleMenuQueryRequest> request) {
        List<String> menus = roleInfService.roleCheckedMenu(request.getParams());
        return MsgUtil.ok(menus);
    }

    /**
     * 保存角色的菜单
     *
     * @param request
     * @return
     */
    @PostMapping("/add_role_menu")
    @Operation(summary = "保存角色菜单")
    public BaseResponse<String> addRoleMenu(@RequestBody @Valid BaseRequest<RoleMenuAddRequest> request) {
        RoleMenuAddRequest params = MsgUtil.params(request);
        roleInfService.saveRoleMenu(params);
        return MsgUtil.ok();
    }


    /**
     * 新增角色
     *
     * @param request 查询参数
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "新增角色")
    public BaseResponse<String> addRoleInfo(@RequestBody @Valid BaseRequest<RoleInfoCreateRequest> request) {
        RoleInfoCreateRequest params = MsgUtil.params(request);
        roleInfService.addRole(params);
        return MsgUtil.ok();
    }

    /**
     * @desc: 修改角色
     **/
    @PostMapping("/update")
    @Operation(summary = "修改角色信息")
    public BaseResponse<String> updateRoleInfo(@RequestBody @Valid BaseRequest<RoleInfoUpdateRequest> request) {
        RoleInfoUpdateRequest params = MsgUtil.params(request);
        roleInfService.updateRole(params);
        return MsgUtil.ok();
    }

    /**
     * 删除角色
     *
     * @param request 查询参数
     * @return
     */
    @PostMapping("/delete")
    @Operation(summary = "删除角色")
    public BaseResponse<String> deleteRoleInfo(@RequestBody @Valid BaseRequest<ComId> request) {
        ComId params = MsgUtil.params(request);
        roleInfService.deleteRole(params.getId());
        return MsgUtil.ok();
    }

    /**
     * 角色详情
     *
     * @param request 查询参数
     * @return
     */
    @PostMapping("/detail")
    @Operation(summary = "查看角色详情")
    public BaseResponse<RoleInfoResponse> detailOfRoleInfo(@RequestBody @Valid BaseRequest<ComId> request) {
        ComId params = MsgUtil.params(request);
        RoleInfoResponse detail = roleInfService.detail(params.getId());
        return MsgUtil.ok(detail);
    }

    /**
     * 角色管理分页
     *
     * @param request
     * @return
     */
    @PostMapping("/page")
    @Operation(summary = "角色管理分页")
    public BaseResponse<List<RoleInfoPageResponse>> pageOfRoleInfo(@RequestBody @Valid BaseRequest<RoleInfoPageRequest> request) {
        return roleInfService.pageList(request);
    }


    @PostMapping("/user_role")
    @Operation(summary = "查询用户所有的角色")
    public BaseResponse<List<UserRoleResponse>> userRoles(@RequestBody BaseRequest<String> request) {
        List<UserRoleResponse> menuTree = roleInfService.userRoles();
        return MsgUtil.ok(menuTree);
    }
}
