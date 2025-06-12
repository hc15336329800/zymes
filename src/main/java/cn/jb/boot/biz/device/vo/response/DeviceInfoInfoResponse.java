package cn.jb.boot.biz.device.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 设备信息表 详情返回信息
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-31 04:46:16
 */
@Data
@Schema(name = "DeviceInfoInfoResponse", description = "设备信息 详情返回信息")
public class DeviceInfoInfoResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 设备编号
     */
    @Schema(description = "设备编号")
    private String deviceNo;

    /**
     * 品牌
     */
    @Schema(description = "品牌")
    private String brand;

    /**
     * 设备厂商
     */
    @Schema(description = "设备厂商")
    private String manufacturer;

    /**
     * 所属车间
     */
    @Schema(description = "所属车间")
    private String deptId;

    /**
     * 设备状态
     */
    @Schema(description = "设备状态")
    private String dataStatus;

    /**
     * 设备类型ID
     */
    @Schema(description = "设备类型ID")
    private String typeId;

}
