package cn.jb.boot.biz.device.entity;

import cn.jb.boot.framework.com.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 维修单表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-01 16:15:25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_repair_order")
@Schema(name = "RepairOrder", description = "维修单")
public class RepairOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 维修单名称
     */
    @Schema(description = "维修单名称")
    @TableField("name")
    private String name;


    /**
     * 设备
     */
    @Schema(description = "设备")
    @TableField("device_id")
    private String deviceId;


    /**
     * 保修时间
     */
    @Schema(description = "保修时间")
    @TableField("report_time")
    private LocalDateTime reportTime;


    /**
     * 维修结束时间
     */
    @Schema(description = "维修结束时间")
    @TableField("repair_time")
    private LocalDateTime repairTime;


    /**
     * 维修人ID
     */
    @Schema(description = "维修人ID")
    @TableField("repair_uid")
    private String repairUid;


    /**
     * 维修结果
     */
    @Schema(description = "维修结果")
    @TableField("repair_result")
    private String repairResult;


    /**
     * 验收时间
     */
    @Schema(description = "验收时间")
    @TableField("check_time")
    private LocalDateTime checkTime;


    /**
     * 验收人Id
     */
    @Schema(description = "验收人Id")
    @TableField("check_uid")
    private String checkUid;

}
