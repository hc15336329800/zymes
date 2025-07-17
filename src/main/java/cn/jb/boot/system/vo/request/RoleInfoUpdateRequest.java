package cn.jb.boot.system.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
public class RoleInfoUpdateRequest extends RoleInfoCreateRequest {

    @Schema(description = "角色ID")
    @NotBlank(message = "角色ID")
    private String id;
}
