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

/**
 * 发运单明细表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-04 12:56:26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_delivery_dtl")
@Schema(name = "DeliveryDtl", description = "发运单明细")
public class DeliveryDtl extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 主表ID
     */
    @Schema(description = "主表ID")
    @TableField("delivery_id")
    private String deliveryId;


    /**
     * 产品编码
     */
    @Schema(description = "产品编码")
    @TableField("item_no")
    private String itemNo;


    /**
     * 计划数量
     */
    @Schema(description = "计划数量")
    @TableField("plan_count")
    private BigDecimal planCount;


    /**
     * 产品单位
     */
    @Schema(description = "产品单位")
    @TableField("item_measure")
    private String itemMeasure;


    /**
     * 发货数量
     */
    @Schema(description = "发货数量")
    @TableField("real_count")
    private BigDecimal realCount;


    /**
     * 装车人
     */
    @Schema(description = "装车人")
    @TableField("loader")
    private String loader;


    /**
     * 卡车司机
     */
    @Schema(description = "卡车司机")
    @TableField("truck")
    private String truck;


    /**
     * 卡车号
     */
    @Schema(description = "卡车号")
    @TableField("truck_no")
    private String truckNo;


    /**
     * 提交状态;00:未提交，01:已提交
     */
    @Schema(description = "提交状态;00:未提交，01:已提交")
    @TableField("commit_status")
    private String commitStatus;


    @TableField("remark")
    private String remark;

    @TableField(exist = false)
    private String bomNo;

}
