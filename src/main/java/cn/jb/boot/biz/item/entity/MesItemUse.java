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
 * 产品用料表表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 16:55:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mes_item_use")
@Schema(name = "MesItemUse", description = "产品用料表")
public class MesItemUse extends BaseEntity {

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
     * 用料编码
     */
    @Schema(description = "用料编码")
    @TableField("use_item_no")
    private String useItemNo;


    /**
     * 用料量
     */
    @Schema(description = "用料量")
    @TableField("use_item_count")
    private BigDecimal useItemCount;


    /**
     * 用料单位 eg:公斤，件
     */
    @Schema(description = "用料单位 eg:公斤，件")
    @TableField("use_item_measure")
    private String useItemMeasure;


    /**
     * 固定用量
     */
    @Schema(description = "固定用量")
    @TableField("fixed_use")
    private BigDecimal fixedUse;


    /**
     * 变动用量
     */
    @Schema(description = "变动用量")
    @TableField("vari_use")
    private BigDecimal variUse;


    /**
     * 辅助固定用量
     */
    @Schema(description = "辅助固定用量")
    @TableField("fixed_use_assist")
    private BigDecimal fixedUseAssist;


    /**
     * 辅助变动用量
     */
    @Schema(description = "辅助变动用量")
    @TableField("vari_use_assist")
    private BigDecimal variUseAssist;


    /**
     * 辅助计量单位
     */
    @Schema(description = "辅助计量单位")
    @TableField("item_measure_assist")
    private String itemMeasureAssist;


    /**
     * 用料类型,00:物料，01：BOM
     */
    @Schema(description = "用料类型,00:物料，01：BOM")
    @TableField("use_item_type")
    private String useItemType;

}
