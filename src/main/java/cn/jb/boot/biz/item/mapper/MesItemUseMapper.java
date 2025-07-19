package cn.jb.boot.biz.item.mapper;

import cn.jb.boot.biz.item.dto.UseItemTreeRow;
import cn.jb.boot.biz.item.entity.BomUsed;
import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.entity.MesItemUse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

/**
 * 产品用料表 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 16:55:58
 */
public interface MesItemUseMapper extends BaseMapper<MesItemUse> {





    /**
     * 查询在指定时间之后变更过的用料记录（仅返回 item_no，作为构建入口）
     *
     * @param startTime 更新时间起点（含）
     * @return 最近变更的 item_no 列表（不重复）
     */
    List<String> selectNearItemNo(@Param("startTime") String startTime);

    /**
     * 根据条件分页查询产品用料表列表
     * @return 产品用料表信息集合信息
     */

    List<MesItemStock> selectUses(String itemNo);
}
