package cn.jb.boot.biz.work.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 工单报工表表 详情返回信息
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-09 14:06:38
 */
@Data
@Schema(name = "WorkReportInfoResponse", description = "工单报工表 详情返回信息")
public class WorkReportInfoResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

    /**
     * 工单表主键
     */
    @Schema(description = "工单表主键")
    private String workOrderId;

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
     * 报工状态;01:待审核，02:待验收，03:验收通过
     */
    @Schema(description = "报工状态;01:待审核，02:待验收，03:验收通过")
    private String status;

    /**
     * 质检员时间
     */
    @Schema(description = "质检员时间")
    private LocalDateTime quaTime;

    /**
     * 质检员工号
     */
    @Schema(description = "质检员工号")
    private String quaUser;

    /**
     * 审核时间
     */
    @Schema(description = "审核时间")
    private LocalDateTime verifyTime;

    /**
     * 审核员工号
     */
    @Schema(description = "审核员工号")
    private String verifyUser;

}
