package cn.jb.boot.biz.agvcar.mapper;

import cn.jb.boot.biz.agvcar.entity.AgvManageInfo;
import cn.jb.boot.biz.agvcar.vo.request.AgvManageInfoPageRequest;
import cn.jb.boot.biz.agvcar.vo.response.AgvManageInfoPageResponse;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 点检信息 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-01 20:04:08
 */
@DS("db3")
@Mapper
public interface BomManageInfoMapper extends BaseMapper<AgvManageInfo> {

    void routerUpdate(@Param("routerIdList") List<Integer> routerIdList);
    void bomUpdate(@Param("idList") List<Integer> idList);

    void materUpdate(@Param("itemNoList") List<String> itemNoList);

    List<Map<String, Object>> materialMessage();

    List<Map<String, Object>> receiptMessage();

    List<Map<String, Object>> bomMessage();

    List<Map<String, Object>> bomRouter();
}
