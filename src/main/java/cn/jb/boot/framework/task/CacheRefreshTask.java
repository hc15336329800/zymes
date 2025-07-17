package cn.jb.boot.framework.task;

import cn.jb.boot.biz.device.mapper.DeviceInfoMapper;
import cn.jb.boot.biz.item.mapper.WarehouseInfoMapper;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.biz.item.mapper.MesItemStockMapper;
import cn.jb.boot.system.entity.DictData;
import cn.jb.boot.system.mapper.DeptInfoMapper;
import cn.jb.boot.system.mapper.UserInfoMapper;
import cn.jb.boot.system.service.DictDataService;
import cn.jb.boot.system.vo.DictDataVo;
import cn.jb.boot.util.DictUtil;
import cn.jb.boot.util.PojoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 缓存刷新任务
 *
 * @author xyb
 * @Description
 * @Date 2022/7/2 19:20
 */
@Slf4j
@Component
public class CacheRefreshTask implements CommandLineRunner {

    @Resource
    private DictDataService dictDataService;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private MesItemStockMapper stockMapper;
    @Resource
    private DeptInfoMapper deptInfoMapper;
    @Resource
    private WarehouseInfoMapper warehouseInfoMapper;
    @Resource
    private DeviceInfoMapper deviceInfoMapper;


//    @Scheduled(cron = "0 0/30 * * * ?")
    public void initData() {
        log.info("定时刷新缓存  暂时停止  请手动打开");
        System.out.println("info:  定时刷新缓存开始  装载字典（频率30分钟） 暂时停止  请手动打开");
        //run();
    }

    @Override
    public void run(String... args) throws Exception {
        run();
    }


    public void run() {
        log.info("Cache load start：装载基础数据");
        List<DictData> dictData = dictDataService.allDict();
        List<DictDataVo> list = PojoUtil.copyList(dictData, DictDataVo.class);
        Map<String, List<DictDataVo>> dict = list.stream().collect(Collectors.groupingBy(DictDataVo::getDictType));
        //加在物料信息表
        List<DictDataVo> misList = stockMapper.getCache();
        dict.put(DictType.BOM_NO.getCode(), misList);
        List<DictDataVo> userList = userInfoMapper.getCache();
        dict.put(DictType.USER_INFO.getCode(), userList);
        List<DictDataVo> workShopList = deptInfoMapper.workShopSelect();
        dict.put(DictType.WORK_SHOP.getCode(), workShopList);
        List<DictDataVo> locations = warehouseInfoMapper.selected();
        dict.put(DictType.WARE_HOUSE.getCode(), locations);
        List<DictDataVo> deviceList = deviceInfoMapper.selected();
        dict.put(DictType.DEVICE.getCode(), deviceList);

        DictUtil.setDictCache(dict);

        log.info("Cache load start：装载用户数据资源结束………………");
    }
}
