package cn.jb.boot.biz.work.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
public class WorkReportCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单表主键
     */
    @Schema(description = "工单表主键")
    @NotEmpty(message = "工单ID不能为空")
    private String workOrderId;

    /**
     * 正品数量
     */
    @Schema(description = "正品数量")
    @NotNull(message = "正品数量不为空")
    private BigDecimal realCount;

    /**
     * 次品数量
     */
    @Schema(description = "次品数量")
    @NotNull(message = "次品数量不为空")
    private BigDecimal deffCount;

    @NotEmpty(message = "分组Id不能为空")
    private String groupId;

    @NotEmpty(message = "报工类型")
    private String reportType;


}
