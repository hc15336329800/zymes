package cn.jb.boot.biz.tabularAnalysis.entity;

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
 * 托盘物料信息保存
 *
 * @author shihy
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-04-11 20:04:08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_tray_info")
@Schema(name = "TrayManageInfo", description = "托盘信息保存")
public class TrayManageInfo extends BaseEntity {

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 托盘编码
     */
    @Schema(description = "托盘编码")
    @TableField("trayid")
    private String trayid;

    /**
     * 物料编码
     */
    @Schema(description = "物料编码")
    @TableField("procedure_code")
    private String procedure_code;

    /**
     * 物料名称
     */
    @Schema(description = "物料名称")
    @TableField("procedure_name")
    private String procedure_name;

    /**
     * 物料件数
     */
    @Schema(description = "物料件数")
    @TableField("procedure_count")
    private String procedure_count;

    /**
     * 库位码
     */
    @Schema(description = "库位码")
    @TableField("location_code")
    private String location_code;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField("updated_time")
    private LocalDateTime updated_time;
}
