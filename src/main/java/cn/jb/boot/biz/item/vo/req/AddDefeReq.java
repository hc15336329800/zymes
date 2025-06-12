package cn.jb.boot.biz.item.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddDefeReq {

    @Schema(description = "图纸号")
    private String bomNo;

    /** 数量 */
    @Schema(description = "数量")
    private BigDecimal itemCount;

    /** 业务类型 */
    @Schema(description = "业务类型 00 出库，01 入库")
    private String bizType;
}
