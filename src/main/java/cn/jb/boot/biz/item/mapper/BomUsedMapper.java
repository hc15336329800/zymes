package cn.jb.boot.biz.item.mapper;

import cn.jb.boot.biz.item.dto.UseItemTreeRow;
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


	// 扁平树V1: 用于接收递归 CTE 查询结果  (一次查询 + 内存组装)
//	List<UseItemTreeRow> treeAll(@io.lettuce.core.dynamic.annotation.Param("itemNo") String itemNo);


	/**
	 * 扁平树 V2 : 拉取整棵递归树（含防环逻辑）
	 * @param itemNo 根物料编码
	 */
	List<UseItemTreeRow> treeAll(@Param("itemNo") String itemNo);




	/**
	 * bom用料依赖 Mapper 接口
	 *   取指定父件的一阶用料
	 */
		List<BomUsed> selectByItemNo(@Param("itemNos") List<String> itemNos);

}
