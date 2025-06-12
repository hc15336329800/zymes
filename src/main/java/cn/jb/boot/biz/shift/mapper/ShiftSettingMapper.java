package cn.jb.boot.biz.shift.mapper;

import cn.jb.boot.biz.shift.entity.ShiftSetting;
import cn.jb.boot.biz.shift.vo.request.ShiftSettingPageRequest;
import cn.jb.boot.biz.shift.vo.response.ShiftSettingPageResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 班次设定表 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-07 14:26:37
 */
public interface ShiftSettingMapper extends BaseMapper<ShiftSetting> {

    /**
     * 根据条件分页查询班次设定表列表
     *
     * @param params 班次设定表信息
     * @return 班次设定表信息集合信息
     */
    IPage<ShiftSettingPageResponse> pageInfo(Page<ShiftSettingPageResponse> page, @Param("p") ShiftSettingPageRequest params);
}
