package cn.jb.boot.biz.work.mapper;

import cn.jb.boot.biz.work.entity.WorkOrderRecord;
import cn.jb.boot.biz.work.vo.request.WorkOrderRecordPageRequest;
import cn.jb.boot.biz.work.vo.response.WorkOrderRecordPageResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 工序分配记录 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-08 18:34:33
 */
public interface WorkOrderRecordMapper extends BaseMapper<WorkOrderRecord> {

    /**
     * 根据条件分页查询工序分配记录列表
     *
     * @param params 工序分配记录信息
     * @return 工序分配记录信息集合信息
     */
    IPage<WorkOrderRecordPageResponse> pageInfo(Page<WorkOrderRecordPageResponse> page, @Param("p") WorkOrderRecordPageRequest params);
}
