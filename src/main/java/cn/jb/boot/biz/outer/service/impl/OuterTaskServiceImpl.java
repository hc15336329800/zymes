package cn.jb.boot.biz.outer.service.impl;

import cn.jb.boot.biz.depository.manager.OutStoreManager;
import cn.jb.boot.biz.order.entity.ProcAllocation;
import cn.jb.boot.biz.order.service.ProcAllocationService;
import cn.jb.boot.biz.outer.entity.OuterTask;
import cn.jb.boot.biz.outer.mapper.OuterTaskMapper;
import cn.jb.boot.biz.outer.service.OuterTaskService;
import cn.jb.boot.biz.outer.vo.request.OuterTaskCreateRequest;
import cn.jb.boot.biz.outer.vo.request.OuterTaskDtlRequest;
import cn.jb.boot.biz.outer.vo.request.OuterTaskPageRequest;
import cn.jb.boot.biz.outer.vo.request.OuterTaskUpdateRequest;
import cn.jb.boot.biz.outer.vo.response.OuterTaskInfoResponse;
import cn.jb.boot.biz.outer.vo.response.OuterTaskPageResponse;
import cn.jb.boot.framework.com.Constants;
import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.config.SpringContextUtil;
import cn.jb.boot.framework.exception.CavException;
import cn.jb.boot.util.ArithUtil;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import cn.jb.boot.util.ThreadPool;
import cn.jb.boot.util.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 外协任务 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-10 10:44:22
 */
@Service
public class OuterTaskServiceImpl extends ServiceImpl<OuterTaskMapper, OuterTask> implements OuterTaskService {

    @Resource
    private OuterTaskMapper mapper;
    @Resource
    private ProcAllocationService procAllocationService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void createInfo(OuterTaskCreateRequest params) {
        List<OuterTaskDtlRequest> list = params.getList();
        List<OuterTask> outerTasks = PojoUtil.copyList(list, OuterTask.class);
        outerTasks.forEach(d -> d.setAllocId(params.getId()));
        this.saveOrUpdateBatch(outerTasks);
        List<BigDecimal> collect = outerTasks.stream().map(OuterTask::getOuterCount).collect(Collectors.toList());
        BigDecimal outCount = ArithUtil.add(collect);
        ProcAllocation pa = procAllocationService.getById(params.getId());
        pa.setOuterPubCount(outCount);
        if (outCount.compareTo(pa.getOuterAllocCount()) > 0) {
            throw new CavException("分配数量超过外协数量");
        }
        procAllocationService.updateById(pa);
    }


    @Override
    public BaseResponse<List<OuterTaskPageResponse>> pageInfo(Paging page, OuterTaskPageRequest params) {
        PageUtil<OuterTaskPageResponse, OuterTaskPageRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }

    @Override
    public List<OuterTaskInfoResponse> listByPid(String id) {
        List<OuterTask> list = this.list(new LambdaQueryWrapper<OuterTask>().eq(OuterTask::getAllocId, id));
        return PojoUtil.copyList(list, OuterTaskInfoResponse.class);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void accept(ComId params) {
        String id = params.getId();
        OuterTask ot = this.getById(id);
        ot.setAcceptStatus(Constants.STATUS_01);
        this.updateById(ot);
        ProcAllocation pa = procAllocationService.getById(ot.getAllocId());
        String uid = UserUtil.uid();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                ThreadPool.commonExecute(() -> SpringContextUtil.getBeanByClass(OutStoreManager.class)
                        .createOutStore(ot.getOuterCount(), pa.getItemNo(), pa.getProcedureCode(), ot.getId(), Constants.STATUS_02, uid));
            }
        });
    }
}
