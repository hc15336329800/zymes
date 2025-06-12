package cn.jb.boot.biz.item.vo.response;

import cn.jb.boot.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 设备工位信息表 分页返回参数
 *
 * @author shihy
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-04-18 00:44:38
 */
@Data
@Schema(name = "DeviceLocationPageResponse", description = "设备工位 分页返回参数")
public class DeviceLocationPageResponse implements Serializable {

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
    private String device;

    /**
     * 工位
     */
    @Schema(description = "工位")
    private String station;

    /**
     * 库位
     */
    @Schema(description = "库位")
    private String location;

    /**
     * agv点位
     */
    @Schema(description = "agv点位")
    private String agvlocation;
    /**
     * 是否有货（0：有货 1：无货）
     */
    @Schema(description = "是否有货")
    private String status;

}
