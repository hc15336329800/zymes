package cn.jb.boot.biz.device.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 设备类型信息表 修改请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-31 03:22:39
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "DeviceTypeInfoUpdateRequest", description = "设备类型信息 修改请求参数")
public class DeviceTypeInfoUpdateRequest extends DeviceTypeInfoCreateRequest {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    @NotBlank(message = "id不能为空")
    private String id;


}
