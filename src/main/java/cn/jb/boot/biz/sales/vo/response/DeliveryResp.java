package cn.jb.boot.biz.sales.vo.response;

import cn.jb.boot.framework.annotation.DictTrans;
import cn.jb.boot.framework.com.DictType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeliveryResp {
    /**
     * 主键
     */
    @Schema(description = "主键")
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
    @DictTrans(type = DictType.BOM_NO, name = "bomNo")
    private String itemNo;


    /**
     * 计划数量
     */
    @Schema(description = "计划数量")
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

    /**
     * 卡车号
     */
    @Schema(description = "备注")
    private String remark;
}
