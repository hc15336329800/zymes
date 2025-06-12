package cn.jb.boot.biz.device.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 维修单表 分页请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-01 16:15:25
 */
@Data
@Schema(name = "RepairOrderPageRequest", description = "维修单 分页请求参数")
public class RepairOrderPageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "开始时间 yyyy-MM-dd")
    private String beginTime;

    @Schema(description = "结束时间 yyyy-MM-dd")
    private String endTime;
    @Schema(description = "设备")
    private String deviceId;

    @Schema(description = "名称")
    private String name;
}
