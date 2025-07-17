package cn.jb.boot.biz.outer.service;

import cn.jb.boot.biz.outer.entity.OuterTask;
import cn.jb.boot.biz.outer.vo.request.OuterTaskCreateRequest;
import cn.jb.boot.biz.outer.vo.request.OuterTaskPageRequest;
import cn.jb.boot.biz.outer.vo.request.OuterTaskUpdateRequest;
import cn.jb.boot.biz.outer.vo.response.OuterTaskInfoResponse;
import cn.jb.boot.biz.outer.vo.response.OuterTaskPageResponse;
import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 外协任务 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-10 10:44:22
 */
public interface OuterTaskService extends IService<OuterTask> {

    /**
     * 新增外协任务
     *
     * @param params 外协任务
     */
    void createInfo(OuterTaskCreateRequest params);


    /**
     * 分页查询
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<OuterTaskPageResponse>> pageInfo(Paging page, OuterTaskPageRequest params);

    List<OuterTaskInfoResponse> listByPid(String id);

    void accept(ComId params);
}
