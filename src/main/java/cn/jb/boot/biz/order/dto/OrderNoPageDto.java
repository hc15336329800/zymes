package cn.jb.boot.biz.order.dto;

import lombok.Data;

/**
 * 销售审批分页查询请求 DTO
 */
@Data
public class OrderNoPageDto {

	/** 销售订单号 */
	private String orderNo;

	/** 业务类型 */
	private String bizType;

	/** 审批状态 */
	private String placeStatus;

	/** 申请起始日期 yyyy-MM-dd */
	private String applyStartDate;

	/** 申请结束日期 yyyy-MM-dd */
	private String applyEndDate;

	/** 当前页码，从1开始 */
	private Integer pageNum = 1;

	/** 每页条数 */
	private Integer pageSize = 10;
}
