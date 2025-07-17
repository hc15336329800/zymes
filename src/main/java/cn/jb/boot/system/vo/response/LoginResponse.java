package cn.jb.boot.system.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginResponse {

    @Schema(description = "用户ID")
    private String id;
    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "用户昵称")
    private String nickName;
}
