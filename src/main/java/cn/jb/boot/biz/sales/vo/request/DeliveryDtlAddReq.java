package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class DeliveryDtlAddReq {


    @Schema(description = "列表每条数据ID,修改时不为空")
    private String id;


    /**
     * 图纸号
     */
    @Schema(description = "产品编码不能为空")
    @NotNull(message = "产品编码不能为空")
    private String itemNo;


    /**
     * 计划数量
     */
    @Schema(description = "计划数量")
    @NotNull(message = "计划数量不能为空")
    private BigDecimal planCount;

    /**
     * 产品单位
     */
    @Schema(description = "产品单位")
    private String itemMeasure;

    /**
     * 发货数量
     */
    @Schema(description = "发货数量")
    private BigDecimal realCount;

    /**
     * 装车人
     */
    @Schema(description = "装车人")
    private String loader;

    /**
     * 卡车司机
     */
    @Schema(description = "卡车司机")
    private String truck;

    /**
     * 卡车号
     */
    @Schema(description = "卡车号")
    private String truckNo;
    @Schema(description = "备注")
    private String remark;
}
