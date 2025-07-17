package cn.jb.boot.biz.item.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 产品库存表表 新增请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 17:04:05
 */
@Data
@Schema(name = "MesItemStockCreateRequest", description = "产品库存表 新增请求参数")
public class MesItemStockCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 产品编码
     */
    @Schema(description = "产品编码")
    @NotEmpty(message = "产品编码不能为空")
    private String itemNo;

    /**
     * 产品名称
     */
    @Schema(description = "产品名称")
    @NotEmpty(message = "产品名称不能为空")
    private String itemName;

    /**
     * 库存量
     */
    @Schema(description = "库存量")
    private BigDecimal itemCount;

    /**
     * 单位 eg:公斤，件
     */
    @Schema(description = "单位 eg:公斤，件")
    private String itemMeasure;

    /**
     * 来源：自制或采购
     */
    @Schema(description = "来源：自制或采购")
    private String itemOrigin;

    /**
     * 型号规格
     */
    @Schema(description = "型号规格")
    private String itemModel;

    /**
     * BOM编码
     */
    @Schema(description = "BOM编码")
    private String bomNo;

    /**
     * 00:物料，01:bom
     */
    @Schema(description = "00:物料，01:bom")
    @NotEmpty(message = "物料类型不能为空")
    private String itemType;

    /**
     * 库位
     */
    @Schema(description = "库位")
    private String location;

    /**
     * 辅助量
     */
    @Schema(description = "辅助量")
    private BigDecimal itemCountAssist;

    /**
     * 辅助计量单位
     */
    @Schema(description = "辅助计量单位")
    private String itemMeasureAssist;

    /**
     * 净重
     */
    @Schema(description = "净重")
    private BigDecimal netWeight;

}
