package cn.jb.boot.biz.work.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 工序分配记录表 分页请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-08 18:34:33
 */
@Data
@Schema(name = "WorkOrderRecordPageRequest", description = "工序分配记录 分页请求参数")
public class WorkOrderRecordPageRequest implements Serializable {

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
    @Schema(description = "工序名称")
    private List<String> procedureNames;


}
