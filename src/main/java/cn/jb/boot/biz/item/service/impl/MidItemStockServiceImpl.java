package cn.jb.boot.biz.item.service.impl;

import cn.jb.boot.biz.item.entity.MesProcedure;
import cn.jb.boot.biz.item.entity.MidItemStock;
import cn.jb.boot.biz.item.mapper.MidItemStockMapper;
import cn.jb.boot.biz.item.service.MidItemStockService;
import cn.jb.boot.biz.item.vo.request.MidItemStockPageRequest;
import cn.jb.boot.biz.item.vo.response.MidItemStockPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.util.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * mes中间件库存表 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-03 17:28:52
 */
@Service
public class MidItemStockServiceImpl extends ServiceImpl<MidItemStockMapper, MidItemStock> implements MidItemStockService {

    @Resource
    private MidItemStockMapper mapper;


    @Override
    public BaseResponse<List<MidItemStockPageResponse>> pageInfo(Paging page, MidItemStockPageRequest params) {
        PageUtil<MidItemStockPageResponse, MidItemStockPageRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }

    @Override
    public List<MesProcedure> getMissingMid() {
        return mapper.getMissingMid();
    }

//    @Override
//    public Map<String, MidItemStock> selectStock(List<String> itemNos) {
//
//        List<MidItemStock> mids = mapper.selectStock(itemNos);
//        return mids.stream().collect(Collectors.toMap(MidItemStock::getItemNo, Function.identity()));
//    }


    @Override
    public Map<String, MidItemStock> selectStock(List<String> itemNos) {
        // 1. 走 Mapper 拿到 List
        List<MidItemStock> list = this.baseMapper.selectStock(itemNos);
        // 2. 转成 Map<itemNo, MidItemStock>
        return list.stream()
                .collect(Collectors.toMap(
                        MidItemStock::getItemNo,
                        Function.identity(),
                        (a,b) -> a  // 重复取第一个
                ));
    }

}
