package cn.jb.boot.biz.work.service.impl;

import cn.jb.boot.biz.work.entity.WorkAssign;
import cn.jb.boot.biz.work.mapper.WorkAssignMapper;
import cn.jb.boot.biz.work.service.WorkAssignService;
import cn.jb.boot.biz.work.vo.request.WorkAssignPageRequest;
import cn.jb.boot.biz.work.vo.response.WorkAssignPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.util.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 工单下达表 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-09 14:06:38
 */
@Service
public class WorkAssignServiceImpl extends ServiceImpl<WorkAssignMapper, WorkAssign> implements WorkAssignService {

    @Resource
    private WorkAssignMapper mapper;


    @Override
    public BaseResponse<List<WorkAssignPageResponse>> pageInfo(Paging page, WorkAssignPageRequest params) {
        PageUtil<WorkAssignPageResponse, WorkAssignPageRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }
}
