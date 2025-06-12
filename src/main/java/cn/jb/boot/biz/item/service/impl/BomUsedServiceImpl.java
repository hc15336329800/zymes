package cn.jb.boot.biz.item.service.impl;

import cn.jb.boot.biz.item.entity.BomUsed;
import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.entity.MesItemUse;
import cn.jb.boot.biz.item.enums.ItemType;
import cn.jb.boot.biz.item.mapper.BomUsedMapper;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private BomUsedMapper mapper;
    @Resource
    private MesItemUseMapper useMapper;

    @Override
    public Map<String, List<BomUsed>> getBomDepend(List<String> itemNos) {
        LambdaQueryWrapper<BomUsed> in = new LambdaQueryWrapper<BomUsed>().in(BomUsed::getItemNo, itemNos);
        List<BomUsed> list = this.list(in);
        return list.stream().collect(Collectors.groupingBy(BomUsed::getItemNo));
    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void loadParBomData(String itemNo, List<String> itList) {
        try {
            if (itList.contains(itemNo)) {
                return;
            }
            log.info("开始加载bom:{}", itemNo);

            List<MesItemStock> mesItemStocks = useMapper.selectUses(itemNo);
            itList.add(itemNo);
            if (CollectionUtils.isNotEmpty(mesItemStocks)) {
                for (MesItemStock mis : mesItemStocks) {
                    log.info("开始处理bom:{}", itemNo);
                    loadBomData(mis);
                    loadParBomData(mis.getItemNo(), itList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    @Override
    public List<BomUsed> tree(String itemNo) {
        return this.list(new LambdaQueryWrapper<BomUsed>().eq(BomUsed::getItemNo, itemNo));

    }


    public void loadBomData(MesItemStock mis) {
        long start = System.currentTimeMillis();
        String itemNo = mis.getItemNo();
        String bomNo = mis.getBomNo();
        List<BomUsed> list = new ArrayList<>();
        try {
            findChildUse(itemNo, itemNo, BigDecimal.ONE, list);
        } catch (Throwable e) {
            log.error("同步bom异常的,itemNo:{}", itemNo);
        }
        list.forEach(data -> {
            data.setItemNo(itemNo);
            data.setBomNo(bomNo);
        });
        BomUsed current = getCurrent(itemNo, bomNo);
        list.add(current);
        this.remove(new LambdaQueryWrapper<BomUsed>().eq(BomUsed::getItemNo, itemNo));
        this.saveBatch(list);
        log.info("bom加载完成,itemNo:{},cost:{}", itemNo, System.currentTimeMillis() - start);
    }


    private static BomUsed getCurrent(String itemNo, String bomNo) {
        BomUsed current = new BomUsed();
        current.setBomNo(bomNo);
        current.setItemNo(itemNo);
        current.setUseItemNo(itemNo);
        current.setUseItemType(ItemType.BOM.getCode());
        current.setItemNos(itemNo);
        current.setUseItemCount(BigDecimal.ONE);
        current.setParentCode(itemNo);
        return current;
    }


    private void findChildUse(String itemNo, String itemNos, BigDecimal rate, List<BomUsed> list) {
        List<MesItemUse> useList = useMapper.selectList(new LambdaQueryWrapper<MesItemUse>().eq(MesItemUse::getItemNo
                , itemNo));
        if (CollectionUtils.isNotEmpty(useList)) {
            MesItemUse miu;
            for (int i = 0; i < useList.size(); i++) {
                miu = useList.get(i);
                if (itemNo.equals(miu.getUseItemNo())) {
                    continue;
                }
                BigDecimal useItemCount = miu.getUseItemCount();
                BigDecimal count = ArithUtil.mul(rate, useItemCount).setScale(3, RoundingMode.DOWN);
                BomUsed used = new BomUsed();
                used.setUsedId(miu.getId());
                used.setParentCode(miu.getItemNo());
                used.setUseItemNo(miu.getUseItemNo());
                used.setUseItemType(miu.getUseItemType());
                used.setUseItemCount(count);
                used.setFixedUsed(miu.getFixedUse());
                appendNos(itemNos, used);
                list.add(used);

                findChildUse(miu.getUseItemNo(), used.getItemNos(), count, list);
            }

        }
    }


    private void appendNos(String parNo, BomUsed currentUser) {
        StringBuilder sv = new StringBuilder();
        if (StringUtils.isNotBlank(parNo)) {
            sv.append(parNo).append("|");
        }
        sv.append(currentUser.getUseItemNo());
        currentUser.setItemNos(sv.toString());
    }


}
