package cn.jb.boot.biz.order.service.impl;

import cn.jb.boot.biz.item.entity.BomUsed;
import cn.jb.boot.biz.item.mapper.BomUsedMapper;
import cn.jb.boot.biz.order.entity.OrderDtl;
import cn.jb.boot.biz.order.entity.ProcAllocation;
import cn.jb.boot.biz.order.mapper.OrderDtlMapper;
import cn.jb.boot.biz.order.service.OrderDtlService;
import cn.jb.boot.biz.order.vo.request.OrderAllDtlUpdateStatusRequest;
import cn.jb.boot.biz.order.vo.request.OrderDtlPageRequest;
import cn.jb.boot.biz.order.vo.request.OrderDtlUpdateStatusRequest;
import cn.jb.boot.biz.order.vo.response.OrderDtlPageResponse;
import cn.jb.boot.biz.order.vo.response.WarningResponse;
import cn.jb.boot.biz.production.entity.ProductionOrder;
import cn.jb.boot.biz.production.service.ProductionOrderService;
import cn.jb.boot.biz.sales.entity.SaleOrderPlace;
import cn.jb.boot.biz.sales.service.SaleOrderPlaceService;
import cn.jb.boot.biz.work.entity.WorkReport;
import cn.jb.boot.biz.work.service.WorkReportService;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.com.response.PagingResponse;
import cn.jb.boot.system.entity.UserRole;
import cn.jb.boot.system.service.UserRoleService;
import cn.jb.boot.util.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mortbay.util.ajax.JSON;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;




/**
 * 订单明细表 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-05 19:00:38
 */
@Service
public class OrderDtlServiceImpl extends ServiceImpl<OrderDtlMapper, OrderDtl> implements OrderDtlService {

    @Resource
    private OrderDtlMapper mapper;
    @Resource
    private BomUsedMapper bomUsedMapper;
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private SaleOrderPlaceService saleOrderPlaceService;
    @Resource
    private ProductionOrderService productionOrderService;
    @Resource
    private WorkReportService workReportService;


    // -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -新增// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// ---


    //    使用树代替组  可行   优化根节点信息
    public BaseResponse<List<OrderDtlPageResponse>> pageTreeWithItemName(OrderDtlPageRequest dto) {
        try {
            // 1. 统计不同 orderNo 的组数
            long totalGroup = mapper.countGroupByOrderNo(dto);

            // 1.1 如果没有数据，直接返回空分页
            if (totalGroup == 0) {
                Paging emptyPaging = new Paging();
                emptyPaging.setPageNum(dto.getPageNum());
                emptyPaging.setPageSize(dto.getPageSize());
                return MsgUtil.emptyPage(emptyPaging);
            }

            // 2. 计算偏移量并查询本页需要的 orderNo 列表
            long offset = (long)(dto.getPageNum() - 1) * dto.getPageSize();
            List<String> orderNos = mapper.selectPageOrderNos(dto, offset, dto.getPageSize());

            // 2.1 如果 orderNos 为空（极端兜底），返回空列表 + 分页信息
            if (CollectionUtils.isEmpty(orderNos)) {
                PagingResponse pr = new PagingResponse();
                pr.setPageNum(dto.getPageNum());
                pr.setPageSize(dto.getPageSize());
                pr.setTotalNum((int) totalGroup);
                pr.setPages((int)((totalGroup + dto.getPageSize() - 1) / dto.getPageSize()));
                BaseResponse<List<OrderDtlPageResponse>> resp = new BaseResponse<>();
                resp.setTxStatus("00");
                resp.setData(Collections.emptyList());
                resp.setPage(pr);
                return resp;
            }

            // 3. 批量查询这些 orderNo 下的所有明细
            List<OrderDtlPageResponse> details = mapper.selectDtlWithItemAndTopBomByOrderNos(orderNos, dto);



            // 4. 按 orderNo 分组并组装成树形结构
            Map<String, List<OrderDtlPageResponse>> grouped = details.stream().collect(Collectors.groupingBy(
                            OrderDtlPageResponse::getOrderNo,
                            LinkedHashMap::new,
                            Collectors.toList()));

            // 4.1 构建树形结构
            List<OrderDtlPageResponse> treeList = new ArrayList<>();
            for (List<OrderDtlPageResponse> group : grouped.values()) {
                String topBomNo = group.get(0).getTopBomNo();
                String orderNo = group.get(0).getOrderNo();

                // 构造子项
                List<OrderDtlPageResponse> children = group.stream()
                        .map(item -> {
                            OrderDtlPageResponse copy = new OrderDtlPageResponse();
                            copy.setId(item.getId());
                            copy.setOrderNo(item.getOrderNo());
                            copy.setTopBomNo(item.getTopBomNo());
                            copy.setBomNo(item.getBomNo());
                            copy.setItemName(item.getItemName());
                            copy.setItemNo(item.getItemNo());
                            copy.setItemCount(item.getItemCount());
                            copy.setProductionCount(item.getProductionCount());
                            copy.setOrderDtlStatus(item.getOrderDtlStatus());
                            copy.setCreatedTime(item.getCreatedTime());
                            copy.setUpdatedTime(item.getUpdatedTime());
                            return copy;
                        })
                        .collect(Collectors.toList());


                // 构造根节点（使用找到的那条记录作为来源）
                OrderDtlPageResponse root = new OrderDtlPageResponse();
                root.setId(UUID.randomUUID().toString()); // 虚拟 ID 不变
                root.setOrderNo(orderNo);
                root.setTopBomNo(topBomNo);
                root.setBomNo(topBomNo); // 默认等于 topBomNo
                root.setItemCount(BigDecimal.ZERO);
                root.setProductionCount(BigDecimal.ZERO);
                root.setOrderDtlStatus("03");
                //如果有数据则覆盖
                Optional<OrderDtlPageResponse> matchOpt = children.stream()              // 先尝试找出那条 bomNo == topBomNo 的记录
                        .filter(i -> i.getBomNo() != null && i.getBomNo().equals(topBomNo))
                        .findFirst();
                if (matchOpt.isPresent()) {
                    OrderDtlPageResponse match = matchOpt.get();
                    root.setItemCount( match.getItemCount());
                    root.setProductionCount( match.getProductionCount());
                    root.setItemName(match.getItemName());
                    root.setItemNo(match.getItemNo());
                    root.setCreatedTime(match.getCreatedTime());
                    root.setUpdatedTime(match.getUpdatedTime());
                } else {
                    root.setItemName("产品名称"); // 兜底
                }


                //装载：把子节点装载仅父跟
                root.setChildren(children); // ✅ 安全拷贝，不会污染原始 group

                // 包装器返回
                treeList.add(root);
            }



            // 5. 构造统一的分页响应
            PagingResponse pr = new PagingResponse();
            pr.setPageNum(dto.getPageNum());
            pr.setPageSize(dto.getPageSize());
            pr.setTotalNum((int) totalGroup);
            pr.setPages((int)((totalGroup + dto.getPageSize() - 1) / dto.getPageSize()));

            BaseResponse<List<OrderDtlPageResponse>> resp = new BaseResponse<>();
            resp.setTxStatus("00");
            resp.setData(treeList);
            resp.setPage(pr);
            return resp;

        } catch (Exception ex) {
            // 捕获所有异常并透传给前端
            return MsgUtil.fail("分页查询失败：" + ex.getMessage());
        }
    }




    //    使用树代替组  可行
    public BaseResponse<List<OrderDtlPageResponse>> pageTreeWithItemName成功(OrderDtlPageRequest dto) {
        try {
            // 1. 统计不同 orderNo 的组数
            long totalGroup = mapper.countGroupByOrderNo(dto);

            // 1.1 如果没有数据，直接返回空分页
            if (totalGroup == 0) {
                Paging emptyPaging = new Paging();
                emptyPaging.setPageNum(dto.getPageNum());
                emptyPaging.setPageSize(dto.getPageSize());
                return MsgUtil.emptyPage(emptyPaging);
            }

            // 2. 计算偏移量并查询本页需要的 orderNo 列表
            long offset = (long)(dto.getPageNum() - 1) * dto.getPageSize();
            List<String> orderNos = mapper.selectPageOrderNos(dto, offset, dto.getPageSize());

            // 2.1 如果 orderNos 为空（极端兜底），返回空列表 + 分页信息
            if (CollectionUtils.isEmpty(orderNos)) {
                PagingResponse pr = new PagingResponse();
                pr.setPageNum(dto.getPageNum());
                pr.setPageSize(dto.getPageSize());
                pr.setTotalNum((int) totalGroup);
                pr.setPages((int)((totalGroup + dto.getPageSize() - 1) / dto.getPageSize()));

                BaseResponse<List<OrderDtlPageResponse>> resp = new BaseResponse<>();
                resp.setTxStatus("00");
                resp.setData(Collections.emptyList());
                resp.setPage(pr);
                return resp;
            }

            // 3. 批量查询这些 orderNo 下的所有明细
//            List<OrderDtlPageResponse> details = mapper.selectDetailsByOrderNos(orderNos);
            // 改为调用新增的方法
             List<OrderDtlPageResponse> details = mapper.selectDtlWithItemAndTopBomByOrderNos(orderNos, dto);



            // 4. 按 orderNo 分组并组装成树形结构
            Map<String, List<OrderDtlPageResponse>> grouped = details.stream()
                    .collect(Collectors.groupingBy(
                            OrderDtlPageResponse::getOrderNo,
                            LinkedHashMap::new,
                            Collectors.toList()));

            List<OrderDtlPageResponse> treeList = new ArrayList<>();
            for (List<OrderDtlPageResponse> group : grouped.values()) {
                // 取第一条作为根节点
                OrderDtlPageResponse root = group.get(0);

                // 如果该组不止一条，则把剩余的作为 children
                if (group.size() > 1) {
                    // 注意 subList(1, size) 不会包含根节点自己
                    List<OrderDtlPageResponse> children = new ArrayList<>(group.subList(1, group.size()));
                    root.setChildren(children);
                }
                treeList.add(root);
            }

            // 5. 构造统一的分页响应
            PagingResponse pr = new PagingResponse();
            pr.setPageNum(dto.getPageNum());
            pr.setPageSize(dto.getPageSize());
            pr.setTotalNum((int) totalGroup);
            pr.setPages((int)((totalGroup + dto.getPageSize() - 1) / dto.getPageSize()));

            BaseResponse<List<OrderDtlPageResponse>> resp = new BaseResponse<>();
            resp.setTxStatus("00");
            resp.setData(treeList);
            resp.setPage(pr);
            return resp;

        } catch (Exception ex) {
            // 捕获所有异常并透传给前端
            return MsgUtil.fail("分页查询失败：" + ex.getMessage());
        }
    }



    /**
     * 分组按 order_no，取每组最新 created_time，并分页返回 ， 可行
     */
    public BaseResponse<List<OrderDtlPageResponse>> pageWithItemName01(OrderDtlPageRequest dto) {

        try {
            /* ---------- 1. 统计分组总数 ---------- */
            long totalGroup = mapper.countGroupByOrderNo(dto);
            if (totalGroup == 0) {
                // 用框架自带工具返回空分页
                Paging paging = new Paging();
                paging.setPageNum(dto.getPageNum());
                paging.setPageSize(dto.getPageSize());
                return MsgUtil.emptyPage(paging);
            }

            /* ---------- 2. 取当前页需要显示的 order_no 列表 ---------- */
            long offset = (long) (dto.getPageNum() - 1) * dto.getPageSize();
            List<String> pageOrderNos =
                    mapper.selectPageOrderNos(dto, offset, dto.getPageSize());

            if (pageOrderNos.isEmpty()) {
                // 理论上不会为空，但兜底处理
                PagingResponse pr = new PagingResponse();
                pr.setPageNum(dto.getPageNum());
                pr.setPageSize(dto.getPageSize());
                pr.setTotalNum(totalGroup);
                pr.setPages((int) ((totalGroup + dto.getPageSize() - 1) / dto.getPageSize()));

                BaseResponse<List<OrderDtlPageResponse>> resp = new BaseResponse<>();
                resp.setTxStatus("00");
                resp.setData(Collections.emptyList());
                resp.setPage(pr);
                return resp;
            }

            /* ---------- 3. 查询这一批 order_no 的全部明细 ---------- */
            List<OrderDtlPageResponse> details =
                    mapper.selectDetailsByOrderNos(pageOrderNos);

            /* ---------- 4. 组装分页响应 ---------- */
            PagingResponse pr = new PagingResponse();
            pr.setPageNum(dto.getPageNum());
            pr.setPageSize(dto.getPageSize());
            pr.setTotalNum(totalGroup);
            pr.setPages((int) ((totalGroup + dto.getPageSize() - 1) / dto.getPageSize()));

            BaseResponse<List<OrderDtlPageResponse>> resp = new BaseResponse<>();
            resp.setTxStatus("00");
            resp.setData(details);
            resp.setPage(pr);
            return resp;

        } catch (Exception ex) {
            // 将真实异常透传给前端，便于调试
            return MsgUtil.fail("分页查询失败：" + ex.getMessage());
        }
    }

    // -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// - // -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// -// ---


    // 查询t_order_dtl明细
    public BaseResponse<List<OrderDtlPageResponse>> pageWithItemName(OrderDtlPageRequest dto) {
        // 构建分页对象
        Page<OrderDtlPageResponse> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        // 查询分页数据
        IPage<OrderDtlPageResponse> pageData = mapper.selectDtlWithItemPage(page, dto);


        // 封装分页响应
        PagingResponse paging = new PagingResponse();
        paging.setPageNum((int) pageData.getCurrent());
        paging.setPageSize((int) pageData.getSize());
        paging.setTotalNum((int) pageData.getTotal());
        paging.setPages((int) pageData.getPages());

        // 构造返回
        BaseResponse<List<OrderDtlPageResponse>> response = new BaseResponse<>();
        response.setTxStatus("00");
        response.setData(pageData.getRecords());
        response.setPage(paging);
        return response;
    }


    @Override
    public BaseResponse<List<OrderDtlPageResponse>> pageInfo(Paging page, OrderDtlPageRequest params) {
        if (StringUtils.isNotEmpty(params.getParentItemNo())) {
            List<BomUsed> list = bomUsedMapper.selectList(new LambdaQueryWrapper<BomUsed>().eq(BomUsed::getItemNo, params.getParentItemNo()));
            if (CollectionUtils.isNotEmpty(list)) {
                List<String> itemNos = list.stream().map(BomUsed::getUseItemNo).collect(Collectors.toList());
                params.setChildItemNos(itemNos);
            }
        }
        PageUtil<OrderDtlPageResponse, OrderDtlPageRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }

    @Override
    public List<OrderDtl> selectCurrentMonth(List<String> bomItemNos) {
        return mapper.selectCurrentMonth(bomItemNos);
    }

    @Override
    public void updateStatus(OrderDtlUpdateStatusRequest params) {
        OrderDtl orderDtl = PojoUtil.copyBean(params, OrderDtl.class);
        this.updateById(orderDtl);
    }

    @Override
    public void updateAllStatus(OrderAllDtlUpdateStatusRequest params) {
        List<String> ids = params.getIds();
        for(String id:ids){
            OrderDtl orderDtl = new OrderDtl();
            orderDtl.setId(id);
            orderDtl.setOrderDtlStatus("06");
            this.updateById(orderDtl);
        }
    }

    @Override
    public String getProcTodayDatas(Map<String,Object> params) {
        Map<String,Object> dataMap = new HashMap<>();
        String date = DateUtil.formatDateTime(DateUtil.nowDateTime(),"yyyy-MM-dd");
        List<Map<String,Object>>  dataList = mapper.getProcTodayDatas(date);
        String[] hours = new String[dataList.size()];
        String[] hoursCount = new String[dataList.size()];
        for(int i = 0;i< dataList.size();i++){
            hours[i] = dataList.get(i).get("updatedTime").toString().substring(11,13);
            hoursCount[i] = dataList.get(i).get("productionCount").toString();
        }
        dataMap.put("hours",hours);
        dataMap.put("hoursCount",hoursCount);
        return JSON.toString(dataMap);
    }

    @Override
    public BaseResponse<List<WarningResponse>> getWarning() {
        String uid = UserUtil.uid();
        List<String> userRole = userRoleService.userRoles(uid);
        BaseResponse<List<WarningResponse>> retrunData = new BaseResponse<>();
        List<WarningResponse> warningList = new ArrayList<>();
        //获取登录人的信息
        WarningResponse warningResponse = new WarningResponse();
        if(userRole.contains("JS000000033") || userRole.contains("JS000000005")){
            //车间主任技术员，进行审批
            List<Map<String,Object>> isApproveList = saleOrderPlaceService.listMaps(new LambdaQueryWrapper<SaleOrderPlace>().eq(SaleOrderPlace::getPlaceStatus,
                    "00"));
            if(isApproveList != null && isApproveList.size() != 0){
                warningResponse.setData("有待审批订单，请及时处理");
            }
        }else if(userRole.contains("JS000000033") || userRole.contains("JS000000005")){
            //审批完成，需要排程
            List<ProductionOrder> productOrderList = productionOrderService.list(new LambdaQueryWrapper<ProductionOrder>().eq(ProductionOrder::getStatus,
                    "01"));
            if(productOrderList != null && productOrderList.size() != 0){
                warningResponse.setData("有待排程的订单，请及时处理");
            }
        }else if(userRole.contains("JS000000038")){
            //验收审批
           List<WorkReport> workReportList = workReportService.list(new LambdaQueryWrapper<WorkReport>().ne(WorkReport::getStatus,
                    "03"));
           if(workReportList != null || workReportList.size() != 0){
               warningResponse.setData("有待审批的报工订单，请及时处理");
           }
        }else if(userRole.contains("JS000000034") ){
            String nowDateStart = DateUtil.formatDateTime(DateUtil.nowDateTime().minusDays(2),"yyyy-MM-dd HH:mm:ss");
            String nowDateEnd = DateUtil.formatDateTime(DateUtil.nowDateTime(),"yyyy-MM-dd HH:mm:ss");
            //临近发货日期
            List<SaleOrderPlace> saleOrderPlaceList = saleOrderPlaceService.list(new LambdaQueryWrapper<SaleOrderPlace>().between(SaleOrderPlace::getDeliverTime,
                    nowDateStart,nowDateEnd));
            if(saleOrderPlaceList != null || saleOrderPlaceList.size() != 0){
                warningResponse.setData("有快到发货日期的订单，请及时处理");
            }
        }
        warningList.add(warningResponse);
        retrunData.setData(warningList);
        return retrunData;
    }
}
