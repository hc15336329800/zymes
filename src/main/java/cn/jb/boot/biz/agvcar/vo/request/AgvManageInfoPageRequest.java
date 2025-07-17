package cn.jb.boot.biz.agvcar.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
* 点检信息表 分页请求参数
* @Description
* @Copyright Copyright (c) 2024
* @author lxl
* @since 2024-01-01 20:04:08
*/
@Data
@Schema(name = "AgvManageInfoPageRequest", description = "点检信息 分页请求参数")
public class AgvManageInfoPageRequest implements Serializable {

    @Schema(description = "agv名称")
    private String agvName;

    @Schema(description = "起始命令字")
    private String beginCmd;

    @Schema(description = "起始工位")
    private String begin;

    @Schema(description = "起始工位")
    private String kuwei;

    @Schema(description = "结束命令字")
    private String endCmd;

    @Schema(description = "结束点")
    private String end;

    @Schema(description = "结束点工位")
    private String location;

    @Schema(description = "搬运ID")
    private String CarryID;

    @Schema(description = "任务插入时间")
    private String datetime;
}
