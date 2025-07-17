package cn.jb.boot.biz.test.mapper;


import cn.jb.boot.system.entity.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;


//MyBatis‑Plus方式，不在写xml和接口


@Mapper
public interface TestMapper extends BaseMapper<UserInfo> {
	// 不需要再写任何方法，BaseMapper 已包含 selectPage()
}
