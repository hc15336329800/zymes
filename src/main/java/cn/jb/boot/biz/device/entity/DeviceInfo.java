package cn.jb.boot.biz.device.entity;

import cn.jb.boot.framework.com.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备信息表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-31 04:46:16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_device_info")
@Schema(name = "DeviceInfo", description = "设备信息")
public class DeviceInfo extends BaseEntity {

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
    @Schema(description = "设备名称")
    @TableField("device_name")
    private String deviceName;


    /**
     * 设备编号
     */
    @Schema(description = "设备编号")
    @TableField("device_no")
    private String deviceNo;


    /**
     * 品牌
     */
    @Schema(description = "品牌")
    @TableField("brand")
    private String brand;


    /**
     * 设备厂商
     */
    @Schema(description = "设备厂商")
    @TableField("manufacturer")
    private String manufacturer;


    /**
     * 所属车间
     */
    @Schema(description = "所属车间")
    @TableField("dept_id")
    private String deptId;


    /**
     * 设备状态
     */
    @Schema(description = "设备状态")
    @TableField("data_status")
    private String dataStatus;


    /**
     * 设备类型ID
     */
    @Schema(description = "设备类型ID")
    @TableField("type_id")
    private String typeId;

    @TableField("is_valid")
    private String isValid;

}
