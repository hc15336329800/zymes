package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class DeliveryDtlUpdateReq {
    /**
     * 主键
     */
    @Schema(description = "主键")
    @NotNull(message = "id不能为空")
    private String id;
    /**
     * 主表ID
     */
    @Schema(description = "主表ID")
    private String deliveryId;


    /**
     * 图纸号
     */
    @Schema(description = "图纸号")
    @NotNull(message = "图纸号不能为空")
    private String bomNo;

    /**
     * 产品名称
     */
    @Schema(description = "产品名称")
    private String itemName;

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
