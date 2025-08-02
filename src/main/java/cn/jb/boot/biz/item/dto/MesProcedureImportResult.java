package cn.jb.boot.biz.item.dto;

import java.util.ArrayList;
import java.util.List;

public class MesProcedureImportResult {

	private int successCount;
	private int failCount;
	private List<FailDetail> failList = new ArrayList<>();
	// 可以扩展：成功明细等

	public int getSuccessCount() { return successCount; }
	public void setSuccessCount(int successCount) { this.successCount = successCount; }
	public int getFailCount() { return failCount; }
	public void setFailCount(int failCount) { this.failCount = failCount; }
	public List<FailDetail> getFailList() { return failList; }
	public void setFailList(List<FailDetail> failList) { this.failList = failList; }

	// 内部静态类
	public static class FailDetail {
		private int rowNum; // Excel 行号
		private String bomNo;
		private String reason;

		public int getRowNum() { return rowNum; }
		public void setRowNum(int rowNum) { this.rowNum = rowNum; }
		public String getBomNo() { return bomNo; }
		public void setBomNo(String bomNo) { this.bomNo = bomNo; }
		public String getReason() { return reason; }
		public void setReason(String reason) { this.reason = reason; }
	}
}
