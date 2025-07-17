package cn.jb.boot.biz.order.entity;

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
 * 订单明细表表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-05 19:00:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order_dtl")
@Schema(name = "OrderDtl", description = "订单明细表")
public class OrderDtl extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 订单号
     */
    @Schema(description = "订单号")
    @TableField("order_no")
    private String orderNo;


    /**
     * 产品编码
     */
    @Schema(description = "产品编码")
    @TableField("item_no")
    private String itemNo;


    /**
     * 产品数量
     */
    @Schema(description = "产品数量")
    @TableField("item_count")
    private BigDecimal itemCount;


    /**
     * 已生产数量
     */
    @Schema(description = "已生产数量")
    @TableField("production_count")
    private BigDecimal productionCount;


    /**
     * 订单明细状态;01:就绪，02:排程中
     */
    @Schema(description = "订单明细状态;01:就绪，02:排程中")
    @TableField("order_dtl_status")
    private String orderDtlStatus;


    /**
     * 待计算中间件数量
     */
    @Schema(description = "待计算中间件数量")
    @TableField("need_mid_count")
    private BigDecimal needMidCount;


    @Schema(description = "待计算中间件数量")
    @TableField(exist = false)
    private BigDecimal midCount = BigDecimal.ZERO;

    @Schema(description = "层级序号")
    @TableField(exist = false)
    private Integer seqNo = -1;

}
