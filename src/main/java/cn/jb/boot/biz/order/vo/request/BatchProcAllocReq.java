package cn.jb.boot.biz.order.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class BatchProcAllocReq {
    @Schema(description = "班次类型")
    @NotEmpty(message = "班次类型不能为空")
    private String shiftType;

    @Schema(description = "班组类型")
    @NotEmpty(message = "班组类型不能为空")
    private String groupId;

    @NotNull
    @Valid
    @Schema(description = "明细数据")
    private List<SingleProcAllocReq> list;
}
