package cn.jb.boot.biz.order.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DistListRequest {
    @Schema(description = "工序Id")
    @NotNull(message = "id 不能未空")
    private List<String> ids;
    @Schema(description = "班次")
    @NotNull(message = "班次不能为空")
    private String shiftType;


}
