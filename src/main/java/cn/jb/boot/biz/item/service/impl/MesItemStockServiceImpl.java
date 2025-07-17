package cn.jb.boot.biz.item.service.impl;

import cn.jb.boot.biz.item.entity.MidItemStock;
import cn.jb.boot.biz.item.enums.StockType;
import cn.jb.boot.biz.item.service.MidItemStockService;
import cn.jb.boot.biz.item.vo.request.BomNoSelectedRequest;
import cn.jb.boot.biz.item.vo.request.BomPageRequest;
import cn.jb.boot.biz.item.vo.request.ItemNoSelectedRequest;
import cn.jb.boot.biz.item.vo.response.ItemSelectedResponse;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.common.exception.ServiceException;
import cn.jb.boot.framework.enums.ValidEnum;
import cn.jb.boot.framework.exception.CavException;
import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.enums.ItemType;
import cn.jb.boot.biz.item.mapper.MesItemStockMapper;
import cn.jb.boot.biz.item.service.MesItemStockService;
import cn.jb.boot.biz.item.vo.request.MesItemStockCreateRequest;
import cn.jb.boot.biz.item.vo.request.MesItemStockPageRequest;
import cn.jb.boot.biz.item.vo.request.MesItemStockUpdateRequest;
import cn.jb.boot.biz.item.vo.request.MesItemUploadRequest;
import cn.jb.boot.biz.item.vo.response.MesItemStockInfoResponse;
import cn.jb.boot.biz.item.vo.response.MesItemStockPageResponse;
import cn.jb.boot.system.vo.DictDataVo;
import cn.jb.boot.util.DictUtil;
import cn.jb.boot.util.EasyExcelUtil;
import cn.jb.boot.util.FileUtil;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import cn.jb.boot.util.ThreadPool;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 产品库存表 服务实现类
 *
 * @author user
 * @Description
 * @Copyright Copyright (c) 2022
 * @since 2022-08-03 13:24:13
 */
@Service
@Slf4j
public class MesItemStockServiceImpl extends ServiceImpl<MesItemStockMapper, MesItemStock> implements MesItemStockService {

    @Resource
    MesItemStockMapper mapper;
    @Resource
    private MidItemStockService midItemStockService;

    @Override
    public void createInfo(MesItemStockCreateRequest params) {
        MesItemStock entity = PojoUtil.copyBean(params, MesItemStock.class);
        this.checkItem(params.getItemNo(), params.getBomNo(), null);
        this.save(entity);
    }

    ////////////////////////////////////////////////////////////////////////////新街口//////////////////////////////////////////////////////////////////////////////////

    /**
     * 新接口V3： 物料上传 Excel → 导入 mes_item_stock → 更新 mid_item_stock（所有在同一事务中）
     * 修复第7步
     */
    @Transactional(rollbackFor = Throwable.class)
    public void uploadNew(HttpServletRequest request) {
        // 1. 取文件 & 校验表头
        // 1. 取文件 & 读取字典（库位映射）
        MultipartFile file = FileUtil.getFile(request);

        // 模板格式检查：改用 Arrays.asList
        List<String> expected = Arrays.asList("图纸号", "物品编码", "数量");
        List<String> actual   = EasyExcelUtil.readHead(file);

        if (!actual.containsAll(expected)) {
            throw new ServiceException("导入失败：请使用最新模板，确保包含“图纸号”、“物品编码”、“数量”三列");
        }

        // 2. 读 Excel VO
        List<MesItemUploadRequest> list = EasyExcelUtil.importExcel(file, MesItemUploadRequest.class);
        if (CollectionUtils.isEmpty(list)) {
            throw new ServiceException("导入失败：文件中无数据");
        }

        // 3. 准备库位映射
        Map<String,String> locMap = DictUtil.getDictCache(DictType.WARE_HOUSE)
                .stream().collect(Collectors.toMap(d->d.getDictLabel(), d->d.getDictValue(), (a,b)->a));

        // 4. 从 VO 映射到 Entity，并做必要的字段处理
        //    4.1 去重
        Map<String, MesItemStock> unique = new LinkedHashMap<>();
        for (MesItemUploadRequest r : list) {
            String itemNo = StringUtils.trimToEmpty(r.getItemNo());
            if (itemNo.isEmpty()) continue;
            // 每次 put，会覆盖上一次，保留最后一条
            MesItemStock mis = new MesItemStock();
            mis.setItemNo(itemNo);
            mis.setItemName(r.getItemName());
            mis.setItemCount(r.getItemCount());
            mis.setItemMeasure(r.getItemMeasure());
            mis.setItemOrigin(r.getItemOrigin());
            mis.setItemModel(r.getItemModel());

            // —— 关键：根据有没有 bomNo 来设置 itemType 和 bomNo 字段 ——
            String bomNo = StringUtils.trimToEmpty(r.getBomNo());
            mis.setBomNo(bomNo);
            if (StringUtils.isNotBlank(bomNo)) {
                mis.setItemType("01");   // 半成品
            } else {
                mis.setItemType("00");   // 原料
            }

            // —— 库位映射 ——
            mis.setLocation(locMap.getOrDefault(r.getLocation(), ""));
            // （可选）其它默认字段：isValid, uniId, erpCount, netWeight, createdBy/time 等

            unique.put(itemNo, mis);
        }
        List<MesItemStock> toAdd = new ArrayList<>(unique.values());
        if (toAdd.isEmpty()) {
            throw new ServiceException("导入失败：无有效新物料");
        }

        // 5. 【改动点】先删旧：根据 itemNo 或 bomNo 删除
        List<String> itemNos = toAdd.stream()
                .map(MesItemStock::getItemNo)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        List<String> bomNos = toAdd.stream()
                .map(MesItemStock::getBomNo)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());

        if (!itemNos.isEmpty() || !bomNos.isEmpty()) {
            LambdaQueryWrapper<MesItemStock> delWrapper = new LambdaQueryWrapper<>();
            delWrapper.nested(w -> {
                // 满足 itemNo 条件
                if (!itemNos.isEmpty()) {
                    w.in(MesItemStock::getItemNo, itemNos);
                }
                // 或者满足 bomNo 条件
                if (!bomNos.isEmpty()) {
                    if (!itemNos.isEmpty()) {
                        w.or();
                    }
                    w.in(MesItemStock::getBomNo, bomNos);
                }
            });
            this.remove(delWrapper);
        }

        // 6. 写入
        this.saveBatch(toAdd);

        // 7. 批量更新 mid_item_stock 中间件库存（仅 BOM 类型）
        // todo:   后续修改  暂时不动 1、库存表逻辑不对！如果中间库存表有则导入会覆盖。 2、库存表重复则bom列表查出来也重复

        //    —— 先定义 bomItemNos 列表 ——
        List<String> bomItemNos = toAdd.stream()
                .filter(mis -> "01".equals(mis.getItemType()))
                .map(MesItemStock::getItemNo)
                .collect(Collectors.toList());

        if (!bomItemNos.isEmpty()) {
            // 拿到 Map<itemNo, MidItemStock>
            Map<String, MidItemStock> midMap = midItemStockService.selectStock(bomItemNos);
            for (MesItemStock mis : toAdd) {
                if (!"01".equals(mis.getItemType())) continue;
                MidItemStock mid = midMap.get(mis.getItemNo());
                if (mid == null) continue;
                mid.setInitialCount(mis.getItemCount());
                midItemStockService.updateById(mid);
           }
        }


    }


    /**
     * 新接口V2： 物料上传 Excel → 导入 mes_item_stock → 更新 mid_item_stock（所有在同一事务中）
     * 存在则删除重写    有bug
     */
    @Transactional(rollbackFor = Throwable.class)
    public void uploadNew2(HttpServletRequest request) {
        // 1. 取文件 & 校验表头
        // 1. 取文件 & 读取字典（库位映射）
        MultipartFile file = FileUtil.getFile(request);

        // 模板格式检查：改用 Arrays.asList
        List<String> expected = Arrays.asList("图纸号", "物品编码", "数量");
        List<String> actual   = EasyExcelUtil.readHead(file);

        if (!actual.containsAll(expected)) {
            throw new ServiceException("导入失败：请使用最新模板，确保包含“图纸号”、“物品编码”、“数量”三列");
        }

        // 2. 读 Excel VO
        List<MesItemUploadRequest> list = EasyExcelUtil.importExcel(file, MesItemUploadRequest.class);
        if (CollectionUtils.isEmpty(list)) {
            throw new ServiceException("导入失败：文件中无数据");
        }

        // 3. 准备库位映射
        Map<String,String> locMap = DictUtil.getDictCache(DictType.WARE_HOUSE)
                .stream().collect(Collectors.toMap(d->d.getDictLabel(), d->d.getDictValue(), (a,b)->a));

        // 4. 从 VO 映射到 Entity，并做必要的字段处理
        //    4.1 去重
        Map<String, MesItemStock> unique = new LinkedHashMap<>();
        for (MesItemUploadRequest r : list) {
            String itemNo = StringUtils.trimToEmpty(r.getItemNo());
            if (itemNo.isEmpty()) continue;
            // 每次 put，会覆盖上一次，保留最后一条
            MesItemStock mis = new MesItemStock();
            mis.setItemNo(itemNo);
            mis.setItemName(r.getItemName());
            mis.setItemCount(r.getItemCount());
            mis.setItemMeasure(r.getItemMeasure());
            mis.setItemOrigin(r.getItemOrigin());
            mis.setItemModel(r.getItemModel());

            // —— 关键：根据有没有 bomNo 来设置 itemType 和 bomNo 字段 ——
            String bomNo = StringUtils.trimToEmpty(r.getBomNo());
            mis.setBomNo(bomNo);
            if (StringUtils.isNotBlank(bomNo)) {
                mis.setItemType("01");   // 半成品
            } else {
                mis.setItemType("00");   // 原料
            }

            // —— 库位映射 ——
            mis.setLocation(locMap.getOrDefault(r.getLocation(), ""));
            // （可选）其它默认字段：isValid, uniId, erpCount, netWeight, createdBy/time 等

            unique.put(itemNo, mis);
        }
        List<MesItemStock> toAdd = new ArrayList<>(unique.values());
        if (toAdd.isEmpty()) {
            throw new ServiceException("导入失败：无有效新物料");
        }

        // 5. 【改动点】先删旧
        List<String> itemNos = toAdd.stream()
                .map(MesItemStock::getItemNo)
                .collect(Collectors.toList());
        this.remove(new LambdaQueryWrapper<MesItemStock>()
                .in(MesItemStock::getItemNo, itemNos));

        // 6. 写入
        this.saveBatch(toAdd);

        // 7. 更新 mid_item_stock（仅 BOM 类型）
        for (MesItemStock mis : toAdd) {
            if ("01".equals(mis.getItemType())) {
                // 一次调用，避免重复查询
                Map<String, MidItemStock> midMap =
                        midItemStockService.selectStock(Collections.singletonList(mis.getItemNo()));
                MidItemStock mid = midMap.get(mis.getItemNo());

                if (mid == null) {
                    // 如果你期望“若不存在则新增”，可以在这里插入新纪录：
                    // mid = new MidItemStock();
                    // mid.setItemNo(mis.getItemNo());
                    // mid.setInitialCount(mis.getItemCount());
                    // midItemStockService.save(mid);
                    // continue;

                    // 或者简单跳过，不报错
                    continue;
                }

                // 安全地更新数量
                mid.setInitialCount(mis.getItemCount());
                midItemStockService.updateById(mid);
            }
        }

    }


    /**
     * 新接口V1： 物料上传 Excel → 导入 mes_item_stock → 更新 mid_item_stock（所有在同一事务中）
     *  存在即跳过写入
     */
    @Transactional(rollbackFor = Throwable.class)
    public void uploadNewV1(HttpServletRequest request) {

        // 1. 取文件 & 读取字典（库位映射）
        MultipartFile file = FileUtil.getFile(request);

        // 模板格式检查：改用 Arrays.asList
        List<String> expected = Arrays.asList("图纸号", "物品编码", "数量");
        List<String> actual   = EasyExcelUtil.readHead(file);

        // —— DEBUG 打印实际表头 ——
        System.out.println("【DEBUG】实际表头 = " + actual);


        if (!actual.containsAll(expected)) {
            throw new ServiceException("导入失败：请使用最新模板，确保包含“图纸号”、“物品编码”、“数量”三列");
        }



        List<MesItemUploadRequest> list = EasyExcelUtil.importExcel(file, MesItemUploadRequest.class);
        if (CollectionUtils.isEmpty(list)) {
            throw new ServiceException("导入失败：文件中无数据");
        }

        // 2. 准备库位映射
        Map<String,String> locMap = DictUtil.getDictCache(DictType.WARE_HOUSE).stream()
                .collect(Collectors.toMap(d->d.getDictLabel(), d->d.getDictValue(), (a,b)->a));

        // 3. 转成实体 & 过滤已存在
        List<MesItemStock> rows = PojoUtil.copyList(list, MesItemStock.class);
        List<String> itemNos = rows.stream()
                .map(MesItemStock::getItemNo).collect(Collectors.toList());
        List<MesItemStock> dbList = this.list(
                new LambdaQueryWrapper<MesItemStock>()
                        .in(MesItemStock::getItemNo, itemNos)
        );
        Set<String> existing = dbList.stream()
                .map(MesItemStock::getItemNo).collect(Collectors.toSet());

        List<MesItemStock> toAdd = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (MesItemStock mis : rows) {
            if (StringUtils.isBlank(mis.getItemNo())      // 编码空
                    || existing.contains(mis.getItemNo())        // 已存在
                    || !seen.add(mis.getItemNo())) {             // 重复行
                continue;
            }

            // —— 新增根节点：如果有 bomNo，就标为 BOM 类型，否则物料类型 ——
//            if (StringUtils.isNotBlank(mis.getBomNo())) {
//                mis.setItemType("01");    // 半成品/BOM
//            } else {
//                mis.setItemType("00");    // 纯物料
//            }
            // —— 关键：有 bomNo 就标 '01'，否则 '00' ——
            String bomNo = StringUtils.trimToEmpty(mis.getBomNo());
            mis.setBomNo(bomNo);
            if (!bomNo.isEmpty()) {
                mis.setItemType("01");
            } else {
                mis.setItemType("00");
            }


            // 3.1 类型转换 & 库位映射
//            mis.setItemType(ItemType.getCodeByName(mis.getItemType()));
            mis.setLocation(locMap.getOrDefault(mis.getLocation(), ""));
            // 3.2 BOM 必填校验
            if (ItemType.isBom(mis.getItemType()) && StringUtils.isBlank(mis.getBomNo())) {
                throw new ServiceException("导入失败：BOM ["+mis.getItemNo()+"] 缺少图纸号");
            }
            toAdd.add(mis);
        }

        if (toAdd.isEmpty()) {
            // —— DEBUG 打印所有行错误 ——
            System.out.println("【DEBUG】行校验错误 = ");
//            errors.forEach(e -> System.out.println("  " + e));

            throw new ServiceException("导入失败：无有效新物料");
        }

        // 4. 批量插入 mes_item_stock
        this.saveBatch(toAdd);

        // 5. 对每个 BOM 类型的记录，同步更新 mid_item_stock
        for (MesItemStock mis : toAdd) {
            if (ItemType.isBom(mis.getItemType())) {

                Map<String, MidItemStock> midMap = midItemStockService.selectStock(
                        Collections.singletonList(mis.getItemNo())
                );
                MidItemStock mid = midMap.get(mis.getItemNo());
                if (mid != null) {
                    mid.setInitialCount(mis.getItemCount());
                    midItemStockService.updateById(mid);
                }

            }
        }
    }


    //////////////////////////////////////////////////////////////////////////// //////////////////////////////////////////////////////////////////////////////////






    private void checkItem(String itemNo, String bomNo, String id) {
        checkItemNo(itemNo, id);
        if (StringUtils.isNotBlank(bomNo)) {
            checkBomNo(itemNo, bomNo, id);
        }
    }

    /**
     * 校验bom编码
     *
     * @param itemNo
     * @param bomNo
     * @param id
     */
    private void checkBomNo(String itemNo, String bomNo, String id) {
        MesItemStock bom = this.getOne(new LambdaQueryWrapper<MesItemStock>()
                .eq(MesItemStock::getBomNo, bomNo)
        );
        if (Objects.nonNull(bom)) {
            if (StringUtils.isEmpty(id)) {
                throw new CavException(itemNo + "物料编码已存在!请检查");
            } else {
                if (!id.equals(bom.getId())) {
                    throw new CavException(itemNo + "物料编码已存在!请检查");
                }
            }
        }
    }

    /**
     * 校验物料编码
     *
     * @param itemNo
     * @param id
     */
    private void checkItemNo(String itemNo, String id) {
        MesItemStock one = this.getOne(new LambdaQueryWrapper<MesItemStock>()
                .eq(MesItemStock::getItemNo, itemNo)
        );
        if (Objects.nonNull(one)) {
            if (StringUtils.isEmpty(id)) {
                throw new CavException(itemNo + "物料编码已存在!请检查");
            } else {
                if (!id.equals(one.getId())) {
                    throw new CavException(itemNo + "物料编码已存在!请检查");
                }
            }
        }
    }

    @Override
    public MesItemStockInfoResponse getInfoById(String id) {
        MesItemStock entity = this.getById(id);
        return PojoUtil.copyBean(entity, MesItemStockInfoResponse.class);
    }

    @Override
    public void updateInfo(MesItemStockUpdateRequest params) {
        MesItemStock entity = PojoUtil.copyBean(params, MesItemStock.class);
        this.checkItem(params.getItemNo(), params.getBomNo(), params.getId());
        this.updateById(entity);
        if (ItemType.isBom(params.getItemType())) {
            ThreadPool.execute(() -> {
                updateBomCount(params.getItemNo(), params.getItemCount());
            });
        }
    }


    private void updateBomCount(String itemNo, BigDecimal count) {
        Map<String, MidItemStock> midMap = midItemStockService.selectStock(Collections.singletonList(itemNo));
        MidItemStock mid = midMap.get(itemNo);
        if (Objects.nonNull(mid)) {
            mid.setInitialCount(count);
            midItemStockService.updateById(mid);
        }
    }

    @Override
    public BaseResponse<List<MesItemStockPageResponse>> pageInfo(Paging page, MesItemStockPageRequest params) {
        PageUtil<MesItemStockPageResponse, MesItemStockPageRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void delete(String id) {
        MesItemStock mis = this.getById(id);
        mis.setId(id);
        mis.setItemNo(mis.getItemNo() + "-作废");
        mis.setItemName(mis.getItemName() + "-作废");
        mis.setBomNo(mis.getBomNo() + "-作废");
        mis.setIsValid(ValidEnum.INVALID.getCode());
        //TODO 校验是否有订单，有订单的不能删除
        this.updateById(mis);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void upload(HttpServletRequest request) {
        MultipartFile file = FileUtil.getFile(request);
        List<DictDataVo> caches = DictUtil.getDictCache(DictType.WARE_HOUSE);
        Map<String, String> map = caches.stream().collect(Collectors.toMap(DictDataVo::getDictLabel, DictDataVo::getDictValue, (k1, k2) -> k1));
        List<MesItemUploadRequest> list = EasyExcelUtil.importExcel(file, MesItemUploadRequest.class);
        List<MesItemStock> misList = PojoUtil.copyList(list, MesItemStock.class);
        if (CollectionUtils.isNotEmpty(misList)) {
            List<String> itemNos = misList.stream().map(MesItemStock::getItemNo).collect(Collectors.toList());
            LambdaQueryWrapper<MesItemStock> eq = new LambdaQueryWrapper<MesItemStock>().in(MesItemStock::getItemNo, itemNos);
            List<MesItemStock> dbList = this.list(eq);
            List<String> dbItemNos = dbList.stream().map(MesItemStock::getItemNo).collect(Collectors.toList());
            List<MesItemStock> adds = new ArrayList<>();
            List<String> addItems = new ArrayList<>();
            for (MesItemStock mis : misList) {
                if (StringUtils.isBlank(mis.getItemNo()) || dbItemNos.contains(mis.getItemNo())) {
                    continue;
                }
                mis.setItemType(ItemType.getCodeByName(mis.getItemType()));
                mis.setLocation(map.getOrDefault(mis.getLocation(), StringUtils.EMPTY));
                if (addItems.contains(mis.getItemNo())) {
                    continue;
                }
                addItems.add(mis.getItemNo());
                if (ItemType.BOM.getCode().equals(mis.getItemType()) && StringUtils.isBlank(mis.getBomNo())) {
                    continue;
                }
                adds.add(mis);
            }
            if (CollectionUtils.isNotEmpty(adds)) {
                this.saveBatch(adds);
                for (MesItemStock add : adds) {
                    //更新成品数量
                    if (ItemType.isBom(add.getItemType())) {
                        ThreadPool.execute(() -> {
                            updateBomCount(add.getItemNo(), add.getItemCount());
                        });
                    }
                }

            }

        }
    }

    @Override
    public Map<String, MesItemStock> checkedItemNos(List<String> itemNos) {
        LambdaQueryWrapper<MesItemStock> eq = new LambdaQueryWrapper<MesItemStock>().in(MesItemStock::getItemNo, itemNos);
        List<MesItemStock> dbList = this.list(eq);
        Map<String, MesItemStock> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(dbList)) {
            dbList.forEach(m -> map.put(m.getItemNo(), m));
        }
        itemNos.forEach(s -> {
            if (map.get(s) == null) {
                throw new CavException("产品编码不存在" + s + "不存在！");
            }
        });
        return map;
    }

    @Override
    public Map<String, MesItemStock> getByBomNos(List<String> boms) {
        if (CollectionUtils.isEmpty(boms)) {
            return new HashMap<>();
        }
        return mapper.getByBomNos(boms);
    }

    @Override
    public Map<String, MesItemStock> getByItemNos(List<String> itemNos) {
        if (CollectionUtils.isEmpty(itemNos)) {
            return new HashMap<>();
        }
        return mapper.getByItemNos(itemNos);
    }

    @Override
    public MesItemStock getByItemNo(String itemNo) {
        MesItemStock one = this.getOne(new LambdaQueryWrapper<MesItemStock>().eq(MesItemStock::getItemNo, itemNo));
        if (Objects.isNull(one)) {
            return null;
        }
        return one;
    }


    @Override
    public List<ItemSelectedResponse> itemNoSelected(ItemNoSelectedRequest request) {
        String itemNo = request.getItemNo();
        List<ItemSelectedResponse> list = mapper.itemNoSelected(request);
        return list;
    }

    @Override
    public List<ItemSelectedResponse> listBomByNo(BomNoSelectedRequest params) {

        return mapper.listBomByNo(params);
    }

    @Override
    public Map<String, MesItemStock> checkedBoms(List<String> boms) {
        LambdaQueryWrapper<MesItemStock> eq = new LambdaQueryWrapper<MesItemStock>().in(MesItemStock::getBomNo, boms);
        List<MesItemStock> dbList = this.list(eq);
        Map<String, MesItemStock> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(dbList)) {
            dbList.forEach(m -> map.put(m.getBomNo(), m));
        }
        boms.forEach(s -> {
            if (map.get(s) == null) {
                throw new CavException("图纸号" + s + "不存在！");
            }
        });
        return map;
    }

    @Override
    public BaseResponse<List<MesItemStockPageResponse>> bomPageList(Paging page, BomPageRequest params) {
        PageUtil<MesItemStockPageResponse, BomPageRequest> pu = (p, q) -> mapper.bomPageList(p, q);
        return pu.page(page, params);
    }
}
