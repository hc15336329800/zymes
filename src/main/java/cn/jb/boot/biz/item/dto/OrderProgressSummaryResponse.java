package cn.jb.boot.biz.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(name = "OrderProgressSummaryResponse", description = "订单进度汇总信息")
public class OrderProgressSummaryResponse implements Serializable {

	private static final long serialVersionUID = 1L;

//	@Schema(description = "主键")
//	private String id;

	@Schema(description = "订单号")
	private String orderNo;


	@Schema(description = "BOM编号")
	private String bomNo;

	@Schema(description = "物料名称")
	private String itemName;

	@Schema(description = "客户名称")
	private String custName;


	@Schema(description = "需求数量")
	private BigDecimal needNum;


//	@Schema(description = "总工时")
//	private BigDecimal totalHours;
//
//	@Schema(description = "已完成工时")
//	private BigDecimal doneHours;

	@Schema(description = "进度百分比")
	private BigDecimal progressPercent;

	@Schema(description = "订单创建时间")
	private LocalDateTime createdTime;

	@Schema(description = "更新时间")
	private LocalDateTime updateTime;
}
