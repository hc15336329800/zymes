package cn.jb.boot.biz.item.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ItemIdReq {

    @Schema(description = "id")
    @NotNull(message = "id 不能为空")
    private String id;
}
