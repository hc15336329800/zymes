package cn.jb.boot.biz.work.service;

import cn.jb.boot.biz.work.entity.WorkAssign;
import cn.jb.boot.biz.work.vo.request.WorkAssignPageRequest;
import cn.jb.boot.biz.work.vo.response.WorkAssignPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 工单下达表 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-09 14:06:38
 */
public interface WorkAssignService extends IService<WorkAssign> {


    /**
     * 分页查询
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<WorkAssignPageResponse>> pageInfo(Paging page, WorkAssignPageRequest params);
}
