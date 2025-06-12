package cn.jb.boot.biz.work.entity;

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
 * 工单表表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-08 22:33:55
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_work_order")
@Schema(name = "WorkOrder", description = "工单表")
public class WorkOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;


    /**
     * 班组  新增
     */
    @Schema(description = "班组")
    @TableField("group_id")
    private String groupId;


    /**
     * 主键Id
     */
    @Schema(description = "主键Id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 分配ID
     */
    @Schema(description = "分配ID")
    @TableField("alloc_id")
    private String allocId;


    /**
     * 订单明细ID
     */
    @Schema(description = "订单明细ID")
    @TableField("order_dtl_id")
    private String orderDtlId;


    /**
     * 工单号
     */
    @Schema(description = "工单号")
    @TableField("work_order_no")
    private String workOrderNo;


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
     * 计划生产数量
     */
    @Schema(description = "计划生产数量")
    @TableField("plan_total_count")
    private BigDecimal planTotalCount;


    /**
     * 已下达数量
     */
    @Schema(description = "已下达数量")
    @TableField("assign_count")
    private BigDecimal assignCount;


    /**
     * 正品数量
     */
    @Schema(description = "正品数量")
    @TableField("real_count")
    private BigDecimal realCount;


    /**
     * 待审核数量
     */
    @Schema(description = "待审核正品数量")
    @TableField("to_review_real_count")
    private BigDecimal toReviewRealCount;


    /**
     * 次品数量
     */
    @Schema(description = "次品数量")
    @TableField("deff_count")
    private BigDecimal deffCount;


    /**
     * 待审核次品数量
     */
    @Schema(description = "待审核次品数量")
    @TableField("to_review_deff_count")
    private BigDecimal toReviewDeffCount;


    /**
     * 设备Id
     */
    @Schema(description = "设备Id")
    @TableField("device_id")
    private String deviceId;


    /**
     * 部门名称
     */
    @Schema(description = "部门名称")
    @TableField("dept_id")
    private String deptId;


    /**
     * 定额工时
     */
    @Schema(description = "定额工时")
    @TableField("hours_fixed")
    private BigDecimal hoursFixed;


    /**
     * 班次
     */
    @Schema(description = "班次")
    @TableField("shift_type")
    private String shiftType;


    /**
     * 工序状态
     */
    @Schema(description = "工序状态")
    @TableField("proc_status")
    private String procStatus;


    /**
     * 数据状态
     */
    @Schema(description = "数据状态")
    @TableField("data_status")
    private String dataStatus;


    /**
     * 临时增加下达状态 ：  就绪  已下达
     */
    @Schema(description = "临时增加下达状态 ：  就绪 he 已下达")
    @TableField("state")
    private String state;



}
