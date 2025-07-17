package cn.jb.boot.biz.work.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 工单表表 分页请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-07 21:52:39
 */
@Data
@Schema(name = "WorkOrderPageRequest", description = "工单表 分页请求参数")
public class WorkOrderPageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    // ★★★ 新增字段：当前登录用户 ID，用于数据权限过滤
    @Schema(description = "当前登录用户 ID，自动填充，无需前端传参")
    private String userId;


    @Schema(description = "车间ID")
    private String deptId;
    @Schema(description = "设备ID")
    private String deviceId;
    @Schema(description = "产品编码")
    private String itemNo;
    @Schema(description = "工序名称")
    private List<String> procedureNames;

    @Schema(description = "工单号")
    private String workOrderNo;
    @Schema(description = "status")
    private String status;


}
