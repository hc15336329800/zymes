package cn.jb.boot.system.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 密码强度校验 返回
 *
 * @Author: xyb
 * @Description:
 * @Date: 2023-05-11 下午 11:04
 **/
@Data
public class PasswordStrengthResponse {
    /**
     * 密码强度
     */
    @Schema(description = "密码强度")
    @NotBlank(message = "密码强度")
    private String strength;
}