package cn.jb.boot.system.service;


import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.system.entity.UserInfo;
import cn.jb.boot.system.vo.request.LoginPasswordRequest;
import cn.jb.boot.system.vo.request.UserInfoCreateRequest;
import cn.jb.boot.system.vo.request.UserInfoPageRequest;
import cn.jb.boot.system.vo.request.UserInfoPageResponse;
import cn.jb.boot.system.vo.request.UserInfoUpdatePasswordRequest;
import cn.jb.boot.system.vo.request.UserInfoUpdateRequest;
import cn.jb.boot.system.vo.response.DictListResponse;
import cn.jb.boot.system.vo.response.LoginResponse;

import cn.jb.boot.system.vo.response.UserInfoDetailResponse;
import cn.jb.boot.system.vo.response.UserSelectResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用户信息表 服务类
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-03-24 14:52:38
 */
public interface UserInfoService extends IService<UserInfo> {


    LoginResponse login(LoginPasswordRequest params, HttpServletResponse response);


    UserInfoDetailResponse userInfoDetail(String uid);

    /**  ：全部用户列表 */
    List<DictListResponse> userInfoAll();

    BaseResponse<List<UserInfoPageResponse>> userPageInfo(BaseRequest<UserInfoPageRequest> request);

    void insertUserInfo(UserInfoCreateRequest params);

    void updateUserInfo(UserInfoUpdateRequest params);

    void updateUserStatus(String id, String dataStatus);

    void deleteUserInfo(String id);

    void resetPassword(String userId, String newPassword);

    void updatePassword(UserInfoUpdatePasswordRequest params);


    void logout();

    List<DictListResponse> outerUserSelected();

}
