package cn.jb.boot.biz.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 激光报工汇总返回对象 */
@Data
@Schema(name = "LaserReportSummarySto", description = "激光报工汇总信息")
public class LaserReportSummarySto {

	@Schema(description = "主键ID")
	private Integer id;

	@Schema(description = "订单号")
	private String orderNo;

	@Schema(description = "设备ID")
	private Long deviceId;

	@Schema(description = "设备名称")
	private String deviceName;

	@Schema(description = "母件BOM编号")
	private String motherBomNo;

	@Schema(description = "子件BOM编号")
	private String childBomNo;

	@Schema(description = "子件物料")
	private String useItemNo;

	@Schema(description = "报工次数")
	private Integer reportCount;

	@Schema(description = "完成数量")
	private BigDecimal finishedQty;

	@Schema(description = "最近报工时间")
	private LocalDateTime lastReportTime;

	@Schema(description = "计划交期")
	private LocalDateTime deliverTime;


}
