package cn.jb.boot.system.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: xyb
 * @Description:
 * @Date: 2023-03-24 下午 01:01
 **/
@Data
public class AuthorizeLoginRequest {

    @NotBlank(message = "随机数不能为空！")
    @Schema(description = "随机数")
    private String uuid;

}