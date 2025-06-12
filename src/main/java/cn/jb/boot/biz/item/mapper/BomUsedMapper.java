package cn.jb.boot.biz.item.mapper;

import cn.jb.boot.biz.item.entity.BomUsed;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * bom用料依赖 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 16:55:58
 */
public interface BomUsedMapper extends BaseMapper<BomUsed> {


	/**
	 * bom用料依赖 Mapper 接口
	 *   取指定父件的一阶用料
	 */
		List<BomUsed> selectByItemNo(@Param("itemNos") List<String> itemNos);

}
