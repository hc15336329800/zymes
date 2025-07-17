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
 * mes与ERP物料出入库流水表表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 16:55:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_mes_erp_item_log")
@Schema(name = "MesErpItemLog", description = "mes与ERP物料出入库流水表")
public class MesErpItemLog extends BaseEntity {

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
     * 图纸号
     */
    @Schema(description = "图纸号")
    @TableField("bom_no")
    private String bomNo;


    /**
     * 全链路唯一ID
     */
    @Schema(description = "全链路唯一ID")
    @TableField("uni_id")
    private Integer uniId;


    /**
     * mes变动量
     */
    @Schema(description = "mes变动量")
    @TableField("mes_count")
    private BigDecimal mesCount;


    /**
     * 总量
     */
    @Schema(description = "总量")
    @TableField("total_count")
    private BigDecimal totalCount;


    /**
     * 物料业务类型;01:MES入库，02：MES_出库
     */
    @Schema(description = "物料业务类型;01:MES入库，02：MES_出库")
    @TableField("biz_type")
    private String bizType;

}
