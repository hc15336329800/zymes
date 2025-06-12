package cn.jb.boot.biz.outer.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 外协任务报工表 新增请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-10 10:44:22
 */
@Data
@Schema(name = "OuterTaskReportCreateRequest", description = "外协任务报工 新增请求参数")
public class OuterTaskReportCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @Schema(description = "任务ID")
    @NotEmpty(message = "任务ID不能为空")
    private String taskId;

    /**
     * 正品数量
     */
    @Schema(description = "正品数量")
    @NotNull(message = "正品数量不能为空")
    private BigDecimal realCount;

    /**
     * 次品数量
     */
    @Schema(description = "次品数量")
    @NotNull(message = "次品数量不能为空")
    private BigDecimal deffCount;


}
