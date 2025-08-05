package cn.jb.boot.biz.production.service.impl;

import cn.jb.boot.biz.depository.manager.OutStoreManager;
import cn.jb.boot.biz.item.entity.BomUsed;
import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.entity.MesProcedure;
import cn.jb.boot.biz.item.enums.ItemType;
import cn.jb.boot.biz.item.service.BomUsedService;
import cn.jb.boot.biz.item.service.MesItemStockService;
import cn.jb.boot.biz.item.service.MesProcedureService;
import cn.jb.boot.biz.order.entity.OrderDtl;
import cn.jb.boot.biz.order.entity.ProcAllocation;
import cn.jb.boot.biz.order.enums.DtlStatusEnum;
import cn.jb.boot.biz.order.enums.ProcStatus;
import cn.jb.boot.biz.order.service.OrderDtlService;
import cn.jb.boot.biz.order.service.ProcAllocationService;
import cn.jb.boot.biz.order.util.SeqUtil;
import cn.jb.boot.biz.production.dto.BomTreeNodeVo;
import cn.jb.boot.biz.production.dto.BomTreePageDto;
import cn.jb.boot.biz.production.emuns.ProductionStatus;
import cn.jb.boot.biz.production.entity.ProductionOrder;
import cn.jb.boot.biz.production.mapper.ProductionOrderMapper;
import cn.jb.boot.biz.production.model.OrderSchedule;
import cn.jb.boot.biz.production.service.ProductionOrderService;
import cn.jb.boot.biz.production.vo.req.ProductionOrderPageRequest;
import cn.jb.boot.biz.production.vo.resp.ProductionOrderPageResponse;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.com.response.PagingResponse;
import cn.jb.boot.framework.config.SpringContextUtil;
import cn.jb.boot.util.ArithUtil;
import cn.jb.boot.util.JsonUtil;
import cn.jb.boot.util.MsgUtil;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import cn.jb.boot.util.SnowFlake;
import cn.jb.boot.util.ThreadPool;
import cn.jb.boot.util.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.util.Map;
import java.util.stream.Collectors;



/**
 * 生产任务单 服务实现类
 *
 * @author user
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-01-15 21:08:58
 */
@Service
@Slf4j
public class ProductionOrderServiceImpl extends ServiceImpl<ProductionOrderMapper, ProductionOrder> implements ProductionOrderService {


    @Resource
    private ProductionOrderMapper mapper;
    @Resource
    private BomUsedService bomUsedService;
    @Resource
    private OrderDtlService orderDtlService;
    @Resource
    private ProcAllocationService procAllocationService;
    @Resource
    private MesProcedureService procedureService;
    @Resource
    private SeqUtil seqUtil;
    @Resource
    private OutStoreManager outStoreManager;


    @Resource
    private MesItemStockService mesItemStockService;


    private static final int MAX_DEPTH = 10;  //递归深度限制


//    删除
@Override
@Transactional(rollbackFor = Throwable.class)
public void delete(List<String> ids) {
    // 校验：不允许删除已排产工单（02 = 已排产）
    long count = this.count(
            new LambdaQueryWrapper<ProductionOrder>()
                    .in(ProductionOrder::getId, ids)   // 工单 ID 在 ids 列表中
                    .eq(ProductionOrder::getStatus, "02") // 工单状态等于 02（已排产）
    );
//    count > 0，说明这些待删工单中有已排产的，不允许删除。
//    count == 0，说明这些工单都是未排产的，可以删。
    if (count > 0) {
        throw new RuntimeException("存在已排产工单，不能删除");
    }

    // 真正物理删除
    this.removeByIds(ids);
}




/////////////////////////////////// 新的bom树   ↓///////////////////////////////////////////


    //table  bom树  (2516004414-1 测试成功)
//     潜在问题： 无法正确显示每个节点的bomNo ! ( 不好解决 需要递归)
     public BaseResponse<List<BomTreeNodeVo>> getBomTreePage(BomTreePageDto dto) {
        // 固定 bomNo（测试用）
//        String fixedBomNo = "2516004414-1";
         String fixedBomNo = dto.getBomNo();
        int pageNum = (dto.getPageNum() == null || dto.getPageNum() <= 0) ? 1 : dto.getPageNum();
        int pageSize = (dto.getPageSize() == null || dto.getPageSize() <= 0) ? 10 : dto.getPageSize();

//         查询 BOM 用料列表    V3.0.0
        List<BomUsed> list = bomUsedService.list(
                new LambdaQueryWrapper<BomUsed>().eq(BomUsed::getBomNo, fixedBomNo)
        );

        BaseResponse<List<BomTreeNodeVo>> response = new BaseResponse<>();
        if (CollectionUtils.isEmpty(list)) {
            PagingResponse emptyPage = new PagingResponse();
            emptyPage.setPageNum(pageNum);
            emptyPage.setPageSize(pageSize);
            emptyPage.setTotalNum(0L);
            emptyPage.setPages(0L);
            response.setTxStatus("00");
            response.setData(Collections.emptyList());
            response.setPage(emptyPage);
            return response;
        }

        // 查询物料名称映射
        Set<String> useItemNos = list.stream()
                .map(BomUsed::getUseItemNo)
                .collect(Collectors.toSet());

        List<MesItemStock> stockList = mesItemStockService.list(
                new LambdaQueryWrapper<MesItemStock>().in(MesItemStock::getItemNo, useItemNos)
        );

        Map<String, String> itemNameMap = stockList.stream()
                .collect(Collectors.toMap(MesItemStock::getItemNo, MesItemStock::getItemName, (a, b) -> a));

        // 构建中间节点映射
        Map<String, BomTreeNodeVo> map = new HashMap<>();
        for (BomUsed bom : list) {
            BomTreeNodeVo node = new BomTreeNodeVo();
            node.setId(bom.getId());
            node.setItemNo(bom.getItemNo());
            node.setUseItemNo(bom.getUseItemNo());
            node.setParentCode(bom.getParentCode());
            node.setUseItemCount(bom.getUseItemCount());
            node.setBomNo(bom.getBomNo());
            node.setItemName(itemNameMap.getOrDefault(bom.getUseItemNo(), ""));
            node.setChildren(new ArrayList<>());
            map.put(bom.getUseItemNo(), node);
        }

        // 构建根节点
        List<BomTreeNodeVo> roots = new ArrayList<>();
        Set<String> allUseItemNos = map.keySet();

        for (BomUsed bom : list) {
            BomTreeNodeVo current = map.get(bom.getUseItemNo());
            String parentCode = bom.getParentCode();

            if (StringUtils.isEmpty(parentCode)
                    || parentCode.equals(bom.getUseItemNo())
                    || !allUseItemNos.contains(parentCode)) {
                roots.add(current);
            } else {
                BomTreeNodeVo parent = map.get(parentCode);
                if (parent != null) {
                    parent.getChildren().add(current);
                }
            }
        }

        // 打印调试信息
        System.out.println("【调试】BOM号：" + fixedBomNo);
        System.out.println("【调试】总记录数：" + list.size());
        System.out.println("【调试】构建出的根节点数：" + roots.size());
        for (BomTreeNodeVo root : roots) {
            System.out.println("  → 根节点 itemNo: " + root.getItemNo() + "，子节点数: " + root.getChildren().size());
        }

        // 分页处理根节点
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, roots.size());
        List<BomTreeNodeVo> pageList = fromIndex < roots.size() ? roots.subList(fromIndex, toIndex) : Collections.emptyList();

        PagingResponse paging = new PagingResponse();
        paging.setPageNum((long) pageNum);
        paging.setPageSize((long) pageSize);
        paging.setTotalNum((long) roots.size());
        paging.setPages((long) Math.ceil((double) roots.size() / pageSize));

        response.setTxStatus("00");
        response.setData(pageList);
        response.setPage(paging);
        return response;
    }



///////////////////////////////////下排产新的测试   ↓///////////////////////////////////////////



    /**
     * 升级排产方法 --  v2.0（  不在递归拆解！  直接读取 物料类型,=01 即可 ）
     *  核心1： 直接读取 物料类型,=01 即可，不拆解到物料，因为物料无需工序
     *  核心2： 无需再联查mes_item_stock，因为读取的时候在进行联查！
     *  主要任务：  解析数据后写入 t_order_dtl 表中
     * 涉及表：
     *  - t_production_order（主表，状态更新）- 插入/更新
     *  - mes_item_stock   （联查 mes_item_stock 取出 item_name） - 查询
     *  - t_bom_used（联查物料BOM结构  ） - 查询
     *  - t_order_dtl（构建子项销售明细, BOM 拆解生成订单明细 ）- 插入/更新
     *  - t_proc_allocation（工序路径分配 ）- 插入/更新
     *  -  还有一个出库单   暂时不实现
     *
     *  - rootItemNo ： 根物料编码   用于排除自引用
     *  - rootCount：计划生产数量
     */
    @Transactional(rollbackFor = Exception.class)
    public void startSchedule20(String bomNo, String salesOrderNo, String rootItemNo, BigDecimal rootCount) {
        String uid = UserUtil.uid();
        String orderNo = (salesOrderNo != null && !"".equals(salesOrderNo)) ? salesOrderNo : seqUtil.getOrderNo();

        // —— 修改：只根据 salesOrderNo 查询 ProductionOrder ——
        ProductionOrder po = mapper.selectOne(new LambdaQueryWrapper<ProductionOrder>()
                .eq(ProductionOrder::getSalesOrderNo, orderNo)
        );
        if (po == null) {
            throw new RuntimeException("未找到生产订单，订单号：" + orderNo);
        }
        if (!ProductionStatus.TO_READY.getCode().equals(po.getStatus())) {
            throw new RuntimeException("生产订单状态不是“就绪”(01)，当前状态：" + po.getStatus());
        }


        log.info("【startSchedule20】根据 bomNo={} 进行拆解，根物料={}, 数量={}", bomNo, rootItemNo, rootCount);

        // 1. 查询 bom_used 表：指定 bom_no，use_item_type = '01'，排除顶层自身
        List<BomUsed> usedList = bomUsedService.list(new LambdaQueryWrapper<BomUsed>()
                .eq(BomUsed::getBomNo, bomNo)
                .eq(BomUsed::getUseItemType, "01"));
//                .ne(BomUsed::getUseItemNo, rootItemNo)); // 避免自引用    注意~

        if (CollectionUtils.isEmpty(usedList)) {
            log.warn("【startSchedule20】未找到有效子项结构，bomNo={} itemNo={}", bomNo, rootItemNo);
             throw new RuntimeException("未找到有效子项结构，bomNo=" + bomNo + " itemNo=" + rootItemNo);

        }

        // 2. 构建 order_dtl 明细
        List<OrderDtl> dtlList = new ArrayList<>();

        for (BomUsed bom : usedList) {
            String useItemNo = bom.getUseItemNo();

            // 数量计算：每条子件记录的数量为 bom_used.use_item_count * rootCount，即 BOM 定义的单件用量 × 根订单计划数量。
            BigDecimal itemCount = ArithUtil.mul(bom.getUseItemCount(), rootCount);

            OrderDtl dtl = new OrderDtl();
            dtl.setId(SnowFlake.genId());
            dtl.setOrderNo(orderNo);
            dtl.setItemNo(useItemNo);
            dtl.setItemCount(itemCount);
            dtl.setNeedMidCount(itemCount);
            dtl.setProductionCount(BigDecimal.ZERO);
            dtl.setOrderDtlStatus(DtlStatusEnum.AWAITING_PRODUCTION.getCode());  //03 、待生产
            dtl.setCreatedBy(uid);
            dtl.setUpdatedBy(uid);
            dtl.setCreatedTime(LocalDateTime.now());
            dtl.setUpdatedTime(LocalDateTime.now());

            dtlList.add(dtl);

            log.info("【拆解记录】useItemNo={}, count={}", useItemNo, itemCount);
        }

        // 3. 插入明细
        orderDtlService.saveBatch(dtlList);
        log.info("【startSchedule20】成功插入明细 {} 条", dtlList.size());

        // ✅ 新增：拆工序并插入 t_proc_allocation
        buildAndInsertAllocations(uid, dtlList);

        // —— 修改：拆单完成后更新状态为已排产(02) ——
        po.setStatus(ProductionStatus.SCHEDULED.getCode());
        po.setUpdatedBy(uid);
        po.setUpdatedTime(LocalDateTime.now());
        mapper.updateById(po);
        log.info("【startSchedule20】订单 {} 状态更新为 已排产(02)", orderNo);
    }


    /**
     * 新增方法：构建并插入工序分配记录
     * @param uid
     * @param dtlList
     */
    private void buildAndInsertAllocations(String uid, List<OrderDtl> dtlList) {
        List<String> itemNos = dtlList.stream().map(OrderDtl::getItemNo).collect(Collectors.toList());

        // 1. 查询 mes_procedure 表的工序路径
        List<MesProcedure> procedureList = procedureService.list(
                new LambdaQueryWrapper<MesProcedure>().in(MesProcedure::getItemNo, itemNos)
        );

        if (CollectionUtils.isEmpty(procedureList)) {
            log.warn("【工序拆解】未查到对应工序路径，itemNos={}", itemNos);
            return;
        }

        // 2. 分组：Map<itemNo, List<MesProcedure>>
        Map<String, List<MesProcedure>> procMap = procedureList.stream()
                .collect(Collectors.groupingBy(MesProcedure::getItemNo));

        // 3. 构建工序分配记录
        List<ProcAllocation> allocationList = new ArrayList<>();

        for (OrderDtl dtl : dtlList) {
            List<MesProcedure> procs = procMap.get(dtl.getItemNo());
            if (CollectionUtils.isEmpty(procs)) {
                log.info("【工序拆解】物料 {} 未配置工序，跳过", dtl.getItemNo());
                continue;
            }

            for (MesProcedure proc : procs) {
                ProcAllocation alloc = new ProcAllocation();
                alloc.setId(SnowFlake.genId());
                alloc.setOrderDtlId(dtl.getId());
                alloc.setOrderNo(dtl.getOrderNo());
                alloc.setItemNo(dtl.getItemNo());
                alloc.setProcedureCode(proc.getProcedureCode());
                alloc.setProcedureName(proc.getProcedureName());
                alloc.setTotalCount(dtl.getItemCount());
                alloc.setOrgiTotalCount(dtl.getItemCount());
                alloc.setProcStatus("01"); // 初始状态：运行中
                alloc.setDeviceId(proc.getDeviceId());
                alloc.setDeptId(proc.getDeptId());
                alloc.setHoursFixed(proc.getHoursWork());
                alloc.setSeqNo(proc.getSeqNo());
                alloc.setCreatedBy(uid);
                alloc.setUpdatedBy(uid);
                alloc.setCreatedTime(LocalDateTime.now());
                alloc.setUpdatedTime(LocalDateTime.now());
                allocationList.add(alloc);

                log.info("【工序分配】itemNo={} 工序={} 数量={}", dtl.getItemNo(), proc.getProcedureName(), dtl.getItemCount());
            }
        }

        if (CollectionUtils.isNotEmpty(allocationList)) {
            procAllocationService.saveBatch(allocationList);
            log.info("【工序分配】已插入工序记录 {} 条", allocationList.size());
        }
    }





    ///////////////////////////////////下排产旧的测试  ↓///////////////////////////////////////////






    /**
     * 简化版下排产方法 --  v1.6（ ）
     *  核心1：递归 +递归深度限制
     * 核心2：“只要某个物料有子项，就记录为一条订单明细，不等到最底层”
     * 涉及表：
     *  - t_production_order（主表，状态更新）
     *  - t_bom_used（物料BOM结构  ）
     *  - t_order_dtl（构建子项销售明细 ）
     */
    @Transactional(rollbackFor = Exception.class)
    public void startSchedule06(String productionOrderId) {
        String uid = UserUtil.uid();

        // 1. 查询生产订单
        ProductionOrder po = this.getById(productionOrderId);
        if (po == null) {
            throw new RuntimeException("未找到生产订单，ID: " + productionOrderId);
        }

        String parentItemNo = po.getItemNo();
        BigDecimal parentCount = po.getItemCount();
        String salesOrderNo = po.getSalesOrderNo();

        log.info("【startSchedule06】开始递归拆解（记录所有中间节点）itemNo = {}, 拆解数量 = {}", parentItemNo, parentCount);

        List<OrderDtl> dtlList = new ArrayList<>();
        String orderNo = (salesOrderNo != null && !"".equals(salesOrderNo)) ? salesOrderNo : seqUtil.getOrderNo();

        // 2. 开始递归，只记录有子项的节点
        splitRecursiveMiddleOnly06(parentItemNo, parentCount, orderNo, uid, dtlList);

        // 3. 批量插入
        orderDtlService.saveBatch(dtlList);

        log.info("【startSchedule06】拆解完成，生成中间节点明细 {} 条", dtlList.size());
    }


    private void splitRecursiveMiddleOnly06(String itemNo, BigDecimal count, String orderNo, String uid, List<OrderDtl> dtlList) {
        // 当前物料的子项（只查 BOM 类型）
        List<BomUsed> children = bomUsedService.list(new LambdaQueryWrapper<BomUsed>()
                .eq(BomUsed::getItemNo, itemNo)
                .eq(BomUsed::getUseItemType, "01")
                .ne(BomUsed::getUseItemNo, itemNo));

        // ✅ 如果有子项 → 当前节点应记录为明细
        if (!CollectionUtils.isEmpty(children)) {
            OrderDtl dtl = new OrderDtl();
            dtl.setId(SnowFlake.genId());
            dtl.setOrderNo(orderNo);
            dtl.setItemNo(itemNo);
            dtl.setItemCount(count);
            dtl.setProductionCount(BigDecimal.ZERO);
            dtl.setOrderDtlStatus(DtlStatusEnum.IN_PRODUCED.getCode());
            dtl.setNeedMidCount(count);
            dtl.setCreatedBy(uid);
            dtl.setUpdatedBy(uid);
            dtl.setCreatedTime(LocalDateTime.now());
            dtl.setUpdatedTime(LocalDateTime.now());
            dtlList.add(dtl);

            log.info("【中间节点】itemNo={}, count={}", itemNo, count);
        } else {
            // ❌ 没有子项，不记录
            log.info("【跳过最底层】itemNo={}, count={}", itemNo, count);
            return;
        }

        // ✅ 不管记录与否，都继续向下递归
        for (BomUsed child : children) {
            BigDecimal nextQty = ArithUtil.mul(child.getUseItemCount(), count);
            splitRecursiveMiddleOnly06(child.getUseItemNo(), nextQty, orderNo, uid, dtlList);
        }
    }





    /**
     * 简化版下排产方法 --  v1.5（初步验证失败）
     * 版本升级点：递归 + 去重 + 合并数量 +递归深度限制
     * 涉及表：
     *  - t_production_order（主表，状态更新）
     *  - t_bom_used（物料BOM结构  ）
     *  - t_order_dtl（构建子项销售明细 ）
     */
    @Transactional(rollbackFor = Exception.class)
    public void startSchedule05(String productionOrderId) {
        String uid = UserUtil.uid();

        if (StringUtils.isBlank(productionOrderId)) {
            throw new IllegalArgumentException("生产订单 ID 不可为空");
        }

        // 1. 查询生产订单
        ProductionOrder po = this.getById(productionOrderId);
        if (po == null) {
            throw new RuntimeException("未找到生产订单，ID: " + productionOrderId);
        }

        String parentItemNo = po.getItemNo();
        BigDecimal parentCount = po.getItemCount();
        String salesOrderNo = po.getSalesOrderNo();

        log.info("【startSchedule05】开始递归拆解物料 itemNo = {}, 拆解数量 = {}", parentItemNo, parentCount);

        // 使用 Map 去重并累加数量
        Map<String, OrderDtl> dtlMap = new LinkedHashMap<>();
        String orderNo = (salesOrderNo != null && !"".equals(salesOrderNo)) ? salesOrderNo : seqUtil.getOrderNo();

        // 2. 递归拆解到底层
        splitRecursive5(parentItemNo, parentCount, orderNo, uid, dtlMap, 0);  // 初始深度为 0


        // 3. 批量插入
        orderDtlService.saveBatch(new ArrayList<>(dtlMap.values()));

        log.info("【startSchedule05】拆解完成，共生成明细 {} 条", dtlMap.size());
    }


    private void splitRecursive5(String itemNo, BigDecimal count, String orderNo, String uid, Map<String, OrderDtl> dtlMap, int depth) {

        if (depth > MAX_DEPTH) {
            throw new RuntimeException("BOM 拆解层级过深，可能存在循环引用：itemNo = " + itemNo);
        }

        List<BomUsed> children = bomUsedService.list(new LambdaQueryWrapper<BomUsed>()
                .eq(BomUsed::getItemNo, itemNo)
                .eq(BomUsed::getUseItemType, "01")
                .ne(BomUsed::getUseItemNo, itemNo)); // 排除自引用

        if (CollectionUtils.isEmpty(children)) {
            // 没有子项 → 最小物料
            if (!dtlMap.containsKey(itemNo)) {
                OrderDtl dtl = new OrderDtl();
                dtl.setId(SnowFlake.genId());
                dtl.setOrderNo(orderNo);
                dtl.setItemNo(itemNo);
                dtl.setItemCount(count);
                dtl.setProductionCount(BigDecimal.ZERO);
                dtl.setOrderDtlStatus(DtlStatusEnum.IN_PRODUCED.getCode());
                dtl.setNeedMidCount(count);
                dtl.setCreatedBy(uid);
                dtl.setUpdatedBy(uid);
                dtl.setCreatedTime(LocalDateTime.now());
                dtl.setUpdatedTime(LocalDateTime.now());
                dtlMap.put(itemNo, dtl);
            } else {
                OrderDtl existing = dtlMap.get(itemNo);
                existing.setItemCount(existing.getItemCount().add(count));
                existing.setNeedMidCount(existing.getNeedMidCount().add(count));
            }

            log.info("【最小物料】itemNo={}, 累加数量={}", itemNo, count);
            return;
        }

        // 有子项 → 继续拆
        for (BomUsed child : children) {
            BigDecimal nextQty = ArithUtil.mul(child.getUseItemCount(), count);
            log.info("【拆解】itemNo={} → 子项={} × {}，本次需求={}", itemNo, child.getUseItemNo(), child.getUseItemCount(), nextQty);
            splitRecursive5(child.getUseItemNo(), nextQty, orderNo, uid, dtlMap, depth + 1); // ✅ 深度+1

        }
    }





    /**
     * 简化版下排产方法 --  升级为支持递归 BOM 拆解
     * 目标：，使其不仅拆出当前层级的子项，还能一直拆到最底层“最小单位物料”（即 use_item_type = '00' 的原材料或终端件）。
     * 涉及表：
     *  - t_production_order（主表，状态更新）
     *  - t_bom_used（物料BOM结构  ）
     *  - t_order_dtl（构建子项销售明细 ）
     */

    @Transactional(rollbackFor = Exception.class)
    public void startSchedule03(String productionOrderId) {
        String uid = UserUtil.uid();

        // 1. 查询生产订单
        ProductionOrder po = this.getById(productionOrderId);
        if (po == null) {
            throw new RuntimeException("未找到生产订单，ID: " + productionOrderId);
        }

        String parentItemNo = po.getItemNo();
        BigDecimal parentCount = po.getItemCount();
        String salesOrderNo = po.getSalesOrderNo();

        log.info("【startSchedule03】开始递归拆解物料 itemNo = {}, 拆解数量 = {}", parentItemNo, parentCount);

        List<OrderDtl> dtlList = new ArrayList<>();

        String orderNo = (salesOrderNo != null && !"".equals(salesOrderNo)) ? salesOrderNo : seqUtil.getOrderNo();

        // 2. 递归拆解
        splitRecursive(parentItemNo, parentCount, orderNo, uid, dtlList);

        // 3. 批量插入明细
        orderDtlService.saveBatch(dtlList);

        log.info("【startSchedule03】拆解完成，共生成明细 {} 条", dtlList.size());
    }

    //新增私有递归方法   一直拆到最底层“最小单位物料”
    private void splitRecursive(String itemNo, BigDecimal count, String orderNo, String uid, List<OrderDtl> dtlList) {
        List<BomUsed> children = bomUsedService.list(new LambdaQueryWrapper<BomUsed>()
                .eq(BomUsed::getItemNo, itemNo)
                .eq(BomUsed::getUseItemType, "01")
                .ne(BomUsed::getUseItemNo, itemNo)); // 排除自引用

        if (CollectionUtils.isEmpty(children)) {
            // 没有子项了，视为最小单位，生成 OrderDtl
            OrderDtl dtl = new OrderDtl();
            dtl.setId(SnowFlake.genId());
            dtl.setOrderNo(orderNo);
            dtl.setItemNo(itemNo);
            dtl.setItemCount(count);
            dtl.setProductionCount(BigDecimal.ZERO);
            dtl.setOrderDtlStatus(DtlStatusEnum.IN_PRODUCED.getCode());
            dtl.setNeedMidCount(count);
            dtl.setCreatedBy(uid);
            dtl.setUpdatedBy(uid);
            dtl.setCreatedTime(LocalDateTime.now());
            dtl.setUpdatedTime(LocalDateTime.now());

            dtlList.add(dtl);
            log.info("【最小物料】itemNo={}, count={}", itemNo, count);
            return;
        }

        // 有子项，继续递归
        for (BomUsed child : children) {
            BigDecimal nextQty = ArithUtil.mul(child.getUseItemCount(), count);
            log.info("【拆解】itemNo={} → 子项={} × {}", itemNo, child.getUseItemNo(), child.getUseItemCount());
            splitRecursive(child.getUseItemNo(), nextQty, orderNo, uid, dtlList);
        }
    }




    /**
     * 简化版下排产方法（集中逻辑，MP操作） --  带工序
     * 目标：将生产订单转为子项订单 + 工序分配记录，状态改为已排产
     * 涉及表：
     *  - t_production_order（主表，状态更新）
     *  - t_bom_used（物料BOM结构）
     *  - t_order_dtl（构建子项销售明细 + 中间件需求）
     *  - mes_procedure（查询工序）
     *  - t_proc_allocation（工序分配）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startSchedule01(List<String> ids) {
        String uid = UserUtil.uid();

        // 步骤1：查询生产订单
        List<ProductionOrder> orderList = this.listByIds(ids);
        if (CollectionUtils.isEmpty(orderList)) return;

        // 步骤2：过滤未排产的订单
        List<ProductionOrder> needScheduleOrders = orderList.stream()
                .filter(o -> !ProductionStatus.SCHEDULED.getCode().equals(o.getStatus()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(needScheduleOrders)) return;

        // 步骤3：提取所有 itemNo，用于后续查询 BOM 和工序路径 601682
        List<String> itemNos = needScheduleOrders.stream().map(ProductionOrder::getItemNo).collect(Collectors.toList());

        // 步骤4：查询 BOM 依赖（子项列表）
        Map<String, List<BomUsed>> usedMap = bomUsedService.getBomDepend(itemNos);

        // 步骤5：提取 BOM 子项中的 BOM 类型（只处理 use_item_type=01 的）
        List<String> bomItemNos = usedMap.values().stream()
                .flatMap(List::stream)
                .filter(bom -> ItemType.isBom(bom.getUseItemType()))
                .map(BomUsed::getUseItemNo)
                .distinct()
                .collect(Collectors.toList());

        // 步骤6：查询已有的订单明细（t_order_dtl），用于匹配子项
        List<OrderDtl> dtls = orderDtlService.selectCurrentMonth(bomItemNos);
        Map<String, OrderDtl> dtlMap = dtls.stream().collect(Collectors.toMap(OrderDtl::getItemNo, Function.identity()));

        // 步骤7：查询工序路径（mes_procedure）一次性查出所有子项的
        List<MesProcedure> procedures = procedureService.list(new LambdaQueryWrapper<MesProcedure>()
                .in(MesProcedure::getItemNo, bomItemNos));
        Map<String, List<MesProcedure>> procMap = procedures.stream()
                .collect(Collectors.groupingBy(MesProcedure::getItemNo));

        // 准备工序分配记录
        Map<String, List<ProcAllocation>> allocMap = new HashMap<>();

        for (ProductionOrder order : needScheduleOrders) {
            String parentItemNo = order.getItemNo();
            List<BomUsed> children = usedMap.get(parentItemNo);
            if (CollectionUtils.isEmpty(children)) continue;

            for (BomUsed used : children) {
                // 跳过原材料（只处理 BOM 类型）
                if (ItemType.isMaterials(used.getUseItemType())) continue;

                String childItemNo = used.getUseItemNo();
                BigDecimal qty = ArithUtil.mul(used.getUseItemCount(), order.getItemCount()); // 拆出数量

                // 查是否已存在订单明细记录
                OrderDtl dtl = dtlMap.get(childItemNo);
                if (dtl != null) {
                    dtl.setItemCount(ArithUtil.add(dtl.getItemCount(), qty)); // 累加数量
                    dtl.setMidCount(ArithUtil.add(dtl.getMidCount(), qty));   // 中间件需求数量
                    dtl.setOrderDtlStatus(DtlStatusEnum.IN_PRODUCED.getCode());
                } else {
                    // 不存在则构建新的明细记录
                    dtl = buildDtlV1(childItemNo, qty, uid);
                    dtlMap.put(childItemNo, dtl);
                }

                // 为该子项生成工序分配
                List<MesProcedure> procList = procMap.get(childItemNo);
                List<ProcAllocation> paList = allocMap.computeIfAbsent(dtl.getId(), k -> new ArrayList<>());
                handleAlloc(paList, qty, procList, dtl);
            }
        }

        // 步骤8：更新生产订单状态为“已排产”
        for (ProductionOrder o : needScheduleOrders) {
            o.setStatus(ProductionStatus.SCHEDULED.getCode());
        }
        this.updateBatchById(needScheduleOrders);

        // 步骤9：批量落库
        orderDtlService.saveOrUpdateBatch(dtlMap.values());

        List<ProcAllocation> allocations = allocMap.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        procAllocationService.saveOrUpdateBatch(allocations);
    }



    /**
     * 简化版下排产方法（集中逻辑，MP操作） --  不带工序
     * 目标：将生产订单转为子项订单 + 工序分配记录，状态改为已排产
     * -- 注意：这个查询只显示了有子项的物料，而没有显示没有子项的物料
     * 涉及表：
     *  - t_production_order（主表，状态更新）
     *  - t_bom_used（物料BOM结构）
     *  - t_order_dtl（构建子项销售明细 + 中间件需求）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startSchedule02(String productionOrderId) {
        String uid = UserUtil.uid();

        // 1. 查询生产订单
        ProductionOrder po = this.getById(productionOrderId);
        if (po == null) {
            throw new RuntimeException("未找到生产订单，ID: " + productionOrderId);
        }

        String parentItemNo = po.getItemNo();
        BigDecimal parentCount = po.getItemCount();
        String salesOrderNo = po.getSalesOrderNo();

        log.info("开始拆解物料 itemNo = {}, 拆解数量 = {}", parentItemNo, parentCount);

        // 2. 查询 BOM 子项，排除自引用 BOM（use_item_no != item_no）
        List<BomUsed> bomList = bomUsedService.list(new LambdaQueryWrapper<BomUsed>()
                .eq(BomUsed::getItemNo, parentItemNo)
                .eq(BomUsed::getUseItemType, "01")
                .ne(BomUsed::getUseItemNo, parentItemNo)); // 排除自身作为子项

        if (CollectionUtils.isEmpty(bomList)) {
            throw new RuntimeException("物料 " + parentItemNo + " 无 BOM 子项（或仅有自引用），无法拆单");
        }

        log.info("查询到 BOM 子项数量：{}", bomList.size());


        // 3. 查询所有子项的物料名称（用于展示）（备用  禁止删！）
//        Set<String> itemNos = bomList.stream()
//                .map(BomUsed::getUseItemNo)
//                .collect(Collectors.toSet());
//
//        Map<String, String> itemNameMap = mesItemStockService.list(new LambdaQueryWrapper<MesItemStock>()
//                        .in(MesItemStock::getItemNo, itemNos))
//                .stream()
//                .collect(Collectors.toMap(MesItemStock::getItemNo, MesItemStock::getItemName));


        // 4、 构建订单明细记录
        List<OrderDtl> dtlList = new ArrayList<>();
        String orderNo = (salesOrderNo != null && !"".equals(salesOrderNo)) ? salesOrderNo : seqUtil.getOrderNo();

        for (BomUsed bom : bomList) {
            BigDecimal needQty = ArithUtil.mul(bom.getUseItemCount(), parentCount);
            String itemNo = bom.getUseItemNo();

            // ✅ 日志输出子项信息测试（含名称）
//            String itemName = itemNameMap.getOrDefault(itemNo, "【未知物料】"); （备用  禁止删！）
//            log.info("生成子项：itemNo={}, name={}, qty={}", itemNo, itemName, needQty); （备用  禁止删！）

            OrderDtl dtl = new OrderDtl();
            dtl.setId(SnowFlake.genId());
            dtl.setOrderNo(orderNo);
            dtl.setItemNo(bom.getUseItemNo());
            dtl.setItemCount(needQty);
            dtl.setProductionCount(BigDecimal.ZERO);
            dtl.setOrderDtlStatus(DtlStatusEnum.IN_PRODUCED.getCode());
            dtl.setNeedMidCount(needQty);
            dtl.setCreatedBy(uid);
            dtl.setUpdatedBy(uid);
            dtl.setCreatedTime(LocalDateTime.now());
            dtl.setUpdatedTime(LocalDateTime.now());

            dtlList.add(dtl);
        }

        // 4. 插入明细
        orderDtlService.saveBatch(dtlList);

        log.info("成功将生产订单 [{}] 拆解为 {} 条子项订单明细", productionOrderId, dtlList.size());
    }



    /////////////////////////////////////原始代码 ↓ /////////////////////////////////////////




    // 下排产
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void startSchedule(List<String> ids) {
        List<ProductionOrder> orders = this.listByIds(ids);
        String uid = UserUtil.uid();
        OrderSchedule os = new OrderSchedule();
        List<String> pids = new ArrayList<>();
        for (ProductionOrder order : orders) {
            if (StringUtils.equalsAny(order.getStatus(), ProductionStatus.SCHEDULED.getCode())) {
                continue;
            }
            pids.add(order.getId());
            order.setStatus(ProductionStatus.SCHEDULED.getCode());
        }
        this.updateBatchById(orders);
        if (CollectionUtils.isNotEmpty(pids)) {
            os.setUid(uid);
            os.setPids(pids);
            ThreadPool.singleExecute(() -> this.schedule(os));
        }
    }


    // 下排产 --> 触发排产
//    从 mes_procedure（ERP同步的临时工序中转表）中查询相关工序信息，插入到 t_proc_allocation（正式的工序分配表）
    public void schedule(OrderSchedule os) {
        long start = System.currentTimeMillis();
        try {
            log.info("开始排程:{}", JsonUtil.toJson(os));
            List<String> pids = os.getPids();
            List<ProductionOrder> pos = this.listByIds(pids);
            List<String> itemNos = pos.stream().map(ProductionOrder::getItemNo).collect(Collectors.toList());
            Map<String, List<BomUsed>> usedMap = bomUsedService.getBomDepend(itemNos);
            if (MapUtils.isEmpty(usedMap)) {
                return;
            }
            String uid = os.getUid();
            List<String> bomItemNos =
                    usedMap.values().stream().flatMap(List::stream).filter(bomUsed -> ItemType.isBom(bomUsed.getUseItemType())).map(BomUsed::getUseItemNo).collect(Collectors.toList());
            List<OrderDtl> orderDtlList = orderDtlService.selectCurrentMonth(bomItemNos);
            Map<String, OrderDtl> dtlMap = orderDtlList.stream().collect(Collectors.toMap(OrderDtl::getItemNo,
                    Function.identity()));
            List<String> oids = dtlMap.values().stream().map(OrderDtl::getId).collect(Collectors.toList());
            Map<String, List<ProcAllocation>> paMap = new HashMap<>();
            List<ProductionOrder> rollOrders = new ArrayList<>();

            if (CollectionUtils.isNotEmpty(oids)) {
                List<ProcAllocation> paList =
                        procAllocationService.list(new LambdaQueryWrapper<ProcAllocation>().in(ProcAllocation::getOrderDtlId, oids));
                paMap = paList.stream().collect(Collectors.groupingBy(ProcAllocation::getOrderDtlId));
            }
            //只有工序的订单
            List<ProductionOrder> ppos =
                    pos.stream().filter(data -> StringUtils.isNotEmpty(data.getProcedureCode())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(ppos)) {
                procedureBom(uid, ppos, dtlMap, paMap);
            }
            //正常的订单
            List<ProductionOrder> bpos =
                    pos.stream().filter(data -> StringUtils.isEmpty(data.getProcedureCode())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(bpos)) {
                splitBoms(uid, bpos, bomItemNos, dtlMap, usedMap, paMap, rollOrders);
            }
            SpringContextUtil.getBeanByClass(ProductionOrderService.class).saveProcAndDtl(dtlMap, paMap, rollOrders);
            if (CollectionUtils.isNotEmpty(bomItemNos)) {
                List<String> longCodes =
                        usedMap.values().stream().flatMap(List::stream).filter(bomUsed -> ItemType.isBom(bomUsed.getUseItemType())).map(BomUsed::getItemNos).collect(Collectors.toList());
                ThreadPool.commonExecute(() -> outStoreManager.generateOutStore(new ArrayList<>(dtlMap.values()),
                        longCodes));
            }
        } catch (Exception e) {
            log.error("工序分解异常,oids:{}", os.getPids(), e);
        }

        log.info("工序分解完成 cost:{}", System.currentTimeMillis() - start);
    }

    /**
     * 按工序下单 （有 procedure_code 的生产订单  - t_proc_allocation ）
     *
     * @param uid
     * @param ppos
     */
    private void procedureBom(String uid, List<ProductionOrder> ppos, Map<String, OrderDtl> dtlMap, Map<String,
            List<ProcAllocation>> paMap) {
        for (ProductionOrder po : ppos) {
            List<MesProcedure> list =
                    procedureService.list(new LambdaQueryWrapper<MesProcedure>().eq(MesProcedure::getItemNo,
                            po.getItemNo()).eq(MesProcedure::getProcedureCode, po.getProcedureCode()));
            String itemNo = po.getItemNo();
            OrderDtl dtl = dtlMap.get(po.getItemNo());
            BigDecimal itemCount = po.getItemCount();
            if (Objects.nonNull(dtl)) {
                List<ProcAllocation> paList = paMap.getOrDefault(dtl.getId(), new ArrayList<>());
                handleAlloc(paList, itemCount, list, dtl);
                paMap.put(dtl.getId(), paList);
                dtl.setOrderDtlStatus(DtlStatusEnum.IN_PRODUCED.getCode());
            } else {
                dtl = this.buildDtlV1(itemNo, BigDecimal.ZERO, uid);
                List<ProcAllocation> paList = getProcAllocations(uid, dtl, list, itemCount);
                paMap.put(dtl.getId(), paList);
                dtlMap.put(dtl.getItemNo(), dtl);
            }
            dtlMap.put(dtl.getItemNo(), dtl);
        }

    }


    /**
     * 拆分工单（没有 procedure_code 的生产订单 - t_proc_allocation ）
     *
     * @param uid
     * @param pos
     */
    private void splitBoms(String uid, List<ProductionOrder> pos, List<String> bomItemNos, Map<String, OrderDtl> dtlMap,
                           Map<String, List<BomUsed>> usedMap, Map<String, List<ProcAllocation>> paMap,
                           List<ProductionOrder> rollOrders) {

        List<MesProcedure> procs =
                procedureService.list(new LambdaQueryWrapper<MesProcedure>().in(MesProcedure::getItemNo, bomItemNos));
        Map<String, List<MesProcedure>> procMap =
                procs.stream().collect(Collectors.groupingBy(MesProcedure::getItemNo));
        for (ProductionOrder po : pos) {
            try {
                List<BomUsed> bomUseds = usedMap.get(po.getItemNo());
                for (BomUsed used : bomUseds) {
                    String useItemNo = used.getUseItemNo();
                    if (ItemType.isMaterials(used.getUseItemType())) {
                        continue;
                    }
                    BigDecimal itemCount = ArithUtil.mul(used.getUseItemCount(), po.getItemCount());
                    OrderDtl dtl = dtlMap.get(useItemNo);
                    List<MesProcedure> procList = procMap.get(useItemNo);
                    if (Objects.nonNull(dtl)) {
                        dtl.setItemCount(ArithUtil.add(dtl.getItemCount(), itemCount));
                        List<ProcAllocation> paList = paMap.getOrDefault(dtl.getId(), new ArrayList<>());
                        handleAlloc(paList, itemCount, procList, dtl);
                        paMap.put(dtl.getId(), paList);
                        dtl.setMidCount(itemCount.add(dtl.getMidCount()));
                        dtl.setOrderDtlStatus(DtlStatusEnum.IN_PRODUCED.getCode());
                    } else {
                        dtl = buildDtlV1(useItemNo, itemCount, uid);
                        List<ProcAllocation> paList = getProcAllocations(uid, dtl, procList, dtl.getItemCount());
                        paMap.put(dtl.getId(), paList);
                        dtlMap.put(dtl.getItemNo(), dtl);
                    }
                }
            } catch (Exception e) {
                log.error("排成异常,pid:{}", po.getId(), e);
                rollOrders.add(po);
            }
        }

    }


    /**
     * 插入对象字段构造逻辑（ t_proc_allocation ）
     *
     * @param uid
     */
    @NotNull
    private static List<ProcAllocation> getProcAllocations(String uid, OrderDtl dtl, List<MesProcedure> list,
                                                           BigDecimal count) {
        List<ProcAllocation> paList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (MesProcedure proc : list) {
                ProcAllocation pa = PojoUtil.copyBean(proc, ProcAllocation.class);
                pa.setId(null);
                pa.setUpdatedBy(uid);
                pa.setHoursFixed(proc.getHoursWork());
                pa.setOrderDtlId(dtl.getId());
                pa.setOrderNo(dtl.getOrderNo());
                pa.setOrgiTotalCount(count);
                pa.setTotalCount(count);
                pa.setProcStatus(ProcStatus.RUNNING.getCode());
                pa.setCreatedTime(LocalDateTime.now());
                pa.setCreatedBy(uid);
                pa.setUpdatedTime(LocalDateTime.now());
                paList.add(pa);
            }
        }

        return paList;
    }



    private void handleAlloc(List<ProcAllocation> paList, BigDecimal itemCount, List<MesProcedure> procList,
                             OrderDtl dtl) {
        Map<String, ProcAllocation> map = paList.stream().collect(Collectors.toMap(ProcAllocation::getProcedureCode,
                Function.identity()));
        if (CollectionUtils.isNotEmpty(procList)) {
            for (MesProcedure mp : procList) {
                ProcAllocation pa = map.get(mp.getProcedureCode());
                if (Objects.nonNull(pa)) {
                    pa.setTotalCount(ArithUtil.add(pa.getTotalCount(), itemCount));
                    pa.setOrgiTotalCount(ArithUtil.add(pa.getOrgiTotalCount(), itemCount));
                    pa.setDeptId(pa.getDeptId());
                    pa.setDeviceId(mp.getDeviceId());
                    pa.setHoursFixed(mp.getHoursWork());
                    pa.setSeqNo(mp.getSeqNo());
                } else {
                    List<ProcAllocation> pas = getProcAllocations(dtl.getCreatedBy(), dtl,
                            Collections.singletonList(mp), itemCount);
                    paList.addAll(pas);

                }
            }
        }


    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveProcAndDtl(Map<String, OrderDtl> dtlMap, Map<String, List<ProcAllocation>> paMap,
                               List<ProductionOrder> rollOrders) {
        if (CollectionUtils.isNotEmpty(rollOrders)) {
            List<String> ids = rollOrders.stream().map(ProductionOrder::getId).collect(Collectors.toList());
            log.info("需要回滚的订单:{}", JsonUtil.toJson(ids));
            List<ProductionOrder> list = ids.stream().map(id -> {
                ProductionOrder productionOrder = new ProductionOrder();
                productionOrder.setId(id);
                productionOrder.setStatus(ProductionStatus.TO_READY.getCode());
                return productionOrder;
            }).collect(Collectors.toList());
            this.updateBatchById(list);
        }
        if (CollectionUtils.isNotEmpty(dtlMap.values())) {
            orderDtlService.saveOrUpdateBatch(dtlMap.values());
        }
        if (CollectionUtils.isNotEmpty(paMap.values())) {
            List<ProcAllocation> list =
                    paMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
            PojoUtil.executeBatch(list, procAllocationService::saveOrUpdateBatch);
        }
    }

    private OrderDtl buildDtlV1(String itemNo, BigDecimal count, String uid) {
        OrderDtl dtl;
        dtl = new OrderDtl();
        String id = SnowFlake.genId();
        String orderNo = seqUtil.getOrderNo();
        dtl.setId(id);
        dtl.setOrderNo(orderNo);
        dtl.setItemNo(itemNo);
        dtl.setProductionCount(BigDecimal.ZERO);
        dtl.setItemCount(count);
        dtl.setMidCount(dtl.getItemCount());
        dtl.setOrderDtlStatus(DtlStatusEnum.IN_PRODUCED.getCode());
        dtl.setCreatedBy(uid);
        dtl.setUpdatedBy(uid);
        return dtl;
    }

    @Override
    public BaseResponse<List<ProductionOrderPageResponse>> pageList(BaseRequest<ProductionOrderPageRequest> request) {
        PageUtil<ProductionOrderPageResponse, ProductionOrderPageRequest> pu = (p, q) -> mapper.pageList(p, q);
        return pu.page(request.getPage(), MsgUtil.params(request));
    }
}
