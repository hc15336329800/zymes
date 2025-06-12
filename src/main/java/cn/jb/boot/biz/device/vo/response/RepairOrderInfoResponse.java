package cn.jb.boot.biz.device.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 维修单表 详情返回信息
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-01 16:15:25
 */
@Data
@Schema(name = "RepairOrderInfoResponse", description = "维修单 详情返回信息")
public class RepairOrderInfoResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

    /**
     * 维修单名称
     */
    @Schema(description = "维修单名称")
    private String name;

    /**
     * 设备
     */
    @Schema(description = "设备")
    private String deviceId;

    /**
     * 保修时间
     */
    @Schema(description = "保修时间")
    private LocalDateTime reportTime;

    /**
     * 维修结束时间
     */
    @Schema(description = "维修结束时间")
    private LocalDateTime repairTime;

    /**
     * 维修人ID
     */
    @Schema(description = "维修人ID")
    private String repairUid;

    /**
     * 维修结果
     */
    @Schema(description = "维修结果")
    private String repairResult;

    /**
     * 验收时间
     */
    @Schema(description = "验收时间")
    private LocalDateTime checkTime;

    /**
     * 验收人Id
     */
    @Schema(description = "验收人Id")
    private String checkUid;

}
