package cn.jb.boot.biz.work.service.impl;

import cn.jb.boot.biz.work.entity.WorkOrderRecord;
import cn.jb.boot.biz.work.mapper.WorkOrderRecordMapper;
import cn.jb.boot.biz.work.service.WorkOrderRecordService;
import cn.jb.boot.biz.work.vo.request.WorkOrderRecordPageRequest;
import cn.jb.boot.biz.work.vo.response.WorkOrderRecordPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.util.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 工序分配记录 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-08 18:34:33
 */
@Service
public class WorkOrderRecordServiceImpl extends ServiceImpl<WorkOrderRecordMapper, WorkOrderRecord> implements WorkOrderRecordService {

    @Resource
    private WorkOrderRecordMapper mapper;


    @Override
    public BaseResponse<List<WorkOrderRecordPageResponse>> pageInfo(Paging page, WorkOrderRecordPageRequest params) {
        PageUtil<WorkOrderRecordPageResponse, WorkOrderRecordPageRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }
}
