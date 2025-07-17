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
import java.time.LocalDateTime;

/**
 * 工单报工表表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-09 14:06:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_work_report")
@Schema(name = "WorkReport", description = "工单报工表")
public class WorkReport extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 工单表主键
     */
    @Schema(description = "工单表主键")
    @TableField("work_order_id")
    private String workOrderId;


    /**
     * 正品数量
     */
    @Schema(description = "正品数量")
    @TableField("real_count")
    private BigDecimal realCount;


    /**
     * 次品数量
     */
    @Schema(description = "次品数量")
    @TableField("deff_count")
    private BigDecimal deffCount;


    /**
     * 报工状态;01:待审核，02:待验收，03:验收通过
     */
    @Schema(description = "报工状态;01:待审核，02:待验收，03:验收通过")
    @TableField("status")
    private String status;


    /**
     * 质检员时间
     */
    @Schema(description = "质检员时间")
    @TableField("qua_time")
    private LocalDateTime quaTime;


    /**
     * 质检员工号
     */
    @Schema(description = "质检员工号")
    @TableField("qua_user")
    private String quaUser;


    /**
     * 审核时间
     */
    @Schema(description = "审核时间")
    @TableField("verify_time")
    private LocalDateTime verifyTime;


    /**
     * 审核员工号
     */
    @Schema(description = "审核员工号")
    @TableField("verify_user")
    private String verifyUser;

    @TableField("remark")
    private String remark;
    @TableField("group_id")
    private String groupId;

    @TableField("report_type")
    private String reportType;


    @TableField(exist = false)
    private String pid;

}
