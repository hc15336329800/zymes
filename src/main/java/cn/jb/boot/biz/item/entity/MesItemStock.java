package cn.jb.boot.biz.item.entity;

import cn.jb.boot.framework.com.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 产品库存表表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 17:04:05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mes_item_stock")
@Schema(name = "MesItemStock", description = "产品库存表")
public class MesItemStock extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 产品编码
     */
    @Schema(description = "产品编码")
    @TableField("item_no")
    private String itemNo;


    /**
     * 产品名称
     */
    @Schema(description = "产品名称")
    @TableField("item_name")
    private String itemName;


    /**
     * 库存量
     */
    @Schema(description = "库存量")
    @TableField("item_count")
    private BigDecimal itemCount;


    /**
     * 单位 eg:公斤，件
     */
    @Schema(description = "单位 eg:公斤，件")
    @TableField("item_measure")
    private String itemMeasure;


    /**
     * 来源：自制或采购
     */
    @Schema(description = "来源：自制或采购")
    @TableField("item_origin")
    private String itemOrigin;


    /**
     * 型号规格
     */
    @Schema(description = "型号规格")
    @TableField("item_model")
    private String itemModel;


    /**
     * BOM编码
     */
    @Schema(description = "BOM编码")
    @TableField("bom_no")
    private String bomNo;


    /**
     * 00:物料，01:bom
     */
    @Schema(description = "00:物料，01:bom")
    @TableField("item_type")
    private String itemType;


    /**
     * 库位
     */
    @Schema(description = "库位")
    @TableField("location")
    private String location;


    /**
     * 辅助量
     */
    @Schema(description = "辅助量")
    @TableField("item_count_assist")
    private BigDecimal itemCountAssist;


    /**
     * 辅助计量单位
     */
    @Schema(description = "辅助计量单位")
    @TableField("item_measure_assist")
    private String itemMeasureAssist;


    /**
     * 新加字段
     */
    @Schema(description = "新加字段")
    @TableField("uni_id")
    private Integer uniId;


    /**
     * 是有有效 00:无效，01:有效
     */
    @Schema(description = "是有有效 00:无效，01:有效")
    @TableField("is_valid")
    private String isValid;


    /**
     * ERP库存量
     */
    @Schema(description = "ERP库存量")
    @TableField("erp_count")
    private BigDecimal erpCount;


    /**
     * 净重
     */
    @Schema(description = "净重")
    @TableField("net_weight")
    private BigDecimal netWeight;

}
