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
@RequestMapping("/api/anon")
@Tag(name = "登录", description = "登录")
public class LoginController {

    @Resource
    private UserInfoService userInfoService;


    /**
     * 用户登录
     *
     * @param request
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public BaseResponse<LoginResponse> login(@RequestBody @Valid BaseRequest<LoginPasswordRequest> request, HttpServletResponse response) {
        LoginPasswordRequest params = MsgUtil.params(request);
        LoginResponse resp = userInfoService.login(params, response);
        return MsgUtil.ok(resp);
    }

}
