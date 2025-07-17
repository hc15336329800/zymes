package cn.jb.boot.biz.sales.entity;

import cn.jb.boot.framework.com.entity.BaseEntity;
import cn.jb.boot.util.SnowFlake;
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
 * 下单详情流水审批表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-04 00:13:32
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_sale_order_place")
@Schema(name = "SaleOrderPlace", description = "下单详情流水审批")
public class SaleOrderPlace extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 订单号
     */
    @Schema(description = "订单号")
    @TableField("order_no")
    private String orderNo;


    /**
     * 销售单主键
     */
    @Schema(description = "销售单主键")
    @TableField("sale_id")
    private String saleId;


    /**
     * 产品编码
     */
    @Schema(description = "产品编码")
    @TableField("item_no")
    private String itemNo;


    /**
     * 承诺交期
     */
    @Schema(description = "承诺交期")
    @TableField("deliver_Time")
    private LocalDateTime deliverTime;


    /**
     * 优先级别
     * 优先级  "紧急" ="02" "加急" ="03" "正常" ="01" "延后""04"
     */
    @Schema(description = "业务类型")
    @TableField("biz_Type")
    private String bizType;


    /**
     * 下单数量
     */
    @Schema(description = "下单数量")
    @TableField("ordered_Num")
    private BigDecimal orderedNum;


    /**
     * 申请人
     */
    @Schema(description = "申请人")
    @TableField("apply_no")
    private String applyNo;


    /**
     * 申请人名称
     */
    @Schema(description = "申请人名称")
    @TableField("apply_name")
    private String applyName;


    /**
     * 申请时间
     */
    @Schema(description = "申请时间")
    @TableField("apply_time")
    private LocalDateTime applyTime;


    /**
     * 审批状态;00
     */
    @Schema(description = "审批状态;00")
    @TableField("place_status")
    private String placeStatus;


    /**
     * 审批原因
     */
    @Schema(description = "审批原因")
    @TableField("approval_msg")
    private String approvalMsg;

    public static void main(String[] args) {
        System.out.println(SnowFlake.genId());
    }

}
