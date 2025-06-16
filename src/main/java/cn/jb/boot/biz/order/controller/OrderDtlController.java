package cn.jb.boot.biz.order.controller;

import cn.jb.boot.biz.order.service.OrderDtlService;
import cn.jb.boot.biz.order.service.impl.OrderDtlServiceImpl;
import cn.jb.boot.biz.order.vo.request.OrderAllDtlUpdateStatusRequest;
import cn.jb.boot.biz.order.vo.request.OrderDtlPageRequest;
import cn.jb.boot.biz.order.vo.request.OrderDtlUpdateStatusRequest;
import cn.jb.boot.biz.order.vo.response.OrderDtlPageResponse;
import cn.jb.boot.biz.order.vo.response.WarningResponse;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.util.MsgUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 前端控制器
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-05 19:00:38
 */
@RestController
@RequestMapping("/api/order/order_dtl")
@Tag(name = "order-dtl-controller", description = "订单明细表接口")
public class OrderDtlController {

	@Resource
	private OrderDtlService service;


	@Resource
	private OrderDtlServiceImpl orderDtlService; //直调

	/**
	 * 分页查询信息
	 *
	 * @param request 查询参数
	 * @return 分页记录
	 */
	@PostMapping("/page_list2")
	@Operation(summary = "查询订单明细表分页列表")
	public BaseResponse<List<OrderDtlPageResponse>> pageList(@RequestBody @Valid BaseRequest<OrderDtlPageRequest> request) {
		OrderDtlPageRequest params = MsgUtil.params(request);
		return service.pageInfo(request.getPage(), params);
	}


	// 分页查询orderDtl  + 物料名称   新增
	@PostMapping("/page_list")
	@Operation(summary = "分页查询订单明细 + 物料名称")
	public BaseResponse<List<OrderDtlPageResponse>> getPageWithItemName(@RequestBody OrderDtlPageRequest dto) {


		//正常列表
//        try {
//            return orderDtlService.pageWithItemName(dto);
//        } catch (Exception e) {
//            // 打印栈追踪到控制台
//            e.printStackTrace();
//            // 把错误信息透传给前端
//            return MsgUtil.fail("分页出错：" + e.getMessage());
//        }


		//    使用树代替组  可行
		return orderDtlService.pageTreeWithItemName(dto);

	}

	@PostMapping("/update_status")
	@Operation(summary = "查询订单明细表分页列表")
	public BaseResponse<String> updateStatus(@RequestBody @Valid BaseRequest<OrderDtlUpdateStatusRequest> request) {
		service.updateStatus(MsgUtil.params(request));
		return MsgUtil.ok();
	}

	@PostMapping("/update_all_status")
	@Operation(summary = "查询订单明细表分页列表")
	public BaseResponse<String> updateAllStatus(@RequestBody @Valid BaseRequest<OrderAllDtlUpdateStatusRequest> request) {
		service.updateAllStatus(MsgUtil.params(request));
		return MsgUtil.ok();
	}

	/**
	 * 获取当日实时数据
	 *
	 * @param params 查询参数
	 * @return 工位库位
	 */
	@PostMapping("/getProcTodayDatas")
	@Operation(summary = "获取实时数据")
	public String getProcTodayDatas(@RequestBody Map<String, Object> params) {
		String todayDatas = service.getProcTodayDatas(params);
		return todayDatas;
	}

	/**
	 * 获取页面提示数据
	 *
	 * @param
	 * @return
	 */
	@PostMapping("/getWarnning")
	@Operation(summary = "查询页面提示数据")
	public BaseResponse<List<WarningResponse>> getWarnning() {
		return service.getWarning();
	}
}
