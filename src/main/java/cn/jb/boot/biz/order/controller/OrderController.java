package cn.jb.boot.biz.order.controller;

import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.mapper.MesItemStockMapper;
import cn.jb.boot.biz.order.dto.OrderNoPageDto;
import cn.jb.boot.biz.order.dto.OrderNoRequest;
import cn.jb.boot.biz.order.dto.SaleOrderFullInfoResp;
import cn.jb.boot.biz.order.dto.SaleOrderIdReqDto;
import cn.jb.boot.biz.order.service.OrderService;
import cn.jb.boot.biz.sales.entity.SaleOrder;
import cn.jb.boot.biz.sales.entity.SaleOrderPlace;
import cn.jb.boot.biz.sales.mapper.SaleOrderMapper;
import cn.jb.boot.biz.sales.service.SaleOrderPlaceService;
import cn.jb.boot.biz.sales.service.impl.SaleOrderServiceImpl;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.common.utils.StringUtils;
import cn.jb.boot.util.MsgUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * 销售单通用操作控制器（简化接口专用补丁 无冲突）
 */
@RestController
@RequestMapping("/api/salesOrder")
@Tag(name = "OrderController", description = "销售单通用操作")
public class OrderController {


	@Resource
	private SaleOrderMapper saleOrderMapper;

	@Resource
	private MesItemStockMapper mesItemStockMapper;


  /////////////////////////////////////////////////////////////销售表///////////////////////////////////////////////////////////////////////

	/**
	 * 根据主键 ID 查询销售单详情  -- 联表
	 */
	@PostMapping("/infoById")
	@Operation(summary = "根据ID查询销售单详情（含产品名称）")
	public BaseResponse<Map<String, Object>> getById(@RequestBody Map<String, Object> body) {
		String id = (String) body.get("id");

		// 查询销售单
		SaleOrder order = saleOrderMapper.selectById(id);
		if (order == null) {
			return MsgUtil.ok(null); // 或者返回自定义错误
		}

		// 查询产品信息（只查 item_name）
		LambdaQueryWrapper<MesItemStock> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(MesItemStock::getItemNo, order.getItemNo()).select(MesItemStock::getItemName);
		MesItemStock stock = mesItemStockMapper.selectOne(wrapper);

		// 手动封装返回字段
		Map<String, Object> result = new HashMap<>();
		result.put("id", order.getId());
		result.put("orderNo", order.getOrderNo());
		result.put("custName", order.getCustName());
		result.put("itemNo", order.getItemNo());
		result.put("needNum", order.getNeedNum());
		result.put("approvedOrderedNum", order.getApprovedOrderedNum());
		result.put("producedNum", order.getProducedNum());
		result.put("procedureCode", order.getProcedureCode());
		result.put("procedureName", order.getProcedureName());
		result.put("itemName", stock != null ? stock.getItemName() : null); // 🔗 补充字段

		return MsgUtil.ok(result);
	}



	/**
	 * 修改销售单（简单模式，控制器直调 Mapper，Map 参数接收）
	 */
	@PostMapping("/update")
	@Operation(summary = "修改销售单（控制器直调）")
	public BaseResponse<String> updateOrder(@RequestBody Map<String, Object> body) {
		String id = (String) body.get("id");
		String custName = (String) body.get("custName");
		String itemNo = (String) body.get("itemNo");
		BigDecimal needNum = new BigDecimal(body.get("needNum").toString());

		SaleOrder order = new SaleOrder();
		order.setId(id);
		order.setCustName(custName);
		order.setItemNo(itemNo);
		order.setNeedNum(needNum);

		saleOrderMapper.updateById(order);
		return MsgUtil.ok();
	}


	/**
	 * 新增销售单（简单模式，控制器直调 Mapper，Map 参数接收）
	 */
	@PostMapping("/add")
	@Operation(summary = "新增销售单（直接写入）")
	public BaseResponse<String> insertSimple(@RequestBody Map<String, Object> body) {
		String custName = (String) body.get("custName");
		String itemNo = (String) body.get("itemNo");
		BigDecimal needNum = new BigDecimal(body.get("needNum").toString());

		SaleOrder order = new SaleOrder();
		order.setCustName(custName);
		order.setItemNo(itemNo);
		order.setNeedNum(needNum);
		order.setOrderNo(genOrderNo());
		order.setOrderStatus("00");
		order.setApprovedOrderedNum(BigDecimal.ZERO);
		order.setProducedNum(BigDecimal.ZERO);

		saleOrderMapper.insert(order);
		return MsgUtil.ok();
	}

	/**
	 * 自动生成销售单号（格式：yyyyMMdd + 随机4位数）  后期需优化
	 */
	private String genOrderNo() {
		String ymd = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		int rand = new Random().nextInt(9000) + 1000;
		return ymd + rand;
	}

	/////////////////////////////////////////////////////////////销售审批表///////////////////////////////////////////////////////////////////////



	@Resource
	private OrderService orderService;  // 订单操作和审批  合并在这里

	@Resource
	private SaleOrderServiceImpl saleOrderService;

	@Resource
	private SaleOrderPlaceService saleOrderPlaceService;

	/**
	 * 根据订单号查询销售单（不分页）
	 * @return
	 */
	@PostMapping("/getByOrderNo")
	@Operation(summary = "根据订单号查询销售单（不分页）")
	public BaseResponse<List<SaleOrder>> getByOrderNo(@RequestBody OrderNoRequest req) {
		String orderNo = req.getOrderNo();
		if (orderNo == null || orderNo.trim().isEmpty()) {
			return MsgUtil.fail("订单号不能为空");
		}
		List<SaleOrder> list = orderService.listByOrderNo(orderNo.trim());
		return MsgUtil.ok(list);
	}



	/**
	 * 查询订单的审批列表（不分页）
	 * @param  request  格式： {	 "orderNo": "202505164707" }
	 * @return
	 */
	@PostMapping("/getAllByOrderNo")
	@Operation(summary = "根据订单号查询销售单+审批单（组合展开）")
	public BaseResponse<List<SaleOrderFullInfoResp>> listOrderPlaceDetail(@RequestBody OrderNoRequest request) {
		String orderNo = request.getOrderNo();
		if (StringUtils.isBlank(orderNo)) {
			return MsgUtil.fail("订单号不能为空");
		}
		List<SaleOrderFullInfoResp> list = orderService.listCombinedInfo(orderNo.trim());
		return MsgUtil.ok(list);
	}


	/**
	 *查询销售单+审批单 （ 分页原生）
	 * @param dto
	 * @return
	 */
	@PostMapping("/getAllByOrderNoPageList")
	@Operation(summary = "分页查询销售单+审批单")
	public BaseResponse<List<SaleOrderFullInfoResp>> getPlacePage(@RequestBody(required = false) OrderNoPageDto dto) {
		if (dto == null) {
			dto = new OrderNoPageDto();
		}
		if (dto.getPageNum() == null || dto.getPageNum() <= 0) {
			dto.setPageNum(1);
		}
		if (dto.getPageSize() == null || dto.getPageSize() <= 0) {
			dto.setPageSize(10);
		}

		return orderService.getPlaceOrderPage(dto);
	}



	//	提交生产订单的审批
@PostMapping("/submitPlaceOrder")
@Operation(summary = "提交下生产审批单（简化）")
public BaseResponse<String> submitPlaceOrders(@RequestBody List<SaleOrderPlace> placeOrders) {
	if (placeOrders == null || placeOrders.isEmpty()) {
		return MsgUtil.fail("提交数据不能为空");
	}

	try {
		String result = orderService.submitPlaceOrders(placeOrders);
		return MsgUtil.ok(result);
	} catch (Exception e) {
		return MsgUtil.fail("提交失败：" + e.getMessage());
	}
}




	/**
	 * 审批通过
	 *
	 * @param request 查询参数
	 * @return 返回成功
	 */
	@PostMapping("/approval")
	@Operation(summary = "审批通过")
	public BaseResponse<String> approval(@RequestBody @Valid SaleOrderIdReqDto request) {
		orderService.approval(Collections.singletonList(request.getId()));
		return MsgUtil.ok();
	}




}
