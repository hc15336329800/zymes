package cn.jb.boot.biz.outer.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 外协任务报工表 修改请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-10 10:44:22
 */
@Data
@Schema(name = "OuterTaskReportUpdateRequest", description = "外协任务报工 修改请求参数")
public class OuterTaskReportUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    @NotBlank(message = "id不能为空")
    private String id;

    /**
     * 任务ID
     */
    @Schema(description = "任务ID")
    private String taskId;

    /**
     * 正品数量
     */
    @Schema(description = "正品数量")
    private BigDecimal realCount;

    /**
     * 次品数量
     */
    @Schema(description = "次品数量")
    private BigDecimal deffCount;

    /**
     * 审核状态;00:待审核
     */
    @Schema(description = "审核状态;00:待审核")
    private String reviewStatus;

    /**
     * 审核账号
     */
    @Schema(description = "审核账号")
    private String reviewUserId;

    /**
     * 审核描述
     */
    @Schema(description = "审核描述")
    private String reviewDesc;

    /**
     * 审核时间
     */
    @Schema(description = "审核时间")
    private LocalDateTime reviewTime;

}
