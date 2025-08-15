package cn.jb.boot.biz.work.vo.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 工人报工明细 更新请求参数
 */
@Data
@Schema(name = "WorkerReportDtlUpdateRequest", description = "工人报工明细 更新请求参数")
public class WorkerReportDtlUpdateRequest {

	@NotBlank
	@Schema(description = "报工明细ID")
	private String id;

	@NotNull
	@Schema(description = "加工件数")
	private BigDecimal userCount;

	@NotNull
	@Schema(description = "单价")
	private BigDecimal hoursFixed;

	@NotNull
	@Schema(description = "工资")
	private BigDecimal wages;

	@NotBlank
	@Schema(description = "备注（必填）")
	private String remark;
}
