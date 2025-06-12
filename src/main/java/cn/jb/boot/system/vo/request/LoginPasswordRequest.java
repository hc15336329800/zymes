package cn.jb.boot.system.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginPasswordRequest {

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "密码")
    private String password;
}
