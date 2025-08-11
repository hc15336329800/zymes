package cn.jb.boot.biz.order.service;

import cn.hutool.core.date.DateTime;
import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.mapper.MesItemStockMapper;
import cn.jb.boot.biz.order.dto.OrderNoPageDto;
import cn.jb.boot.biz.order.dto.SaleOrderFullInfoResp;
import cn.jb.boot.biz.production.entity.ProductionOrder;
import cn.jb.boot.biz.production.service.ProductionOrderService;
import cn.jb.boot.biz.sales.entity.SaleOrder;
import cn.jb.boot.biz.sales.entity.SaleOrderPlace;
import cn.jb.boot.biz.sales.enums.ApprovalStatus;
import cn.jb.boot.biz.sales.mapper.SaleOrderMapper;
import cn.jb.boot.biz.sales.mapper.SaleOrderPlaceMapper;
import cn.jb.boot.biz.sales.service.SaleOrderPlaceService;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.com.response.PagingResponse;
import cn.jb.boot.framework.common.utils.StringUtils;
import cn.jb.boot.framework.exception.CavException;
import cn.jb.boot.util.MsgUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderService {

	@Resource
	private SaleOrderMapper saleOrderMapper;

	@Resource
	private SaleOrderPlaceMapper saleOrderPlaceMapper;

	@Resource
	private MesItemStockMapper mesItemStockMapper;

	@Resource
	private SaleOrderPlaceService saleOrderPlaceService;


	@Resource
	private ProductionOrderService productionOrderService;    //保存生产单

	/**
	 * 审批通过
	 * @return 返回成功
	 */
	@Transactional(rollbackFor = Throwable.class)
	public void approval(List<String> placeIds) {
		if (placeIds == null || placeIds.isEmpty()) {
			throw new CavException("请选择要审批的数据！");
		}

		// 查询 SaleOrderPlace 列表
		List<SaleOrderPlace> places = saleOrderPlaceMapper.selectBatchIds(placeIds);
		if (places.size() != placeIds.size()) {
			throw new CavException("部分审批单不存在！");
		}

		// 校验状态为“待审批”
		for (SaleOrderPlace place : places) {
			if (!"00".equals(place.getPlaceStatus())) {
				throw new CavException("审批单【" + place.getOrderNo() + "】状态不是待审批！");
			}
		}

		// 提取销售单ID并查询
		List<String> saleIds = places.stream()
				.map(SaleOrderPlace::getSaleId)
				.distinct()
				.collect(Collectors.toList());

		List<SaleOrder> orders = saleOrderMapper.selectBatchIds(saleIds);

		// 构建 saleOrder 映射
		Map<String, SaleOrder> saleOrderMap = orders.stream()
				.collect(Collectors.toMap(SaleOrder::getId, Function.identity()));

		// 生成更新列表和生产单列表
		List<SaleOrderPlace> updateList = new ArrayList<>();
		List<ProductionOrder> productionOrders = new ArrayList<>();

		for (SaleOrderPlace place : places) {
			// 修改状态
			SaleOrderPlace update = new SaleOrderPlace();
			update.setId(place.getId());
			update.setPlaceStatus("01"); // 审批通过
			updateList.add(update);

			SaleOrder order = saleOrderMap.get(place.getSaleId());
			if (order == null) continue;

			ProductionOrder po = new ProductionOrder();
			po.setSalesOrderNo(order.getOrderNo());// 订单号，必须从 SaleOrder 查
			po.setItemNo(order.getItemNo());
			po.setDeliverTime(place.getDeliverTime());
			po.setBizType(place.getBizType()); //优先级
			po.setItemCount(place.getOrderedNum());
			po.setStatus("01"); // 就绪状态
			po.setPlaceId(place.getId());
			po.setProcedureCode(order.getProcedureCode());// 工序编码
			po.setProcedureName(order.getProcedureName()); // 工序名称
			po.setOrderType("01"); //销售单
			productionOrders.add(po);
		}

//		// 批量更新审批状态
//		saleOrderPlaceService.updateBatchById(updateList);
//		// 批量生成生产单
//		productionOrderService.saveBatch(productionOrders);

		try {
			// 批量更新审批状态
			boolean updated = saleOrderPlaceService.updateBatchById(updateList);
			if (!updated) {
				throw new CavException("更新审批状态失败，请检查记录是否存在！");
			}
		} catch (Exception e) {
			throw new CavException("审批状态更新异常：" + e.getMessage(), e);
		}

		try {
			// 批量生成生产单
			boolean saved = productionOrderService.saveBatch(productionOrders);
			if (!saved) {
				throw new CavException("生成生产单失败！");
			}
		} catch (Exception e) {
			throw new CavException("生产单保存异常：" + e.getMessage(), e);
		}


	}



	//校验状态，批量下触发的验证
	private Map<String, SaleOrderPlace> checkedStatus(List<String> ids) {
		List<SaleOrderPlace> dbs = saleOrderPlaceMapper.selectList(
				new LambdaQueryWrapper<SaleOrderPlace>()
						.eq(SaleOrderPlace::getPlaceStatus, "00") // 审批状态“待审批”
						.in(SaleOrderPlace::getId, ids)
		);

		if (dbs.size() != ids.size()) {
			throw new CavException("部分订单状态不为待审批！");
		}

		return dbs.stream().collect(Collectors.toMap(SaleOrderPlace::getId, Function.identity()));
	}


	/**
	 * 查询订单的审批列表（分页原生写法）  新
	 * @return
	 */
	public BaseResponse<List<SaleOrderFullInfoResp>> getPlaceOrderPage(OrderNoPageDto dto) {
		// 1. 构建分页对象
		Page<SaleOrderFullInfoResp> page = new Page<>(dto.getPageNum(), dto.getPageSize());

		// 2. 查询数据
		IPage<SaleOrderFullInfoResp> pageData = saleOrderPlaceMapper.selectPlacePage(page, dto);

		// 3. 构造分页信息
		PagingResponse paging = new PagingResponse();
		paging.setPageNum((int) pageData.getCurrent());
		paging.setPageSize((int) pageData.getSize());
		paging.setTotalNum((int) pageData.getTotal());
		paging.setPages((int) pageData.getPages());

		// 4. 构造响应对象
		BaseResponse<List<SaleOrderFullInfoResp>> response = new BaseResponse<>();
		response.setTxStatus("00");
		response.setData(pageData.getRecords());
		response.setPage(paging);

		return response;
	}


	/**
	 * 提交订单的审批   333
	 * @param
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public String submitPlaceOrders(List<SaleOrderPlace> placeOrders) {
		if (placeOrders == null || placeOrders.isEmpty()) {
			throw new CavException("提交数据不能为空");
		}

		// 1. 提取所有 orderNo
		List<String> orderNos = placeOrders.stream()
				.map(SaleOrderPlace::getOrderNo)
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());

		if (orderNos.isEmpty()) {
			throw new CavException("订单号不能为空");
		}

		// 2. 查询订单信息
		List<SaleOrder> orders = saleOrderMapper.selectList(
				new LambdaQueryWrapper<SaleOrder>().in(SaleOrder::getOrderNo, orderNos)
		);
		if (orders == null || orders.isEmpty()) {
			throw new CavException("未找到对应销售单");
		}

		// 建立 orderNo → SaleOrder 映射
		Map<String, SaleOrder> orderMap = orders.stream()
				.collect(Collectors.toMap(SaleOrder::getOrderNo, Function.identity()));

		// 3. 组装下发记录
		for (SaleOrderPlace place : placeOrders) {
			String orderNo = place.getOrderNo();
			SaleOrder order = orderMap.get(orderNo);
			if (order == null) {
				throw new CavException("订单号不存在：" + orderNo);
			}

			place.setSaleId(order.getId());
			place.setItemNo(order.getItemNo());
//			place.setApplyTime(LocalDate.now().atStartOfDay());
			place.setApplyTime(LocalDateTime.now()); //当前日期+时间

			place.setApplyNo("system");         // TODO: 替换为真实用户信息
			place.setApplyName("系统默认");
			place.setPlaceStatus("00");
			if (place.getOrderedNum() == null) {
				place.setOrderedNum(BigDecimal.ZERO);
			}
		}

		// 4. 批量保存审批记录
		saleOrderPlaceService.saveBatch(placeOrders);

		// ✅ 5. 更新 sale_order 表中的 order_status = "01"
		List<String> saleIds = placeOrders.stream()
				.map(SaleOrderPlace::getSaleId)
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());

		if (!saleIds.isEmpty()) {
			SaleOrder update = new SaleOrder();
			update.setOrderStatus("01"); // 设置为已下单
			saleOrderMapper.update(update,
					new LambdaQueryWrapper<SaleOrder>().in(SaleOrder::getId, saleIds));
		}

		return "提交成功：" + placeOrders.size() + " 条";
	}


	/**
	 * 根据订单号查询销售单（不分页）
	 * @param orderNo
	 * @return
	 */
	public List<SaleOrder> listByOrderNo(String orderNo) {
		LambdaQueryWrapper<SaleOrder> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(SaleOrder::getOrderNo, orderNo);
		return saleOrderMapper.selectList(wrapper);
	}



	/**
	 * 根据订单号查询销售单 + 下发审批单 + 物料名称（展开组合）
	 */
	public List<SaleOrderFullInfoResp> listCombinedInfo(String orderNo) {
		if (StringUtils.isBlank(orderNo)) {
			throw new CavException("订单号不能为空");
		}

		// 1. 查询销售单（只一条）
		SaleOrder order = saleOrderMapper.selectOne(
				new LambdaQueryWrapper<SaleOrder>()
						.eq(SaleOrder::getOrderNo, orderNo)
						.last("limit 1")
		);
		if (order == null) {
			throw new CavException("未找到销售单：" + orderNo);
		}

		// 2. 查询物料名称
		String itemName = null;
		String bomNo = null;
		if (StringUtils.isNotBlank(order.getItemNo())) {
			MesItemStock stock = mesItemStockMapper.selectOne(
					new LambdaQueryWrapper<MesItemStock>()
							.eq(MesItemStock::getItemNo, order.getItemNo())
			);
			if (stock != null) {
				itemName = stock.getItemName();
				bomNo = stock.getBomNo();
			}
		}

		// 3. 查询该销售单对应的所有 place 审批记录
		List<SaleOrderPlace> places = saleOrderPlaceMapper.selectList(
				new LambdaQueryWrapper<SaleOrderPlace>()
						.eq(SaleOrderPlace::getOrderNo, orderNo)
		);

		// 4. 拼接结果，每条 place 记录 + 销售单信息 → SaleOrderFullInfoResp
		List<SaleOrderFullInfoResp> result = new ArrayList<>();
		for (SaleOrderPlace place : places) {
			SaleOrderFullInfoResp dto = new SaleOrderFullInfoResp();

			// 销售单字段
			dto.setOrderNo(order.getOrderNo());
			dto.setCustName(order.getCustName());
			dto.setItemNo(order.getItemNo());
			dto.setItemName(itemName);
			dto.setBomNo(bomNo);

			dto.setNeedNum(order.getNeedNum());
			dto.setOrderStatus(order.getOrderStatus());

			// 审批记录字段
			dto.setApplyNo(place.getApplyNo());
			dto.setApplyName(place.getApplyName());
			dto.setApplyTime(place.getApplyTime());
			dto.setPlaceStatus(place.getPlaceStatus());
			dto.setApprovalMsg(place.getApprovalMsg());

			dto.setDeliverTime(place.getDeliverTime());
			dto.setBizType(place.getBizType());



			result.add(dto);
		}

		return result;
	}





}
