package cn.jb.boot.biz.item.service;

import cn.jb.boot.biz.item.entity.MesItemUse;
import cn.jb.boot.biz.item.vo.request.ItemNoRequest;
import cn.jb.boot.biz.item.vo.request.MesItemUseCreateRequest;
import cn.jb.boot.biz.item.vo.request.MesItemUseUpdateRequest;
import cn.jb.boot.biz.item.vo.response.ItemResp;
import cn.jb.boot.biz.item.vo.response.UseItemTreeResp;
import cn.jb.boot.biz.item.vo.response.MesItemUseInfoResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

/**
 * 产品用料表 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 16:55:58
 */
public interface MesItemUseService extends IService<MesItemUse> {


    void useDateFromErp(Set<String> items);

    /** bom树构造器  （  将 List<BomUsed> list  + String root   构建为前端树结构） */
    UseItemTreeResp itemUseTreeNew(ItemNoRequest params);

    void export(HttpServletResponse response, String id);


    UseItemTreeResp itemUseTree(ItemNoRequest params);

    void upload(HttpServletRequest request);

    void saveOrUpdateItem(MesItemUseCreateRequest params);

    List<ItemResp> itemList(ItemNoRequest params);

    MesItemUseInfoResponse getInfoById(String id);


    void delete(String id);

}
