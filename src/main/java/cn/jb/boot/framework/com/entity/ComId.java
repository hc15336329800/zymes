package cn.jb.boot.framework.com.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ComId {

    @NotNull(message = "id不能为空")
    @Schema(description = "id")
    private String id;
}
