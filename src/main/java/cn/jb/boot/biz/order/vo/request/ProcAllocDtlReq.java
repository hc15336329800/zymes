package cn.jb.boot.biz.order.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ProcAllocDtlReq {

    @Schema(description = "工单Id")
    private String workOrderId;

    @Schema(description = "设备Id")
    @NotEmpty(message = "设备ID不能为空")
    private String deviceId;

    @NotNull(message = "分配数量不能为空")
    private BigDecimal allocCount;


}
