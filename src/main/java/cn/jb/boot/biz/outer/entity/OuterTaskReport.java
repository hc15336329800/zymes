package cn.jb.boot.biz.outer.entity;

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
 * 外协任务报工表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-10 10:44:22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_outer_task_report")
@Schema(name = "OuterTaskReport1", description = "外协任务报工")
public class OuterTaskReport extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 任务ID
     */
    @Schema(description = "任务ID")
    @TableField("task_id")
    private String taskId;


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
     * 审核状态;00:待审核
     */
    @Schema(description = "审核状态;00:待审核")
    @TableField("review_status")
    private String reviewStatus;


    /**
     * 审核账号
     */
    @Schema(description = "审核账号")
    @TableField("review_user_id")
    private String reviewUserId;


    /**
     * 审核描述
     */
    @Schema(description = "审核描述")
    @TableField("review_desc")
    private String reviewDesc;


    /**
     * 审核时间
     */
    @Schema(description = "审核时间")
    @TableField("review_time")
    private LocalDateTime reviewTime;
}
