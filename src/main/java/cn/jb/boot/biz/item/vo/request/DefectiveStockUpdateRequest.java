package cn.jb.boot.biz.item.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
public class DefectiveStockUpdateRequest extends DefectiveStockCreateRequest {

    @Schema(description = "id")
    @NotEmpty(message = "id不能为空")
    private String id;
}
