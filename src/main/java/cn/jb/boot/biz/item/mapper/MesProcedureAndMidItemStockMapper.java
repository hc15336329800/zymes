package cn.jb.boot.biz.item.mapper;


//-- 物料同步 V1.0
//
//		-- ✅ 差集筛选（left join + null 过滤）：找到“存在于 A 表，不存在于 B 表”的记录；
//
//		-- ✅ 补充插入：把缺的记录补充进中间表，实现数据对齐。


import cn.jb.boot.biz.item.entity.MesProcedure;
import cn.jb.boot.biz.item.entity.MidItemStock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MesProcedureAndMidItemStockMapper {
	List<MesProcedure> getMissingProcedures();

	int insertBatchToMid(@Param("list") List<MidItemStock> list);
}
