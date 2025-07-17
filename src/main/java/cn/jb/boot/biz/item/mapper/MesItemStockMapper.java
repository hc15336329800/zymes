package cn.jb.boot.biz.item.mapper;

import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.entity.MesItemUse;
import cn.jb.boot.biz.item.entity.MesProcedure;
import cn.jb.boot.biz.item.vo.request.BomNoSelectedRequest;
import cn.jb.boot.biz.item.vo.request.BomPageRequest;
import cn.jb.boot.biz.item.vo.request.ItemNoSelectedRequest;
import cn.jb.boot.biz.item.vo.request.MesItemStockPageRequest;
import cn.jb.boot.biz.item.vo.response.ItemSelectedResponse;
import cn.jb.boot.biz.item.vo.response.MesItemStockPageResponse;
import cn.jb.boot.system.vo.DictDataVo;
import cn.jb.boot.system.vo.response.DictListResponse;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 产品库存表 Mapper 接口
 *
 * @author user
 * @Description
 * @Copyright Copyright (c) 2022
 * @since 2022-08-03 13:24:13
 */
public interface MesItemStockMapper extends BaseMapper<MesItemStock> {


    IPage<MesItemStockPageResponse> pageInfo(Page<MesItemStockPageResponse> page, @Param("p") MesItemStockPageRequest p);

    @MapKey("bomNo")
    Map<String, MesItemStock> getByBomNos(@Param("boms") List<String> boms);

    @MapKey("itemNo")
    Map<String, MesItemStock> getByItemNos(@Param("itemNos") List<String> itemNos);

    List<DictDataVo> getCache();


    List<ItemSelectedResponse> itemNoSelected(@Param("p") ItemNoSelectedRequest request);

    List<ItemSelectedResponse> listBomByNo(@Param("p") BomNoSelectedRequest params);

    IPage<MesItemStockPageResponse> bomPageList(Page<MesItemStockPageResponse> page, @Param("p") BomPageRequest params);

    int insertItemStock(Map<String,Object> itemStock);

    int insertBomUsed(MesItemUse itemStock);

    int insertMesProcedure(MesProcedure itemStock);

    @InterceptorIgnore(blockAttack = "true")
    int deleteBomData();

    List<MesItemStock> selectBoms(@Param("startTime") String startTime);

}
