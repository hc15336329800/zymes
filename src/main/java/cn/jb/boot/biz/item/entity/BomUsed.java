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
 * bom用料依赖表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 16:55:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_bom_used")
@Schema(name = "BomUsed", description = "bom用料依赖")
public class BomUsed extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 物料编码
     */
    @Schema(description = "物料编码")
    @TableField("item_no")
    private String itemNo;


    /**
     * 用料编码
     */
    @Schema(description = "用料编码")
    @TableField("use_item_no")
    private String useItemNo;


    /**
     * 用料数量
     */
    @Schema(description = "用料数量")
    @TableField("use_item_count")
    private BigDecimal useItemCount;

    @Schema(description = "固定用量")
    @TableField("fixed_used")
    private BigDecimal fixedUsed;


    /**
     * 用料类型,0:物料，1：子件BOM 2：母件BOM
     */
    @Schema(description = "用料类型,00:物料，01：子件BOM ")
    @TableField("use_item_type")
    private String useItemType;

    @Schema(description = "父编码")
    @TableField("parent_code")
    private String parentCode;

    @Schema(description = "use表Id")
    @TableField("used_id")
    private String usedId;


    /**
     * 用料长编码
     */
    @Schema(description = "用料长编码")
    @TableField("item_nos")
    private String itemNos;


    /**
     * bom编码
     */
    @Schema(description = "bom编码")
    @TableField("bom_no")
    private String bomNo;

}
