package cn.jb.boot.biz.work.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 工人报工明细表 分页请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-11 15:32:29
 */
@Data
@Schema(name = "WorkerReportDtlPageRequest", description = "工人报工明细 分页请求参数")
public class WorkerReportDetailPageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "开始时间 yyyy-MM-dd")
    private String beginTime;

    @Schema(description = "结束时间 yyyy-MM-dd")
    private String endTime;


    private String userId;

    private String itemNo;

    @Schema(description = "BOM编号")
    private String bomNo;          // 新增字段


    private List<String> procedureNames;


    private String workOrderNo;


}
