package cn.jb.boot.biz.tray.vo.response;

import cn.jb.boot.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 托盘返回参数
 *
 * @author shihy
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-04-11 20:04:08
 */
@Data
@Schema(name = "TrayManageInfoResponse", description = "托盘返回数据")
public class TrayManageInfoResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

    /**
     * 托盘编码
     */
    @Schema(description = "托盘编码")
    private String trayid;

    /**
     * 物料编码
     */
    @Schema(description = "物料编码")
    private String itemno;

    /**
     * 物料名称
     */
    @Schema(description = "物料名称")
    private String itemname;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 物料件数
     */
    @Schema(description = "物料件数")
    private String realCount;

    /**
     * 库位码
     */
    @Schema(description = "库位码")
    private String location;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime updatedtime;
}
