package cn.jb.boot.biz.work.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 工单下达表表 分页请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-09 14:06:38
 */
@Data
@Schema(name = "WorkAssignPageRequest", description = "工单下达表 分页请求参数")
public class WorkAssignPageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "开始时间 yyyy-MM-dd")
    private String beginTime;

    @Schema(description = "结束时间 yyyy-MM-dd")
    private String endTime;

    @Schema(description = "车间ID")
    private String deptId;
    @Schema(description = "设备ID")
    private String deviceId;
    @Schema(description = "产品编码")
    private String itemNo;

    @Schema(description = "图纸号")
    private String bomNo;

    @Schema(description = "工序名称")
    private List<String> procedureNames;

    @Schema(description = "工单号")
    private String workOrderNo;
}
