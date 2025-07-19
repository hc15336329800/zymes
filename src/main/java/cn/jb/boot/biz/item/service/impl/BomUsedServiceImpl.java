package cn.jb.boot.biz.item.service.impl;

import cn.jb.boot.biz.item.entity.BomUsed;
import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.entity.MesItemUse;
import cn.jb.boot.biz.item.enums.ItemType;
import cn.jb.boot.biz.item.mapper.BomUsedMapper;
import cn.jb.boot.biz.item.mapper.MesItemStockMapper;
import cn.jb.boot.biz.item.mapper.MesItemUseMapper;
import cn.jb.boot.biz.item.service.BomUsedService;
import cn.jb.boot.biz.item.vo.response.UseItemTreeResp;
import cn.jb.boot.util.ArithUtil;
import cn.jb.boot.util.BomUsedUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * bom用料依赖 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 16:55:58
 */
@Service
@Slf4j
public class BomUsedServiceImpl extends ServiceImpl<BomUsedMapper, BomUsed> implements BomUsedService {

    @Resource
    private BomUsedMapper mapper; // 注入 BomUsedMapper，用于操作 t_bom_used 表

    @Resource
    private MesItemUseMapper useMapper; // 注入 MesItemUseMapper，用于查询用料明细 mes_item_use 表

    @Resource
    private MesItemStockMapper stockMapper; // 注入 MesItemStockMapper，用于查询物料库存 mes_item_stock 表





    /**
     * 根据给定的起始时间，批量加载所有更新过的物料的 BOM 依赖数据  （按时间）
     *
     * @param startTime 更新查询的起始时间
     */
    @Override
    public void load(String startTime) {

        loadWithParents(startTime);
        return;

//        // 刷新受用料变更影响的物料更新时间  t  调试！！
//       int a  =  UpdataItemNOUpTime(startTime);
//
//
//        // 查询所有在 startTime 之后更新且类型为 BOM 的物料记录
//        //   SELECT id, item_no, bom_no  构建纯树
//        List<MesItemStock> itemList = stockMapper.selectBoms(startTime);
//        // 遍历每个物料，加载其 BOM 依赖数据
//        //
//        for (MesItemStock stock : itemList) {
//            loadBomData(stock);
//        }
    }

    @Override
    public void loadWithParents(String startTime) {
        // 1. 先刷新那些因为用料变化而被动变更的物料更新时间
        UpdataItemNOUpTime(startTime);

        // 2. 查询所有受影响的 BOM 类型物料
        List<MesItemStock> itemList = stockMapper.selectBoms(startTime);
        if (CollectionUtils.isEmpty(itemList)) {
            log.info("无 BOM 物料需更新，跳过处理");
            return;
        }

        // 3. 对每个物料进行 BOM 重建并递归更新其母件
        for (MesItemStock stock : itemList) {
            try {
                // 重构自身 BOM
                loadBomData(stock);

                // 向上递归重构所有母件
                List<String> visited = new ArrayList<>();
                loadParBomData(stock.getItemNo(), visited);

            } catch (Exception e) {
                log.error("处理物料 {} 时异常：{}", stock.getItemNo(), e.getMessage(), e);
            }
        }
    }



    //todo ; 增加一个方法  ：查询mes_item_stock（根据更新时间），解析出来itemno号  ，然后去更新mes_item_stock表的对应的数据的更新时间。

    /**
     * 用于刷新 mes_item_stock 表中指定时间后更新的物料的 updated_time 字段，
     * 以确保后续 BOM 构建逻辑能正确识别这些物料作为“被动变更”的来源。
     *
     * 场景：用料关系发生变更时，需要手动触发这些物料的更新时间，以便纳入 BOM 重构范围。
     *
     * @param startTime 起始时间（只更新此时间之后变动过的物料）
     * @return 成功更新的物料数量
     */
    public int UpdataItemNOUpTime(String startTime) {
        // 查询 mes_item_use 表中指定时间后变更的父项物料编号（item_no）
        List<String> itemNos = useMapper.selectNearItemNo(startTime);
        if (CollectionUtils.isEmpty(itemNos)) {
            log.info("无用料更新记录，无需刷新库存更新时间");
            return 0;
        }

        // 构造更新时间
        Date now = new Date();

        // 调用 stockMapper 更新对应库存物料的 updated_time 字段
        int updatedCount = stockMapper.updateItemUpdateTimeByItemNos(itemNos, now);

        log.info("成功刷新 {} 条物料的更新时间（作为用料变更影响）", updatedCount);
        return updatedCount;
    }


// -------------------------------------------------------------------------------------------------------------------------------------

    /**
     * 根据给定的起始时间，批量加载所有更新过的物料的 BOM 依赖数据  （按物料号和bom号）
     *
     * @param itemNo 物料编码
     * @param bomNo  BOM 编号
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void loadByItem(String itemNo, String bomNo) {
        try {
            if (StringUtils.isBlank(itemNo)) {
                log.warn("传入的物料号为空，忽略处理");
                return;
            }
            if (StringUtils.isBlank(bomNo)) {
                log.warn("传入的 BOM 编号为空，忽略处理");
                return;
            }

//            // 可选：检查物料是否存在（防止写入脏数据）
//            MesItemStock exist = stockMapper.selectById(itemNo);
//            if (exist == null) {
//                log.warn("未找到物料信息, itemNo: {}", itemNo);
//                return;
//            }

            // 构造包含物料号和 bomNo 的对象
            MesItemStock stock = new MesItemStock();
            stock.setItemNo(itemNo);
            stock.setBomNo(bomNo);

            // 调用加载方法
            loadBomData(stock);

        } catch (Exception e) {
            log.error("按物料号加载 BOM 异常, itemNo: {}, bomNo: {}", itemNo, bomNo, e);
            throw e;
        }
    }



    //--------------------------------------------------------------------------------------------------------------




    /**
     * 批量获取多个物料编码对应的 BOM 依赖列表，并按物料编码分组
     *
     * @param itemNos 物料编码列表
     * @return Map<itemNo, List<BomUsed>>
     */
    @Override
    public Map<String, List<BomUsed>> getBomDepend(List<String> itemNos) {
        // 构造查询条件：item_no IN (...)
        LambdaQueryWrapper<BomUsed> in = new LambdaQueryWrapper<BomUsed>()
                .in(BomUsed::getItemNo, itemNos);
        // 执行查询
        List<BomUsed> list = this.list(in);
        // 按 itemNo 分组返回
        return list.stream()
                .collect(Collectors.groupingBy(BomUsed::getItemNo));
    }

    /**
     * 递归加载指定物料的父级 BOM 数据（用于完整树状结构构建）
     *
     * @param itemNo 当前物料编码
     * @param itList 已处理的物料编码列表，防止循环递归
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void loadParBomData(String itemNo, List<String> itList) {
        try {
            // 如果已处理过该物料，则直接返回
            if (itList.contains(itemNo)) {
                return;
            }
            log.info("开始加载bom:{}", itemNo);

            // 查询当前物料作为子项时的所有父级库存记录
            List<MesItemStock> mesItemStocks = useMapper.selectUses(itemNo);
            // 标记为已处理
            itList.add(itemNo);
            // 如果存在父级记录，则递归处理
            if (CollectionUtils.isNotEmpty(mesItemStocks)) {
                for (MesItemStock mis : mesItemStocks) {
                    log.info("开始处理bom:{}", itemNo);
                    // 加载当前父级的 BOM 数据
                    loadBomData(mis);
                    // 递归加载更上一级的父级 BOM
                    loadParBomData(mis.getItemNo(), itList);
                }
            }
        } catch (Exception e) {
            // 打印错误并抛出，以触发事务回滚
            e.printStackTrace();
            throw e;
        }
    }



    /**
     * 根据 MesItemStock 对象加载单个物料的完整 BOM 依赖数据，包括自身节点
     *
     * @param mis MesItemStock 对象，包含 itemNo 与 bomNo 等基础信息
     */
    public void loadBomData(MesItemStock mis) {
        long start = System.currentTimeMillis(); // 记录开始时间
        String itemNo = mis.getItemNo();
        String bomNo = mis.getBomNo();
        List<BomUsed> list = new ArrayList<>();
        try {
            // 从当前物料开始递归查找子项用料数据
//            findChildUse(itemNo, itemNo, BigDecimal.ONE, list);
            findChildUse(itemNo, itemNo, BigDecimal.ONE, list, new HashSet<String>(), 1);


        } catch (Throwable e) {
            // 如果递归过程中出错，记录错误日志
            log.error("同步bom异常的,itemNo:{}", itemNo);
        }
        // 为每条记录补充上下文字段
        list.forEach(data -> {
            data.setItemNo(itemNo);
            data.setBomNo(bomNo);
        });
        // 构造当前物料自身的 BomUsed 节点
        BomUsed current = getCurrent(itemNo, bomNo);
        list.add(current);
        // 删除数据库中该物料的旧依赖记录
        this.remove(new LambdaQueryWrapper<BomUsed>()
                .eq(BomUsed::getItemNo, itemNo));

        // 去重相同 useItemNo + parentCode 的记录，合并用量
        Map<String, BomUsed> mergedMap = new LinkedHashMap<>();
        for (BomUsed b : list) {
            String key = b.getUseItemNo() + "|" + b.getParentCode();
            if (mergedMap.containsKey(key)) {
                BomUsed existing = mergedMap.get(key);
                // 合并数量（累加或取最大，按业务需求选）
                existing.setUseItemCount(existing.getUseItemCount().add(b.getUseItemCount()));
                if (existing.getFixedUsed() != null && b.getFixedUsed() != null) {
                    existing.setFixedUsed(existing.getFixedUsed().max(b.getFixedUsed()));
                }
            } else {
                mergedMap.put(key, b);
            }
        }
        list = new ArrayList<>(mergedMap.values());// 替换 list



        // 批量插入新的依赖列表
        this.saveBatch(list);
        log.info("bom加载完成,itemNo:{},cost:{}", itemNo, System.currentTimeMillis() - start);
    }

    /**
     * 构造当前物料自身节点的 BomUsed 对象，表示自己依赖自己、数量为 1
     *
     * @param itemNo 物料编码
     * @param bomNo  BOM 编号
     * @return BomUsed 对象
     */
    private static BomUsed getCurrent(String itemNo, String bomNo) {
        BomUsed current = new BomUsed();
        current.setBomNo(bomNo);
        current.setItemNo(itemNo);
        current.setUseItemNo(itemNo);
        current.setUseItemType(ItemType.BOM.getCode()); // 类型为 BOM
        current.setItemNos(itemNo); // 路径仅包含自己
        current.setUseItemCount(BigDecimal.ONE); // 数量为 1
        current.setParentCode(itemNo); // 父节点编码为自己
        return current;
    }

    /**
     * 递归查找指定物料的子项用料，并根据传入倍率计算累计数量，构造 BomUsed 对象列表
     *
     * @param itemNo   当前物料编码
     * @param itemNos  父级路径字符串，用 '|' 分隔
     * @param rate     累计数量倍率
     * @param list     用于收集结果的列表
     */
    private void findChildUse(String itemNo, String itemNos, BigDecimal rate, List<BomUsed> list, Set<String> visited, int depth)
    {
        if (visited.contains(itemNo)) {
            log.warn("检测到循环依赖: {}", itemNo);
            return;
        }
        visited.add(itemNo);

        if (depth > 20) {
            log.warn("超过最大层级保护: {}, 当前层级: {}", itemNo, depth);
            visited.remove(itemNo); // 【修复循环依赖：回溯】
            return;
        }

        List<MesItemUse> useList = useMapper.selectList(
                new LambdaQueryWrapper<MesItemUse>()
                        .eq(MesItemUse::getItemNo, itemNo)
        );

        if (CollectionUtils.isNotEmpty(useList)) {
            for (MesItemUse miu : useList) {


                // 防止自己依赖自己，立即跳过
                if (itemNo.equals(miu.getUseItemNo())) {
                    log.warn("检测到自己依赖自己: {}, 自动跳过", itemNo);
                    continue;
                }

                BigDecimal count = ArithUtil.mul(rate, miu.getUseItemCount()).setScale(3, RoundingMode.DOWN);
                BomUsed used = new BomUsed();
                used.setUsedId(miu.getId());
                used.setParentCode(miu.getItemNo());
                used.setUseItemNo(miu.getUseItemNo());
                used.setUseItemType(miu.getUseItemType());
                used.setUseItemCount(count);
                used.setFixedUsed(miu.getFixedUse());
                appendNos(itemNos, used);
                list.add(used);

                MesItemStock stock = stockMapper.selectById(miu.getUseItemNo());
                if (stock != null && "00".equals(stock.getItemType())) {
                    continue;
                }

                findChildUse(miu.getUseItemNo(), used.getItemNos(), count, list, visited, depth + 1);
            }
        }
        visited.remove(itemNo); // 【修复循环依赖：回溯时移除，避免兄弟分支误判环】

    }


    /**
     * 将当前节点编码追加到父路径字符串后，构造新的路径，并设置到 BomUsed 对象中
     *
     * @param parNo       父路径字符串
     * @param currentUser 当前 BomUsed 对象
     */
    private void appendNos(String parNo, BomUsed currentUser) {
        StringBuilder sv = new StringBuilder();
        if (StringUtils.isNotBlank(parNo)) {
            sv.append(parNo).append("|");
        }
        sv.append(currentUser.getUseItemNo());
        currentUser.setItemNos(sv.toString());
    }



    //--------------------------------------------------------------------------------------------------------------

    /**
     * 获取指定物料的所有直接依赖（不递归）
     *
     * @param itemNo 物料编码
     * @return 该物料的所有 BomUsed 列表
     */
    @Override
    public List<BomUsed> tree(String itemNo) {
        // 查询条件：item_no = itemNo
        return this.list(new LambdaQueryWrapper<BomUsed>()
                .eq(BomUsed::getItemNo, itemNo));
    }

}
