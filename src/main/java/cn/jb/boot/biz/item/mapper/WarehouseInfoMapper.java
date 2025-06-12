package cn.jb.boot.biz.item.mapper;

import cn.jb.boot.biz.item.entity.WarehouseInfo;
import cn.jb.boot.biz.item.vo.request.WarehouseInfoPageRequest;
import cn.jb.boot.biz.item.vo.response.WarehouseInfoPageResponse;
import cn.jb.boot.system.vo.DictDataVo;
import cn.jb.boot.system.vo.response.DictListResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 库位信息 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-02 00:44:38
 */
public interface WarehouseInfoMapper extends BaseMapper<WarehouseInfo> {

    /**
     * 根据条件分页查询库位信息列表
     *
     * @param params 库位信息信息
     * @return 库位信息信息集合信息
     */
    IPage<WarehouseInfoPageResponse> pageInfo(Page<WarehouseInfoPageResponse> page, @Param("p") WarehouseInfoPageRequest params);

    List<DictDataVo> selected();
}
