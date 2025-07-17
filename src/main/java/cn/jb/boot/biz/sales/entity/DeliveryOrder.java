package cn.jb.boot.biz.sales.entity;

import cn.jb.boot.framework.com.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 发货申请表表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-04 12:56:26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_delivery_order")
@Schema(name = "DeliveryOrder", description = "发货申请表")
public class DeliveryOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 目的地
     */
    @Schema(description = "目的地")
    @TableField("destination")
    private String destination;


    /**
     * 客户
     */
    @Schema(description = "客户")
    @TableField("customer")
    private String customer;


    /**
     * 发货日期
     */
    @Schema(description = "发货日期")
    @TableField("delivery_date")
    private LocalDate deliveryDate;


    /**
     * 发货人
     */
    @Schema(description = "发货人")
    @TableField("deliverer")
    private String deliverer;


    /**
     * 接收人
     */
    @Schema(description = "接收人")
    @TableField("accepted_by")
    private String acceptedBy;


    /**
     * 门卫
     */
    @Schema(description = "门卫")
    @TableField("doorman")
    private String doorman;


    /**
     * 制作单人
     */
    @Schema(description = "制作单人")
    @TableField("prepared_by")
    private String preparedBy;


    /**
     * 质检员
     */
    @Schema(description = "质检员")
    @TableField("quality_by")
    private String qualityBy;


    /**
     * 用框量
     */
    @Schema(description = "用框量")
    @TableField("box_num")
    private String boxNum;


    /**
     * 确认状态;00:待确认，01:已确认
     */
    @Schema(description = "确认状态;00:待确认，01:已确认")
    @TableField("status")
    private String status;


    /**
     * 提交状态 00 未提交，01 已提交
     */
    @Schema(description = "提交状态 00 未提交，01 已提交")
    @TableField("commit_status")
    private String commitStatus;


    /**
     * 主表id
     */
    @Schema(description = "主表id")
    @TableField("main_id")
    private String mainId;


    @TableField(exist = false)
    private String driver;


}
