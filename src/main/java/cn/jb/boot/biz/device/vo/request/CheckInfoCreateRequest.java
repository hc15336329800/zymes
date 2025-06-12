package cn.jb.boot.biz.device.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 点检信息表 新增请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-01 20:04:08
 */
@Data
@Schema(name = "CheckInfoCreateRequest", description = "点检信息 新增请求参数")
public class CheckInfoCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 计划名称
     */
    @Schema(description = "计划名称")
    private String name;

    /**
     * 类型;01:点检计划，02:保养计划
     */
    @Schema(description = "类型;01:点检计划，02:保养计划")
    private String checkType;

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String deviceId;

    /**
     * 项目Id
     */
    @Schema(description = "项目Id")
    private String itemId;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private LocalDateTime beginTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    /**
     * 用户Id
     */
    @Schema(description = "用户Id")
    private String userId;
    private String remark;

}
