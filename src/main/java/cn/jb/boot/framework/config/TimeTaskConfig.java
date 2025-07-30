package cn.jb.boot.framework.config;

import cn.jb.boot.biz.work.service.impl.httpGetDataServiceImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;

@Configuration
public class TimeTaskConfig {
    @Resource
    private httpGetDataServiceImpl httpGetDataService;

    //上海嘉意从CSV中获取数据
//    @Scheduled(cron = "0 */5 * * * ?")
    public void monitoringDataTransmissionTask() {

        System.out.println("info:  上海嘉意从CSV中获取数据（频率三分钟） ");
        httpGetDataService.getAllDeviceUrl();
    }
}
