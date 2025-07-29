package cn.jb.boot.biz.item.service;


import cn.jb.boot.biz.item.entity.LaserReportSummary;
import java.util.List;

public interface LaserReportSummaryService {
	/** 查询前 100 条数据 */
	List<LaserReportSummary> listTop100();
}
