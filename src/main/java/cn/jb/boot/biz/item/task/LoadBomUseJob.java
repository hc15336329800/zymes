package cn.jb.boot.biz.item.task;

import cn.jb.boot.biz.item.service.BomUsedService;
import cn.jb.boot.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Component
@Slf4j
public class LoadBomUseJob {

    @Resource
    private BomUsedService usedService;
    private volatile String startTime = "2023-07-01 00:00:00";


    /**
     * 加载用料信息
     */
//    @Scheduled(cron = "0 0 0/1 * * ?")
    public void process() {

        long start = System.currentTimeMillis();
        log.info("开始加载BOM用料...");
        //TODO  一段时间，定时同步一次
        log.info("加载BOM用料完成...cost:{}", System.currentTimeMillis() - start);
        startTime = DateUtil.formatDateTime(LocalDateTime.now());

    }


}
