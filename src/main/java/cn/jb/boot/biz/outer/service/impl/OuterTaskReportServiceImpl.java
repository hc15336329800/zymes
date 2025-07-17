package cn.jb.boot.biz.outer.service.impl;

import cn.jb.boot.biz.order.manager.ProcAllocationManager;
import cn.jb.boot.biz.outer.entity.OuterTask;
import cn.jb.boot.biz.outer.entity.OuterTaskReport;
import cn.jb.boot.biz.outer.mapper.OuterTaskMapper;
import cn.jb.boot.biz.outer.mapper.OuterTaskReportMapper;
import cn.jb.boot.biz.outer.service.OuterTaskReportService;
import cn.jb.boot.biz.outer.vo.request.OuterTaskReportCreateRequest;
import cn.jb.boot.biz.outer.vo.request.OuterTaskReportPageRequest;
import cn.jb.boot.biz.order.model.ProcAllocationReport;
import cn.jb.boot.biz.outer.vo.response.OuterTaskReportPageResponse;
import cn.jb.boot.biz.work.vo.request.ReportUpdateStatusRequest;
import cn.jb.boot.framework.com.Constants;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.exception.CavException;
import cn.jb.boot.util.ArithUtil;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import cn.jb.boot.util.ThreadPool;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 外协任务报工 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-10 10:44:22
 */
@Service
public class OuterTaskReportServiceImpl extends ServiceImpl<OuterTaskReportMapper, OuterTaskReport> implements OuterTaskReportService {

    @Resource
    private OuterTaskReportMapper mapper;
    @Resource
    private OuterTaskMapper outerTaskMapper;
    @Resource
    private ProcAllocationManager procAllocationManager;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void createInfo(OuterTaskReportCreateRequest params) {
        OuterTask ot = outerTaskMapper.selectById(params.getTaskId());
        ot.setWaitRealCount(ArithUtil.add(ot.getWaitRealCount(), params.getRealCount()));
        ot.setWaitDeffCount(ArithUtil.add(ot.getWaitDeffCount(), params.getDeffCount()));
        BigDecimal sub =
                ArithUtil.add(ot.getRealCount(), ot.getWaitRealCount(), ot.getWaitDeffCount()).subtract(ot.getDeffCount());
        if (sub.compareTo(ot.getOuterCount()) > 0) {
            throw new CavException("报工数量超过分配数量");
        }
        OuterTaskReport entity = PojoUtil.copyBean(params, OuterTaskReport.class);
        entity.setReviewStatus(Constants.STATUS_00);
        outerTaskMapper.updateById(ot);
        this.save(entity);
    }


    @Override
    public BaseResponse<List<OuterTaskReportPageResponse>> pageInfo(Paging page, OuterTaskReportPageRequest params) {
        PageUtil<OuterTaskReportPageResponse, OuterTaskReportPageRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateStatus(ReportUpdateStatusRequest params) {
        List<String> ids = params.getIds();
        List<OuterTaskReport> taskReports = this.listByIds(ids);
        List<OuterTaskReport> upList = new ArrayList<>();
        for (OuterTaskReport tr : taskReports) {
            if (Constants.STATUS_00.equals(tr.getReviewStatus())) {
                tr.setReviewStatus(params.getStatus());
                tr.setReviewDesc(params.getMessage());
                tr.setReviewTime(LocalDateTime.now());
                upList.add(tr);
            }
        }
        if (CollectionUtils.isNotEmpty(upList)) {
            this.updateBatchById(upList);
            List<String> tids = upList.stream().map(OuterTaskReport::getTaskId).collect(Collectors.toList());
            List<OuterTask> outerTasks = outerTaskMapper.selectBatchIds(tids);
            Map<String, OuterTask> map = outerTasks.stream().collect(Collectors.toMap(OuterTask::getId,
                    Function.identity()));
            List<ProcAllocationReport> requests = new ArrayList<>();
            for (OuterTaskReport tr : upList) {
                OuterTask ot = map.get(tr.getTaskId());
                if (Constants.STATUS_01.equals(tr.getReviewStatus())) {
                    ProcAllocationReport otr = new ProcAllocationReport(ot.getAllocId(), tr.getRealCount());
                    requests.add(otr);
                }
                ot.setRealCount(ArithUtil.add(ot.getRealCount(), tr.getRealCount()));
                ot.setDeffCount(ArithUtil.add(ot.getDeffCount(), tr.getDeffCount()));
                ot.setWaitDeffCount(ArithUtil.sub(ot.getWaitDeffCount(), tr.getDeffCount()));
                ot.setWaitRealCount(ArithUtil.sub(ot.getRealCount(), tr.getRealCount()));
                outerTaskMapper.updateById(ot);
            }
            if (CollectionUtils.isNotEmpty(requests)) {
                ThreadPool.singleExecute(() -> {
                    procAllocationManager.updateOuterReport(requests);
                });
            }
        }
    }

}
