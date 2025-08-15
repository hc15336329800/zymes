package cn.jb.boot.biz.work.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 工人报工明细 新增请求参数
 */
@Data
@Schema(name = "WorkerReportDtlCreateRequest", description = "工人报工明细 新增请求参数")
public class WorkerReportDtlCreateRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotBlank
	@Schema(description = "用户ID")
	private String userId;

	@NotBlank
	@Schema(description = "分组ID")
	private String groupId;

	@NotBlank
	@Schema(description = "分组账户ID")
	private String groupUid;

	@NotBlank
	@Schema(description = "报工类型")
	private String reportType;

	@NotNull
	@Schema(description = "报工数量")
	private BigDecimal reportCount;

	@NotNull
	@Schema(description = "归属用户数量（加工件数）")
	private BigDecimal userCount;

	@NotBlank
	@Schema(description = "报工ID")
	private String reportId;

	@NotBlank
	@Schema(description = "工单表ID")
	private String workOrderId;

	@Schema(description = "备注")
	private String remark;
}
