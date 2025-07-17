package cn.jb.boot.system.controller;

import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.system.service.UserInfoService;
import cn.jb.boot.system.vo.request.LoginPasswordRequest;
import cn.jb.boot.system.vo.response.LoginResponse;
import cn.jb.boot.util.MsgUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/logout")
@Tag(name = "登出", description = "登出")
public class LogoutController {

    @Resource
    private UserInfoService userInfoService;


    /**
     * 用户登录
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public BaseResponse<String> logout() {
        userInfoService.logout();
        return MsgUtil.ok();
    }
}
