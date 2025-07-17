package cn.jb.boot.biz.item.vo.response;

import cn.jb.boot.framework.annotation.DictTrans;
import cn.jb.boot.framework.com.DictType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 产品库存表表 详情返回信息
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 17:04:05
 */
@Data
@Schema(name = "MesItemStockInfoResponse", description = "产品库存表 详情返回信息")
public class MesItemStockInfoResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

    /**
     * 产品编码
     */
    @Schema(description = "产品编码")
    private String itemNo;

    /**
     * 产品名称
     */
    @Schema(description = "产品名称")
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
    @DictTrans(type = DictType.ITEM_ORIGIN)
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
    @DictTrans(type = DictType.ITEM_TYPE)
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
     * ERP库存量
     */
    @Schema(description = "ERP库存量")
    private BigDecimal erpCount;

    /**
     * 净重
     */
    @Schema(description = "净重")
    private BigDecimal netWeight;

}
