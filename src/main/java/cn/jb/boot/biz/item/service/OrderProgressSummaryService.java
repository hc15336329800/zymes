package cn.jb.boot.biz.item.service;

import cn.jb.boot.biz.item.entity.OrderProgressSummary;
import java.util.List;

/** 订单进度汇总 Service */
public interface OrderProgressSummaryService {
	/** 查询前 100 条数据 */
	List<OrderProgressSummary> listTop100();
}
