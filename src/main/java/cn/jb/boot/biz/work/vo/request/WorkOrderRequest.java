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
@Schema(name = "WorkOrderRequest", description = "激光切割分页请求参数")
public class WorkOrderRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "bomNoList")
    private List<String> bomNoList;
}
