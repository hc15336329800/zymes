package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class BatchUpdateReq extends DeliveryBatchAddReq {

    @NotNull(message = "id不能为空")
    @Schema(description = "主表ID")
    private String id;

}
