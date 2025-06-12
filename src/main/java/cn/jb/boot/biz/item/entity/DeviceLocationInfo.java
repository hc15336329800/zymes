package cn.jb.boot.biz.item.entity;

import cn.jb.boot.framework.com.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库位信息表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-02 00:44:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_device_location")
@Schema(name = "DeviceLocationInfo", description = "设备工位信息")
public class DeviceLocationInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 设备名称
     */
    @Schema(description = "设备")
    @TableField("device")
    private String device;

    /**
     * 工位名称
     */
    @Schema(description = "工位")
    @TableField("station")
    private String station;

    /**
     * 库位名称
     */
    @Schema(description = "库位")
    @TableField("location")
    private String location;

    /**
     * agv点位名称
     */
    @Schema(description = "agv点位")
    @TableField("agvlocation")
    private String agvlocation;

    /**
     * 是否有货（0：有货 1：无货）
     */
    @Schema(description = "是否有货")
    @TableField("status")
    private String status;

}
