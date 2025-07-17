package cn.jb.boot.biz.outer.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OuterTaskDtlRequest {
    /**
     * 外协账号
     */
    @Schema(description = "外协账号")
    private String userId;

    @Schema(description = "id")
    private String id;


    /**
     * 外协数量
     */
    @Schema(description = "外协数量")
    private BigDecimal outerCount;
}
