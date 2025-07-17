package cn.jb.boot.biz.order.manager.impl;

import cn.jb.boot.biz.group.entity.GroupDtl;
import cn.jb.boot.biz.group.entity.GroupInfo;
import cn.jb.boot.biz.group.enums.LeaderType;
import cn.jb.boot.biz.group.service.GroupDtlService;
import cn.jb.boot.biz.group.service.GroupInfoService;
import cn.jb.boot.biz.order.entity.ProcAllocation;
import cn.jb.boot.biz.order.manager.ProcAllocationManager;
import cn.jb.boot.biz.order.manager.OrderDtlManager;
import cn.jb.boot.biz.order.model.OrderDtlReport;
import cn.jb.boot.biz.order.service.ProcAllocationService;
import cn.jb.boot.biz.order.model.ProcAllocationReport;
import cn.jb.boot.biz.work.entity.WorkReport;
import cn.jb.boot.biz.work.entity.WorkerReportDtl;
import cn.jb.boot.biz.work.enums.ReportTypeEnum;
import cn.jb.boot.biz.work.service.WorkerReportDtlService;
import cn.jb.boot.framework.com.Constants;
import cn.jb.boot.util.ArithUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ProcAllocationManagerImpl implements ProcAllocationManager {
    @Resource
    private ProcAllocationService procAllocationService;
    @Resource
    private GroupInfoService groupInfoService;
    @Resource
    private GroupDtlService groupDtlService;
    @Resource
    private WorkerReportDtlService workerReportDtlService;
    @Resource
    private OrderDtlManager orderDtlManager;

    private final BigDecimal RATIO = BigDecimal.valueOf(0.01);
    private static ThreadPoolExecutor itemCountThread = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(100000),
            new BasicThreadFactory.Builder().namingPattern("item-count-%d")
                    .daemon(true).build(), new ThreadPoolExecutor.CallerRunsPolicy());


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateOuterReport(List<ProcAllocationReport> requests) {
        updateProcAllocationCount(requests, Constants.STATUS_01);

    }

    public void updateProcAllocationCount(List<ProcAllocationReport> requests, String type) {
        List<String> pids = requests.stream().map(ProcAllocationReport::getPid).collect(Collectors.toList());
        List<ProcAllocation> pas = procAllocationService.listByIds(pids);
        Map<String, ProcAllocation> map = pas.stream().collect(Collectors.toMap(ProcAllocation::getId, Function.identity()));
        List<OrderDtlReport> dtls = new ArrayList<>();
        for (ProcAllocationReport request : requests) {
            ProcAllocation pa = map.get(request.getPid());
            OrderDtlReport dtl = new OrderDtlReport(pa.getOrderDtlId(), request.getCount(), pa.getProcedureCode());
            if (Constants.STATUS_01.equals(type)) {
                pa.setOuterProdCount(ArithUtil.add(pa.getOuterAllocCount(), request.getCount()));
            } else {
                pa.setProdCount(ArithUtil.add(pa.getProdCount(), request.getCount()));
            }
            dtls.add(dtl);
        }
        procAllocationService.updateBatchById(pas);
        itemCountThread.execute(() -> {
            orderDtlManager.report(dtls);
        });
    }


    //更新工序分配产量 +生成工资表 等等
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateWorkReport(List<WorkReport> list) {
        List<ProcAllocationReport> dataList = list.stream().map(d -> {
            ProcAllocationReport p = new ProcAllocationReport();
            p.setPid(d.getPid());
            p.setCount(d.getRealCount());
            return p;
        }).collect(Collectors.toList());
        updateProcAllocationCount(dataList, Constants.STATUS_02);
        for (WorkReport wr : list) {
            //生成工资表
            List<WorkerReportDtl> dtls = computeByGroupDtl(wr.getReportType(), wr.getRealCount(), wr.getGroupId());
            dtls.forEach(d -> {
                d.setReportId(wr.getId());
                d.setReportType(wr.getReportType());
                d.setWorkOrderId(wr.getWorkOrderId());
            });
            workerReportDtlService.saveBatch(dtls);
        }
    }


    private List<WorkerReportDtl> computeByGroupDtl(String reportType, BigDecimal reportCount, String groupId) {
        GroupInfo gi = groupInfoService.getById(groupId);
        List<GroupDtl> dtls = groupDtlService.list(new LambdaQueryWrapper<GroupDtl>().eq(GroupDtl::getGroupId, groupId));
        if (CollectionUtils.isNotEmpty(dtls)) {
            BigDecimal total = ArithUtil.add(dtls.stream().map(GroupDtl::getPercentage).toArray(BigDecimal[]::new));
            //按件数分
            if (ReportTypeEnum.PERSON_NUMBER.getCode().equals(reportType)) {
                for (int i = dtls.size() - 1; i >= 0; i--) {
                    GroupDtl dtl = dtls.get(i);
                    BigDecimal count = ArithUtil.mul(RATIO, dtl.getPercentage(), reportCount);
                    BigDecimal curCount = count.setScale(5, RoundingMode.DOWN);
                    dtl.setUserCount(curCount);
                }
            } else {
                //合工 40(总件数) /（9*1+7*0.65+7*0.85）
                //单位件数 合工保留小数点后2位
                BigDecimal singleCount = reportCount.divide(total, 5, RoundingMode.HALF_DOWN);
                BigDecimal less = reportCount;
                for (GroupDtl dtl : dtls) {
                    //组员的记件
                    if (LeaderType.WORKER.getCode().equals(dtl.getLeaderType())) {
                        BigDecimal personCount = ArithUtil.mul(singleCount, dtl.getPercentage()).setScale(5, RoundingMode.DOWN);
                        less = less.subtract(personCount);
                        dtl.setUserCount(personCount);
                    }

                }
                Optional<GroupDtl> any = dtls.stream().filter(d -> LeaderType.LEADER.getCode().equals(d.getLeaderType())).findAny();
                if (any.isPresent()) {
                    GroupDtl dtl = any.get();
                    dtl.setUserCount(less);
                }
            }
        }
        List<WorkerReportDtl> list = dtls.stream().filter(d -> BigDecimal.ZERO.compareTo(d.getUserCount()) < 0).map(d -> {
            WorkerReportDtl p = new WorkerReportDtl();
            p.setGroupId(d.getGroupId());
            p.setUserId(d.getUserId());
            p.setReportCount(reportCount);
            p.setGroupUid(gi.getGroupUid());
            p.setUserCount(d.getUserCount());
            return p;
        }).collect(Collectors.toList());
        return list;
    }

}
