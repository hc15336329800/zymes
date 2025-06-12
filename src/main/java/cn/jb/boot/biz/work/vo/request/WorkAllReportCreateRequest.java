package cn.jb.boot.biz.work.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 工单报工表表 新增请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-09 14:06:38
 */
@Data
@Schema(name = "WorkReportCreateRequest", description = "工单报工表 新增请求参数")
public class WorkAllReportCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单表主键
     */
    @Schema(description = "工单表主键")
    @NotEmpty(message = "ids")
    private List<String> ids;

    @NotEmpty(message = "分组Id不能为空")
    private String groupId;

    @NotEmpty(message = "报工类型")
    private String reportType;


}
