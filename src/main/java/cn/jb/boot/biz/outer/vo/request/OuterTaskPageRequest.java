package cn.jb.boot.biz.outer.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 外协任务表 分页请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-10 10:44:22
 */
@Data
@Schema(name = "OuterTaskPageRequest", description = "外协任务 分页请求参数")
public class OuterTaskPageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "开始时间 yyyy-MM-dd")
    private String beginTime;

    @Schema(description = "结束时间 yyyy-MM-dd")
    private String endTime;


    private String itemNo;

    private String userId;

    private List<String> procedureNames;

    private String acceptStatus;
}
