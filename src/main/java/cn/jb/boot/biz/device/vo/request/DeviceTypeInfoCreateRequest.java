package cn.jb.boot.biz.device.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 设备类型信息表 新增请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-31 03:22:39
 */
@Data
@Schema(name = "DeviceTypeInfoCreateRequest", description = "设备类型信息 新增请求参数")
public class DeviceTypeInfoCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备类型名称
     */
    @Schema(description = "设备类型名称")
    private String name;

    @Schema(description = "备注")
    private String remark;

}
