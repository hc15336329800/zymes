package cn.jb.boot.biz.agvcar.entity;

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
 * AGV叉车信息保存
 * @Description
 * @Copyright Copyright (c) 2024
 * @author shihy
 * @since 2024-04-08 20:04:08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("agvtask")
@Schema(name = "AgvManageInfo", description = "AGV叉车信息保存")
public class AgvManageInfo extends BaseEntity {

    /** 主键 */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /** agv名称*/
    @Schema(description = "agv名称")
    @TableField("agvname")
    private String agvname;

    /** 起始命令字 */
    @Schema(description = "起始命令字")
    @TableField("beginCmd")
    private String beginCmd;

    /** 起始点 */
    @Schema(description = "起始点")
    @TableField("begin")
    private String begin;

    /** 结束命令字 */
    @Schema(description = "结束命令字")
    @TableField("endCmd")
    private String endCmd;

    /** 结束点 */
    @Schema(description = "结束点")
    @TableField("end")
    private String end;

    /** 搬运ID */
    @Schema(description = "搬运ID")
    @TableField("CarryID")
    private String CarryID;

    /** 任务插入时间 */
    @Schema(description = "任务插入时间")
    @TableField("datetime")
    private LocalDateTime datetime;
}

