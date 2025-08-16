package cn.jb.boot.biz.work.vo.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 工人报工明细 新增请求参数
 */
@Data
@Schema(name = "WorkerReportDtlCreateRequest", description = "工人报工明细 新增请求参数")
public class WorkerReportDtlCreateRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@Schema(description = "用户ID")
	private String userId;                      // ★保留原字段

	@Schema(description = "工单号")
	private String workOrderNo;                 // ★新增

	@Schema(description = "订单号")
	private String orderNo;                     // ★新增

	@Schema(description = "BOM 编码")
	private String bomNo;                       // ★新增

	@Schema(description = "工序名称")
	private String procedureName;               // ★新增

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Schema(description = "报工时间")
	private LocalDateTime createdTime;          // ★新增


	@Schema(description = "加工件数")
	private BigDecimal userCount;               // ★原字段重用

	@Schema(description = "单价")
	private BigDecimal hoursFixed;              // ★新增（前端展示用）

	@Schema(description = "工资")
	private BigDecimal wages;                   // ★新增（前端展示用）

	@Schema(description = "备注")
	private String remark;                      // ★保留原字段
}
