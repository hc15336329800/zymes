package cn.jb.boot.biz.work.service;

import cn.jb.boot.biz.work.entity.WorkOrderRecord;
import cn.jb.boot.biz.work.vo.request.WorkOrderRecordPageRequest;
import cn.jb.boot.biz.work.vo.response.WorkOrderRecordPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 工序分配记录 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-08 18:34:33
 */
public interface WorkOrderRecordService extends IService<WorkOrderRecord> {


    /**
     * 分页查询
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<WorkOrderRecordPageResponse>> pageInfo(Paging page, WorkOrderRecordPageRequest params);
}
