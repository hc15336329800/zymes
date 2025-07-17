package cn.jb.boot.biz.order.mapper;

import cn.jb.boot.biz.order.entity.OrderDtl;
import cn.jb.boot.biz.order.vo.request.OrderDtlPageRequest;
import cn.jb.boot.biz.order.vo.response.OrderDtlPageResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;



/**
 * 订单明细表 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-05 19:00:38
 */
public interface OrderDtlMapper extends BaseMapper<OrderDtl> {


   // 查询t_order_dtl明细   -
   IPage<OrderDtlPageResponse> selectDtlWithItemPage(Page<?> page, @Param("p") OrderDtlPageRequest dto);




    // -----------------------------------------------------------查询t_order_dtl明细   订单分组-------------------------------------------------------

    // ① 统计组数（Distinct order_no）
    long countGroupByOrderNo(OrderDtlPageRequest dto);

    // ② 取得当前页应显示的 order_no 列表（组级查询）
    List<String> selectPageOrderNos(@Param("dto") OrderDtlPageRequest dto,
                                    @Param("offset") long offset,
                                    @Param("size") long size);

    // ③ 取整组明细
    List<OrderDtlPageResponse> selectDetailsByOrderNos(@Param("orderNos") List<String> orderNos);


//使用树替代组
 List<OrderDtlPageResponse> selectDtlWithItemAndTopBomByOrderNos(
         @Param("orderNos") List<String> orderNos,
         @Param("dto") OrderDtlPageRequest dto
 );

    // -------------------------------------------------------------------------------------------------------------



    /**
     * 根据条件分页查询订单明细表列表
     *
     * @param params 订单明细表信息
     * @return 订单明细表信息集合信息
     */
    IPage<OrderDtlPageResponse> pageInfo(Page<OrderDtlPageResponse> page, @Param("p") OrderDtlPageRequest params);

    List<OrderDtl> selectCurrentMonth(@Param("itemNos") List<String> itemNos);

    Long maxOrderNo();

    List<Map<String,Object>> getProcTodayDatas(@Param("date") String date);

}
