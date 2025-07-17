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
 * 工序分配表表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-05 19:00:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_proc_allocation")
@Schema(name = "ProcAllocation", description = "工序分配表")
public class ProcAllocation extends BaseEntity {

    private static final long serialVersionUID = 1L;


    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 订单明细Id
     */
    @Schema(description = "订单明细Id")
    @TableField("order_dtl_id")
    private String orderDtlId;


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
     * 工序总数
     */
    @Schema(description = "工序总数")
    @TableField("total_count")
    private BigDecimal totalCount;


    /**
     * 外协分配数量
     */
    @Schema(description = "外协分配数量")
    @TableField("outer_alloc_count")
    private BigDecimal outerAllocCount;


    /**
     * 工厂分配数量
     */
    @Schema(description = "工厂分配数量")
    @TableField("worker_alloc_count")
    private BigDecimal workerAllocCount;


    /**
     * 设备编码
     */
    @Schema(description = "设备编码")
    @TableField("device_id")
    private String deviceId;


    /**
     * 定额工时
     */
    @Schema(description = "定额工时")
    @TableField("hours_fixed")
    private BigDecimal hoursFixed;


    /**
     * 状态;00:暂停
     */
    @Schema(description = "状态;00:暂停")
    @TableField("proc_status")
    private String procStatus;


    /**
     * 工作车间名称
     */
    @Schema(description = "工作车间名称")
    @TableField("dept_id")
    private String deptId;


    /**
     * 已生产数量
     */
    @Schema(description = "已生产数量")
    @TableField("prod_count")
    private BigDecimal prodCount;


    /**
     * 原始总数
     */
    @Schema(description = "原始总数")
    @TableField("orgi_total_count")
    private BigDecimal orgiTotalCount;


    /**
     * 中间件使用件数
     */
    @Schema(description = "中间件使用件数")
    @TableField("mid_count")
    private BigDecimal midCount;


    /**
     * 工序序号
     */
    @Schema(description = "工序序号")
    @TableField("seq_no")
    private Integer seqNo;


    /**
     * 外协生产数量
     */
    @Schema(description = "外协生产数量")
    @TableField("outer_prod_count")
    private BigDecimal outerProdCount;


    /**
     * 外协发布数量
     */
    @Schema(description = "外协发布数量")
    @TableField("outer_pub_count")
    private BigDecimal outerPubCount;
}
