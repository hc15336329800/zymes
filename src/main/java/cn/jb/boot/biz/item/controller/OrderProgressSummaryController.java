package cn.jb.boot.biz.item.controller;

import cn.jb.boot.biz.item.dto.OrderProgressSummaryResponse;
import cn.jb.boot.biz.item.entity.OrderProgressSummary;
import cn.jb.boot.biz.item.service.OrderProgressSummaryService;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.util.MsgUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import cn.jb.boot.util.PojoUtil;

/**
 * 查询订单进度汇总的控制器
 */
@RestController
@RequestMapping("/api/item/orderProgressSummary")
@Tag(name = "orderProgressSummary-controller", description = "订单进度汇总接口")
public class OrderProgressSummaryController {

	@Resource
	private OrderProgressSummaryService service;


	/**
	 * 获取 t_order_progress_summary 表前 100 条记录  DTO
	 */
	@PostMapping("/list")
	@Operation(summary = "查询订单进度汇总列表（最多100条）")
	public BaseResponse<List<OrderProgressSummaryResponse>> list() {
		List<OrderProgressSummary> list = service.listTop100();
		List<OrderProgressSummaryResponse> respList =
				PojoUtil.copyList(list, OrderProgressSummaryResponse.class);
		return MsgUtil.ok(respList);
	}


	@PostMapping("/lists")
	@Operation(summary = "查询订单进度汇总列表（最多100条）")
	public BaseResponse<List<OrderProgressSummaryResponse>> lists() {
		// 模拟数据构造
		List<OrderProgressSummaryResponse> respList = new ArrayList<>();

		OrderProgressSummaryResponse item1 = new OrderProgressSummaryResponse();
		item1.setOrderNo("202507264015");
		item1.setBomNo("2516001917-1");
		item1.setItemName("U型板焊合");
		item1.setCustName("2516001917-1");
		item1.setNeedNum(new BigDecimal("2"));
		item1.setProgressPercent(new BigDecimal("100"));

		item1.setCreatedTime(null);
		item1.setUpdateTime(null);
		respList.add(item1);

		OrderProgressSummaryResponse item2 = new OrderProgressSummaryResponse();
		item2.setOrderNo("202507266607");
		item2.setBomNo("2516004015-1");
		item2.setItemName("后尾板焊合");
		item2.setCustName("2516004015-1");
		item2.setNeedNum(new BigDecimal("2"));
		item2.setProgressPercent(new BigDecimal("45.16"));

		item2.setCreatedTime(null);
		item2.setUpdateTime(null);
		respList.add(item2);

		return MsgUtil.ok(respList);
	}



}
