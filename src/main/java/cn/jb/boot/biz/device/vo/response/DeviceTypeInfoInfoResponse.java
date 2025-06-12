package cn.jb.boot.biz.device.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 设备类型信息表 详情返回信息
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-31 03:22:39
 */
@Data
@Schema(name = "DeviceTypeInfoInfoResponse", description = "设备类型信息 详情返回信息")
public class DeviceTypeInfoInfoResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

    /**
     * 设备类型名称
     */
    @Schema(description = "设备类型名称")
    private String name;

    @Schema(description = "备注")
    private String remark;

}
