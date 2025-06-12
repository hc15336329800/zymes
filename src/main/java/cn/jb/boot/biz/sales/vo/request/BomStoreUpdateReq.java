package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class BomStoreUpdateReq {
    @Schema(description = "id")
    @NotBlank
    private String id;
    /**
     * 图纸号
     */
    @Schema(description = "图纸号")
    @NotBlank(message = "itemNo不能为空")
    private String itemNo;

    /**
     * 出入库类型;01:出库，02:入库
     */
    @Schema(description = "出入库类型;00:出库，01:入库")
    @NotBlank(message = "出入库类型不能为空")
    private String bizType;

    /**
     * 申请数量
     */
    @Schema(description = "申请数量")
    @NotNull
    private BigDecimal itemCount;
}
