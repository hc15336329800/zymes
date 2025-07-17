package cn.jb.boot.biz.shift.entity;

import cn.jb.boot.framework.com.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

/**
 * 班次设定表表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-07 14:26:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_shift_setting")
@Schema(name = "ShiftSetting", description = "班次设定表")
public class ShiftSetting extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 班次类型
     */
    @Schema(description = "班次类型")
    @TableField("shift_type")
    private String shiftType;


    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    @TableField("start_time")
    private LocalTime startTime;


    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    @TableField("end_time")
    private LocalTime endTime;


    /**
     * 周几;1:周一，2:周二，3:周三，4:周四，5:周五，6:周六，7周天
     */
    @Schema(description = "周几;1:周一，2:周二，3:周三，4:周四，5:周五，6:周六，7周天")
    @TableField("week_day")
    private Integer weekDay;

}
