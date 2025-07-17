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
 * 销售单表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-04 00:13:32
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_sale_order")
@Schema(name = "SaleOrder", description = "销售单")
public class SaleOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 销售单号
     */
    @Schema(description = "销售单号")
    @TableField("order_no")
    private String orderNo;


    /**
     * 客户名称
     */
    @Schema(description = "客户名称")
    @TableField("cust_name")
    private String custName;


    /**
     * 产品编码
     */
    @Schema(description = "产品编码")
    @TableField("item_no")
    private String itemNo;


    /**
     * 需求量
     */
    @Schema(description = "需求量")
    @TableField("need_num")
    private BigDecimal needNum;


    /**
     * 已审核的下单数量
     */
    @Schema(description = "已审核的下单数量")
    @TableField("approved_ordered_num")
    private BigDecimal approvedOrderedNum;


    /**
     * 已生产数量
     */
    @Schema(description = "已生产数量")
    @TableField("produced_num")
    private BigDecimal producedNum;


    /**
     * 工序编码
     */
    @Schema(description = "工序编码")
    @TableField("procedure_code")
    private String procedureCode;


    /**
     * 工序名称
     */
    @Schema(description = "工序名称")
    @TableField("procedure_name")
    private String procedureName;


    /**
     * 是否已下单
     */
    @Schema(description = "是否已下单")
    @TableField("order_status")
    private String orderStatus;



    /**
     * 订单类型  新
     * 订单类型  "销售单" ="01" "加急单" ="03" "追加计划" ="01" "月度单""04"
     */
    @Schema(description = "订单类型")
    @TableField("order_type")
    private String orderType;



}
