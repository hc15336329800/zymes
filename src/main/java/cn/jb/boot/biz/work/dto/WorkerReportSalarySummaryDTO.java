package cn.jb.boot.biz.work.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 工人工资汇总导出 DTO
 */
@Data
public class WorkerReportSalarySummaryDTO implements Serializable {

	/** 工人ID */
	private String userId;

	/** 工人姓名 */
	private String userName;

	/** 总工资 */
	private String totalWages;
}
