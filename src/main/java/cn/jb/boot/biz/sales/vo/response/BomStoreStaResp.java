package cn.jb.boot.biz.sales.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BomStoreStaResp {

    @Schema(description = "月份")
    private String month;
    @Schema(description = "数量")
    private BigDecimal itemCount;
}
