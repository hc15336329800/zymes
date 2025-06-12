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
 * 点检信息表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-01 20:04:08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_check_info")
@Schema(name = "CheckInfo", description = "点检信息")
public class CheckInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 计划名称
     */
    @Schema(description = "计划名称")
    @TableField("name")
    private String name;


    /**
     * 类型;01:点检计划，02:保养计划
     */
    @Schema(description = "类型;01:点检计划，02:保养计划")
    @TableField("check_type")
    private String checkType;


    /**
     * 设备id
     */
    @Schema(description = "设备id")
    @TableField("device_id")
    private String deviceId;


    /**
     * 项目Id
     */
    @Schema(description = "项目Id")
    @TableField("item_id")
    private String itemId;


    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    @TableField("begin_time")
    private LocalDateTime beginTime;


    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    @TableField("end_time")
    private LocalDateTime endTime;


    /**
     * 用户Id
     */
    @Schema(description = "用户Id")
    @TableField("user_id")
    private String userId;

    @TableField("remark")
    private String remark;

}
