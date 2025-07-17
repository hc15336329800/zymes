package cn.jb.boot.biz.item.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
@DS("db3") // 如果只想这个 Mapper 里所有方法都用 Oracle 数据源
public interface MesToErpDataMapper {

	// 推荐用 @Select 注解， 单独测试数据源是否成功
	@Select("SELECT * FROM JSPMATERIAL WHERE ROWNUM <= 10") // Oracle 测试SQL
	List<Map<String, Object>> materialMessageCS();


	//===============================物料==================================

	// 查询未同步的ERP物料
	List<Map<String, Object>> materialMessage();

	// 批量回写ERP同步标记
	int materUpdate(@Param("itemNoList") List<String> itemNoList);


	//===============================bom树==================================


	// 查询ERP中的BOM用料树（JSPBOM）
	List<Map<String, Object>> bomMessage();

	// 批量回写ERP同步标记
	void bomUpdate(@Param("idList") List<Integer> idList);

	//===============================工序==================================

	/** 拉取ERP工序表未同步（BYTSTATUS=0）的全部数据 */
	List<Map<String, Object>> bomRouter();

	/** 根据工序ID批量回写同步状态为已同步 */
	int routerUpdate(@Param("routerIdList") List<Integer> routerIdList);



}
