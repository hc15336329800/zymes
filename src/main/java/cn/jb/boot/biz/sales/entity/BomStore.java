package cn.jb.boot.biz.sales.entity;

import cn.jb.boot.framework.com.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 出入库流水表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-04 13:26:53
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_bom_store")
@Schema(name = "BomStore", description = "出入库流水")
public class BomStore extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 发运单Id
     */
    @Schema(description = "发运单Id")
    @TableField("delivery_id")
    private String deliveryId;


    /**
     * 产品编码
     */
    @Schema(description = "产品编码")
    @TableField("item_no")
    private String itemNo;


    /**
     * 出入库类型;01:出库，02:入库
     */
    @Schema(description = "出入库类型;01:出库，02:入库")
    @TableField("biz_type")
    private String bizType;


    /**
     * 状态;00:待确认，01:已确认
     */
    @Schema(description = "状态;00:待确认，01:已确认")
    @TableField("store_status")
    private String storeStatus;


    /**
     * 申请数量
     */
    @Schema(description = "申请数量")
    @TableField("item_count")
    private BigDecimal itemCount;


    /** 产品单位 */
//    @Schema(description = "产品单位")
//    @TableField("item_measure")
//    private String itemMeasure;


    /**
     * 确认时间
     */
    @Schema(description = "确认时间")
    @TableField("confirm_time")
    private LocalDateTime confirmTime;


    /**
     * 自动出入库标注;00:手动入库，01:自动入库
     */
    @Schema(description = "自动出入库标注;00:手动入库，01:自动入库")
    @TableField("auto_flag")
    private String autoFlag;


    /**
     * mes库存量
     */
    @Schema(description = "mes库存量")
    @TableField("mes_item_count")
    private BigDecimal mesItemCount;


    /**
     * erp库存量
     */
    @Schema(description = "erp库存量")
    @TableField("erp_item_count")
    private BigDecimal erpItemCount;

}
