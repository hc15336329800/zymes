package cn.jb.boot.biz.tray.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 托盘请求参数
 *
 * @author shihy
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-04-11 20:04:08
 */
@Data
@Schema(name = "TrayManageInfoRequest", description = "托盘请求参数")
public class TrayManageInfoRequest implements Serializable {

    @Schema(description = "主键")
    private String id;

    @Schema(description = "托盘编码")
    private String trayid;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "物料名称")
    private String itemname;

    @Schema(description = "物料编码")
    private String itemno;

    @Schema(description = "物料件数")
    private String realCount;

    @Schema(description = "库位码")
    private String location;

    @Schema(description = "创建时间")
    private String updatedtime;

    @Schema(description = "状态")
    private String status;
}
