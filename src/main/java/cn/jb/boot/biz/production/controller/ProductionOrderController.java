package cn.jb.boot.biz.production.controller;

import cn.jb.boot.biz.production.dto.BomTreeNodeVo;
import cn.jb.boot.biz.production.dto.BomTreePageDto;
import cn.jb.boot.biz.production.service.ProductionOrderService;
import cn.jb.boot.biz.production.service.impl.ProductionOrderServiceImpl;
import cn.jb.boot.biz.production.vo.req.ProductionOrderPageRequest;
import cn.jb.boot.biz.production.vo.resp.ProductionOrderPageResponse;
import cn.jb.boot.biz.sales.vo.request.ComIdsReq;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;

import cn.jb.boot.util.MsgUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import org.springframework.util.CollectionUtils; // 修改：引入 Spring 提供的 CollectionUtils，解决“找不到”问题



/**
 * 生产管理
 *
 * @author user
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-01-15 21:08:58
 */
@RestController
@RequestMapping("/api/production/production_order")
@Tag(name = "production-order-controller", description = "生产任务单接口")
public class ProductionOrderController {

    @Autowired
    private ProductionOrderService service;

    @Autowired
    private ProductionOrderServiceImpl iservice;


    //table  bom树  (2516004414-1 测试成功)
    @PostMapping("/getBomTreePage")
    @Operation(summary = "分页获取 BOM 树")
    public BaseResponse<List<BomTreeNodeVo>> getBomTreePage(@RequestBody BomTreePageDto dto) {
        return iservice.getBomTreePage(dto);
    }



//    删除
    @PostMapping("/delete")
    @Operation(summary = "删除生产工单记录（支持批量）")
    public BaseResponse<String> delete(@RequestBody @Valid BaseRequest<ComIdsReq> request) {
        List<String> ids = MsgUtil.params(request).getIds();

        if (CollectionUtils.isEmpty(ids)) {
            return MsgUtil.fail("未指定删除的工单ID");
        }

        try {
            service.delete(ids);
            return MsgUtil.ok("删除成功");
        } catch (RuntimeException ex) {
            return MsgUtil.fail("删除失败：" + ex.getMessage());
        }
    }




    /**
     *  下排产 （单个拆单） 单层  --  逻辑整改版本
     *  -- 注意：这个查询只显示了有子项的物料，而没有显示没有子项的物料
     *  {
     *   "params": {
     *     "ids": ["449961488933900288"]
     *   }
     * }
     */
//    @PostMapping("/start_scheduled")
//    @Operation(summary = "BOM 拆单（根据 bomNo）")
//    public BaseResponse<String> startScheduled(@RequestBody Map<String, Object> body) {
//        Object paramObj = body.get("params");
//
//        if (paramObj instanceof Map) {
//            Map<?, ?> params = (Map<?, ?>) paramObj;
//
//            String bomNo = Objects.toString(params.get("bomNo"), null);
//            String salesOrderNo = Objects.toString(params.get("salesOrderNo"), null);
//            String rootItemNo = Objects.toString(params.get("rootItemNo"), null);
//            Object rootCountObj = params.get("rootCount");
//
//            if (StringUtils.isAnyBlank(bomNo, rootItemNo) || rootCountObj == null) {
//                return MsgUtil.fail("参数缺失");
//            }
//
//
//// 数量格式校验
//            BigDecimal rootCount;
//            try {
//                rootCount = new BigDecimal(rootCountObj.toString());
//            } catch (NumberFormatException e) {
//                return MsgUtil.fail("数量格式不正确");
//            }
//
//            try {
//                // 调用 Service，里面会校验状态是否为“01”
//                iservice.startSchedule20(bomNo, salesOrderNo, rootItemNo, rootCount);
//                return MsgUtil.ok("拆单成功");
//            } catch (RuntimeException ex) {
//                return MsgUtil.fail(ex.getMessage());
//            }
//        }
//
//        return MsgUtil.fail("参数格式错误");
//    }




    /**
     *  下排产 （多个拆单） 单层  --  逻辑整改版本
     *  -- 注意：这个查询只显示了有子项的物料，而没有显示没有子项的物料
     *  {
     *   "params": {
     *     "ids": ["449961488933900288"]
     *   }
     * }
     */
    @PostMapping("/start_scheduled")
    @Operation(summary = "BOM 拆单（根据 bomNo），支持批量多选")
    public BaseResponse<String> startScheduled(@RequestBody Map<String, Object> body) {
        Object paramObj = body.get("params");

        // 批量处理
        if (paramObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> listParams = (List<Map<String, Object>>) paramObj;
            if (CollectionUtils.isEmpty(listParams)) {
                return MsgUtil.fail("参数列表为空");
            }

            int success = 0, fail = 0;
            List<String> failDetails = new ArrayList<>(); // 用于收集失败明细

            for (Map<String, Object> params : listParams) {
                String bomNo = Objects.toString(params.get("bomNo"), null);
                String salesOrderNo = Objects.toString(params.get("salesOrderNo"), null);
                String rootItemNo = Objects.toString(params.get("rootItemNo"), null);
                Object rootCountObj = params.get("rootCount");
                // 必填校验
                if (StringUtils.isAnyBlank(bomNo, rootItemNo) || rootCountObj == null) {
                    fail++;
                    failDetails.add(String.format("bomNo=%s, rootItemNo=%s 缺失参数", bomNo, rootItemNo));
                    continue;
                }
                BigDecimal rootCount;
                try {
                    rootCount = new BigDecimal(rootCountObj.toString());
                } catch (NumberFormatException e) {
                    fail++;
                    failDetails.add(String.format("bomNo=%s, rootItemNo=%s 数量格式错误", bomNo, rootItemNo));
                    continue;
                }
                try {
                    iservice.startSchedule20(bomNo, salesOrderNo, rootItemNo, rootCount);
                    success++;
                } catch (RuntimeException ex) {
                    // ======【新增处理】======
                    String msg = ex.getMessage();
                    // 如果异常信息为已排产状态，则视为成功，直接计入success，不记录为fail
                    if (msg != null && msg.contains("生产订单状态不是“就绪”(01)，当前状态：02")) {
                        success++;
                        // 该情况不添加到failDetails
                    } else {
                        fail++;
                        failDetails.add(String.format("bomNo=%s, rootItemNo=%s 拆单失败: %s", bomNo, rootItemNo, msg));
                    }
                    // ======【end】======
                }
            }
            // 返回统计信息（如失败条数多，可做详细与简略提示切换）
            StringBuilder msg = new StringBuilder();
            msg.append("批量拆单完成：成功").append(success).append("条，失败").append(fail).append("条");
            if (!failDetails.isEmpty()) {
                msg.append("，失败原因示例：").append(failDetails.stream().limit(5).collect(Collectors.joining("；")));
                if (failDetails.size() > 5) msg.append(" 等，共").append(failDetails.size()).append("条。");
            }
            return MsgUtil.ok(msg.toString());
        }

        // 单条逻辑保持不变
        if (paramObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<?, ?> params = (Map<?, ?>) paramObj;
            String bomNo = Objects.toString(params.get("bomNo"), null);
            String salesOrderNo = Objects.toString(params.get("salesOrderNo"), null);
            String rootItemNo = Objects.toString(params.get("rootItemNo"), null);
            Object rootCountObj = params.get("rootCount");
            if (StringUtils.isAnyBlank(bomNo, rootItemNo) || rootCountObj == null) {
                return MsgUtil.fail("参数缺失");
            }
            BigDecimal rootCount;
            try {
                rootCount = new BigDecimal(rootCountObj.toString());
            } catch (NumberFormatException e) {
                return MsgUtil.fail("数量格式不正确");
            }
            try {
                iservice.startSchedule20(bomNo, salesOrderNo, rootItemNo, rootCount);
                return MsgUtil.ok("拆单成功");
            } catch (RuntimeException ex) {
                // 【可选】如需单条接口也忽略“已排产”，可复制批量同样处理
                String msg = ex.getMessage();
                if (msg != null && msg.contains("生产订单状态不是“就绪”(01)，当前状态：02")) {
                    return MsgUtil.ok("拆单成功（已排产）");
                }
                return MsgUtil.fail(msg);
            }
        }
        return MsgUtil.fail("参数格式错误");
    }


    /**
     *  下排产 （单个拆单） 单层
     *  -- 注意：这个查询只显示了有子项的物料，而没有显示没有子项的物料
     *  {
     *   "params": {
     *     "ids": ["449961488933900288"]
     *   }
     * }
     */
    @PostMapping("/start_scheduled02")
    @Operation(summary = "订单排程/恢复 ")
    public BaseResponse<String> startScheduled02(@RequestBody Map<String, Object> body) {
        Object paramObj = body.get("params");

        if (paramObj instanceof Map) {
            Map<?, ?> params = (Map<?, ?>) paramObj;
            Object idsObj = params.get("ids");

            if (idsObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> ids = (List<String>) idsObj;

                // 调用你的 service 方法
                // 默认只处理第一个 ID（支持单个拆单）
                String orderDtlId = ids.get(0);
                iservice.startSchedule06(orderDtlId);
                return MsgUtil.ok("拆单成功");

            }
        }

        return MsgUtil.fail("参数错误");
    }





    /**
     *  下排产 （批量）
     *  {
     *   "params": {
     *     "ids": ["449961488933900288"]
     *   }
     * }
     */
    @PostMapping("/start_scheduled01")
    @Operation(summary = "订单排程/恢复 ")
    public BaseResponse<String> startScheduled01(@RequestBody Map<String, Object> body) {
        Object paramObj = body.get("params");

        if (paramObj instanceof Map) {
            Map<?, ?> params = (Map<?, ?>) paramObj;
            Object idsObj = params.get("ids");

            if (idsObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> ids = (List<String>) idsObj;

                // 调用你的 service 方法
                service.startSchedule01(ids);

                return MsgUtil.ok();
            }
        }

        return MsgUtil.fail("参数错误");
    }



//    @PostMapping("/start_scheduled")
//    @Operation(summary = "订单排程/恢复 ")
//    public BaseResponse<String> startScheduled(@RequestBody @Valid BaseRequest<ComIdsReq> request) {
//        service.startSchedule(MsgUtil.params(request).getIds());
//        return MsgUtil.ok();
//    }



    /**
     * 分页查询排产信息
     *
     * @param request 查询参数
     * @return 分页记录
     */
    @PostMapping("/page_list")
    @Operation(summary = "查询生产任务单分页列表")
    public BaseResponse<List<ProductionOrderPageResponse>> pageList(@RequestBody @Valid BaseRequest<ProductionOrderPageRequest> request) {

        return service.pageList(request);
    }
}
