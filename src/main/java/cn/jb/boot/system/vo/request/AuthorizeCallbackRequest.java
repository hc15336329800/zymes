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
public class AuthorizeCallbackRequest {

    @NotBlank(message = "code不能为空！")
    @Schema(description = "code")
    private String code;

    @Schema(description = "状态")
    private String status;

}