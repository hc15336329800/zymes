package cn.jb.boot.biz.item.service;

import cn.jb.boot.biz.item.entity.BomUsed;
import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.vo.response.UseItemTreeResp;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * bom用料依赖 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 16:55:58
 */
public interface BomUsedService extends IService<BomUsed> {


    Map<String, List<BomUsed>> getBomDepend(List<String> itemNos);

    void load(String startTime);


    void loadByItem(String itemNo, String bomNo);

    /**
     * 加载bom的数据
     *
     * @param stock
     */
    void loadBomData(MesItemStock stock);

    void loadParBomData(String itemNo, List<String> itList);

    List<BomUsed> tree(String itemNo);
}
