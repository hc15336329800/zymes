package cn.jb.boot.biz.item.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DistributionBomReq {

    @Schema(description = "id")
    private String id;

    @Data
    public static class DistributionDtl {
        @Schema(description = "工人号")
        private String employeeNo;
        @Schema(description = "工人名称")
        private String employeeName;
        @Schema(description = "分配量;占比或数量")
        private BigDecimal distCount;
    }
}
