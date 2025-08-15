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
 * 工人报工明细表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-11 15:32:29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_worker_report_dtl")
@Schema(name = "WorkerReportDtl", description = "工人报工明细")
public class WorkerReportDtl extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @TableField("user_id")
    private String userId;


    /**
     * 分组Id
     */
    @Schema(description = "分组Id")
    @TableField("group_id")
    private String groupId;


    /**
     * 分组账户ID
     */
    @Schema(description = "分组账户ID")
    @TableField("group_uid")
    private String groupUid;


    /**
     * 报工类型
     */
    @Schema(description = "报工类型")
    @TableField("report_type")
    private String reportType;


    /**
     * 报工数量
     */
    @Schema(description = "报工数量")
    @TableField("report_count")
    private BigDecimal reportCount;


    /**
     * 归属用户数量
     */
    @Schema(description = "归属用户数量（加工件数）")
    @TableField("user_count")
    private BigDecimal userCount;


    /**
     * 报工ID
     */
    @Schema(description = "报工ID")
    @TableField("report_id")
    private String reportId;


    /**
     * 工单表Id
     */
    @Schema(description = "工单表Id")
    @TableField("work_order_id")
    private String workOrderId;

    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

}
