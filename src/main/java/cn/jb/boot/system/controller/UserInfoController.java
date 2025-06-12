package cn.jb.boot.system.controller;


import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.system.service.UserInfoService;
import cn.jb.boot.system.vo.request.LoginPasswordRequest;
import cn.jb.boot.system.vo.request.UserInfoCreateRequest;
import cn.jb.boot.system.vo.request.UserInfoPageRequest;
import cn.jb.boot.system.vo.request.UserInfoPageResponse;
import cn.jb.boot.system.vo.request.UserInfoResetPasswordRequest;
import cn.jb.boot.system.vo.request.UserInfoStatusRequest;
import cn.jb.boot.system.vo.request.UserInfoUpdatePasswordRequest;
import cn.jb.boot.system.vo.request.UserInfoUpdateRequest;
import cn.jb.boot.system.vo.response.DictListResponse;
import cn.jb.boot.system.vo.response.LoginResponse;
import cn.jb.boot.system.vo.response.UserInfoDetailResponse;
import cn.jb.boot.system.vo.response.UserSelectResponse;
import cn.jb.boot.util.MsgUtil;
import cn.jb.boot.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * 用户信息相关的
 *
 * @Author: xyb
 * @Description:
 * @Date: 2023-04-10 下午 10:03
 **/
@Slf4j
@RestController
@RequestMapping("/api/user/info")
@Tag(name = "用户信息", description = "用户信息")
public class UserInfoController {


    @Resource
    private UserInfoService userInfoService;


    @PostMapping("/loginUserInfo")
    @Operation(summary = "登录用户的信息")
    public BaseResponse<UserInfoDetailResponse> loginUserInfo() {

        UserInfoDetailResponse response = userInfoService.userInfoDetail(UserUtil.uid());
        return MsgUtil.ok(response);
    }


    /**
     * 用户分页
     *
     * @param request 请求参数
     * @return 分页数据
     */
    @PostMapping("/pageUserInfo")
    @Operation(summary = "登录用户的信息")
    public BaseResponse<List<UserInfoPageResponse>> userPageList(@RequestBody @Valid BaseRequest<UserInfoPageRequest> request) {
        return userInfoService.userPageInfo(request);
    }

    @PostMapping("/userDetail")
    @Operation(summary = "用户详情信息")
    public BaseResponse<UserInfoDetailResponse> userDetail(@RequestBody @Valid BaseRequest<String> request) {
        String id = MsgUtil.params(request);
        UserInfoDetailResponse response = userInfoService.userInfoDetail(id);
        return MsgUtil.ok(response);
    }

    @PostMapping("/saveUser")
    @Operation(summary = "新增用户信息")
    public BaseResponse<String> saveUser(@RequestBody @Valid BaseRequest<UserInfoCreateRequest> request) {
        UserInfoCreateRequest params = MsgUtil.params(request);
        userInfoService.insertUserInfo(params);
        return MsgUtil.ok();
    }

    @PostMapping("/updateUser")
    @Operation(summary = "更新用户信息")
    public BaseResponse<String> updateUserInfo(@RequestBody @Valid BaseRequest<UserInfoUpdateRequest> request) {
        UserInfoUpdateRequest params = MsgUtil.params(request);
        userInfoService.updateUserInfo(params);
        return MsgUtil.ok();
    }

    @PostMapping("/userStatus")
    @Operation(summary = "修改用户状态")
    public BaseResponse<String> updateUserStatus(@RequestBody @Valid BaseRequest<UserInfoStatusRequest> request) {
        UserInfoStatusRequest params = MsgUtil.params(request);
        userInfoService.updateUserStatus(params.getId(), params.getDataStatus());
        return MsgUtil.ok();
    }

    @PostMapping("/deleteUserInfo")
    @Operation(summary = "修改用户状态")
    public BaseResponse<String> deleteUserInfo(@RequestBody @Valid BaseRequest<ComId> request) {
        ComId params = MsgUtil.params(request);
        userInfoService.deleteUserInfo(params.getId());
        return MsgUtil.ok();
    }

    @PostMapping("/resetPwd")
    @Operation(summary = "重置用户密码")
    public BaseResponse<String> resetPwd(@RequestBody @Valid BaseRequest<UserInfoResetPasswordRequest> request) {
        UserInfoResetPasswordRequest params = MsgUtil.params(request);
        userInfoService.resetPassword(params.getUserId(), params.getNewPassword());
        return MsgUtil.ok();
    }

    @PostMapping("/updatePwd")
    @Operation(summary = "更新用户密码")
    public BaseResponse<String> updatePwd(@RequestBody @Valid BaseRequest<UserInfoUpdatePasswordRequest> request) {
        UserInfoUpdatePasswordRequest params = MsgUtil.params(request);
        userInfoService.updatePassword(params);
        return MsgUtil.ok();
    }

    @PostMapping("/outerUserSelected")
    @Operation(summary = "外协用户下拉框")
    public BaseResponse<List<DictListResponse>> outerUserSelected() {
        List<DictListResponse> list = userInfoService.outerUserSelected();
        return MsgUtil.ok(list);
    }


}