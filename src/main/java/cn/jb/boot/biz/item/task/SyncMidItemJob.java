package cn.jb.boot.biz.item.task;

import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.entity.MesProcedure;
import cn.jb.boot.biz.item.entity.MidItemStock;
import cn.jb.boot.biz.item.mapper.MesProcedureMapper;
import cn.jb.boot.biz.item.service.MesItemStockService;
import cn.jb.boot.biz.item.service.MidItemStockService;
import cn.jb.boot.framework.enums.JbEnum;
import cn.jb.boot.util.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


//定时同步物料

@Component
@Slf4j
public class SyncMidItemJob {

    @Resource
    private MidItemStockService midItemStockService;
    @Resource
    private MesProcedureMapper mesProcedureMapper;
    @Resource
    private MesItemStockService mesItemStockService;

    private volatile String startTime = "2024-01-01 00:00:00";
    private static final String lastName = "装车";


    @Scheduled(cron = "0 0/1 * * * ?")
    public void process() {

         System.out.println("info:  物料新增同步调用开始（频率一分钟）");

        syncMissMids();
        updateLastProc();

    }


    private void updateLastProc() {
        List<String> itemNos = mesProcedureMapper.selectNearItemNo(startTime);
        for (String itemNo : itemNos) {
            List<MesProcedure> list =
                    mesProcedureMapper.selectList(new LambdaQueryWrapper<MesProcedure>().eq(MesProcedure::getItemNo,
                            itemNo));
            int maxNo = list.stream().mapToInt(MesProcedure::getSeqNo).max().getAsInt();
            Optional<MesProcedure> optional =
                    list.stream().filter(d -> d.getProcedureName().contains(lastName)).findFirst();
            MesProcedure lastMid;
            if (optional.isPresent()) {
                lastMid = optional.get();
            } else {
                //最后一道工序
                Optional<MesProcedure> first = list.stream().filter(d -> d.getSeqNo() == maxNo).findFirst();
                lastMid = first.get();
            }
            midItemStockService.update(new LambdaUpdateWrapper<MidItemStock>().eq(MidItemStock::getItemNo,
                    lastMid.getItemNo())
                    .set(MidItemStock::getLastFlag, JbEnum.CODE_00.getCode()));
            MesItemStock mis = mesItemStockService.getByItemNo(lastMid.getItemNo());
            if (Objects.isNull(mis)) {
                continue;
            }
            midItemStockService.update(new LambdaUpdateWrapper<MidItemStock>()
                    .eq(MidItemStock::getItemNo, lastMid.getItemNo())
                    .eq(MidItemStock::getProcedureCode, lastMid.getProcedureCode())
                    .set(MidItemStock::getLastFlag, JbEnum.CODE_01.getCode())
                    .set(MidItemStock::getInitialCount, mis.getItemCount())
            );

        }
        startTime = DateUtil.formatDateTime(LocalDateTime.now());

    }

    private void syncMissMids() {
        List<MesProcedure> ids = midItemStockService.getMissingMid();
        if (CollectionUtils.isNotEmpty(ids)) {
            List<MidItemStock> list = ids.stream().map(d -> {
                MidItemStock mis = new MidItemStock();
                mis.setProcedureCode(d.getProcedureCode());
                mis.setProcedureName(d.getProcedureName());
                mis.setSeqNo(d.getSeqNo());
                mis.setItemNo(d.getItemNo());
                mis.setLastFlag(JbEnum.CODE_00.getCode());
                return mis;
            }).collect(Collectors.toList());
            midItemStockService.saveBatch(list);
        }
    }


}
