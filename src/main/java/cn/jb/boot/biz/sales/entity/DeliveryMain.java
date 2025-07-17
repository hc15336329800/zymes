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
 * 发运单主表表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-04 12:56:26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_delivery_main")
@Schema(name = "DeliveryMain", description = "发运单主表")
public class DeliveryMain extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
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
     * 司机
     */
    @Schema(description = "司机")
    @TableField("driver")
    private String driver;


    /**
     * 确认状态;00:待确认，01:已确认
     */
    @Schema(description = "确认状态;00:待确认，01:已确认")
    @TableField("status")
    private String status;


    /**
     * 提交状态;00 未提交，01 已提交
     */
    @Schema(description = "提交状态;00 未提交，01 已提交")
    @TableField("commit_status")
    private String commitStatus;

}
