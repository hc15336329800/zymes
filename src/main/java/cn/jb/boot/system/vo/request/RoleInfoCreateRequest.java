package cn.jb.boot.system.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RoleInfoCreateRequest {
    @NotBlank(message = "角色名称不能为空")
    @Schema(description = "角色名称")

    private String roleName;

    @Schema(description = "备注信息")
    private String roleDesc;
}
