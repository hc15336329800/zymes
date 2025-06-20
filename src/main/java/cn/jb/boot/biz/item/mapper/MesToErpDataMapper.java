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

	// 查询未同步的ERP物料
	List<Map<String, Object>> materialMessage();

	// 批量回写ERP同步标记
	void materUpdate(@Param("itemNoList") List<String> itemNoList);
}
