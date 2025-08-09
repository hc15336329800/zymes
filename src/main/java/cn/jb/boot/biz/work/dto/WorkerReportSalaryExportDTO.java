package cn.jb.boot.biz.work.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class WorkerReportSalaryExportDTO implements Serializable {
	private String orderNo;        // 订单号
	private String workOrderNo;    // 工单号
	private String bomNo;          // 图纸号
	private String itemNo;         // 产品编码
	private String procedureName;  // 工序名称
//	private String userId;         // 工人ID
	private String userName;       // 工人姓名

	private String userCount;      // 加工件数
	private String hoursFixed;     // 单价
	private String wages;          // 工资

//	private String createDate;     // 创建日期（仅日期部分）
	private String createdTime;    // 报工时间（yyyy-MM-dd HH:mm:ss）


}
