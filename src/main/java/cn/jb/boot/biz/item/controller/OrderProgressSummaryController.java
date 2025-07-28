package cn.jb.boot.biz.item.controller;

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
import java.util.List;

/** 查询订单进度汇总的控制器 */
//  有问题  新增的控制器访问不了  ，未找到原因！
@RestController
@RequestMapping("/api/item/orderProgressSummary")
@Tag(name = "orderProgressSummary-controller", description = "订单进度汇总接口")
public class OrderProgressSummaryController {

	@Resource
	private OrderProgressSummaryService service;



	/** 获取 t_order_progress_summary 表前 100 条记录 */
	@PostMapping("/list")
	@Operation(summary = "查询订单进度汇总列表（最多100条）")
	public BaseResponse<List<OrderProgressSummary>> list() {
		List<OrderProgressSummary> data = service.listTop100();
		return MsgUtil.ok(data);
	}

	@PostMapping("/listcs")
	@Operation(summary = "查询订单进度汇总列表（最多100条）")
	public BaseResponse<List<OrderProgressSummary>> listcs() {
		List<OrderProgressSummary> data =null;
		return MsgUtil.ok(data);
	}

}
