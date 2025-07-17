package cn.jb.boot.biz.item.mapper;

import cn.jb.boot.biz.item.entity.MesErpItemLog;
import cn.jb.boot.biz.item.vo.request.MesErpItemLogPageRequest;
import cn.jb.boot.biz.item.vo.response.MesErpItemLogPageResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * mes与ERP物料出入库流水表 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 16:55:58
 */
public interface MesErpItemLogMapper extends BaseMapper<MesErpItemLog> {

    /**
     * 根据条件分页查询mes与ERP物料出入库流水表列表
     *
     * @param params mes与ERP物料出入库流水表信息
     * @return mes与ERP物料出入库流水表信息集合信息
     */
    IPage<MesErpItemLogPageResponse> pageInfo(Page<MesErpItemLogPageResponse> page, @Param("p") MesErpItemLogPageRequest params);
}
