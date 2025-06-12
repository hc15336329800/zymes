package cn.jb.boot.biz.device.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 设备信息表 修改请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-31 04:46:16
 */
@Data
@Schema(name = "DeviceInfoUpdateRequest", description = "设备信息 修改请求参数")
public class DeviceInfoUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    @NotBlank(message = "id不能为空")
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
