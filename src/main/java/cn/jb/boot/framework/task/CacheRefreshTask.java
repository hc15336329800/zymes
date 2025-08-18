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


    @Scheduled(cron = "0 0/13 * * * ?")
    public void initData() {
//        log.info("定时刷新缓存  暂时停止  请手动打开");
        System.out.println("info:  定时刷新缓存开始  装载bom（频率13分钟）  ");
        run();
    }

    @Scheduled(cron = "0 0/3 * * * ?")
    public void initData3() {
//        log.info("定时刷新缓存  暂时停止  请手动打开");
        System.out.println("info:  定时刷新缓存开始  装载基础字典（频率3分钟）  ");
        runBom();
    }

    @Override
    public void run(String... args) throws Exception {
        run();
    }


    public void runBom() {
        // [新增注释] 记录开始装载缓存
        log.info("Cache load start：装载基础bom数据");

        // [新增注释] 1. 查询所有系统字典并按字典类型分组
        List<DictData> dictData = dictDataService.allDict();
        List<DictDataVo> list = PojoUtil.copyList(dictData, DictDataVo.class);
        Map<String, List<DictDataVo>> dict =
                list.stream().collect(Collectors.groupingBy(DictDataVo::getDictType));

//         [新增注释] 2. 加载物料（BOM）信息
        List<DictDataVo> misList = stockMapper.getCache();
        dict.put(DictType.BOM_NO.getCode(), misList);

        // [新增注释] 将所有基础数据写入缓存
        DictUtil.setDictCache(dict);

        // [新增注释] 记录结束
        log.info("Cache load start：装载用户数据资源结束………………");
    }

    public void run() {
        // [新增注释] 记录开始装载缓存
        log.info("Cache load start：装载基础数据");

        // [新增注释] 1. 查询所有系统字典并按字典类型分组
        List<DictData> dictData = dictDataService.allDict();
        List<DictDataVo> list = PojoUtil.copyList(dictData, DictDataVo.class);
        Map<String, List<DictDataVo>> dict =
                list.stream().collect(Collectors.groupingBy(DictDataVo::getDictType));

        // [新增注释] 3. 加载用户信息
        List<DictDataVo> userList = userInfoMapper.getCache();
        dict.put(DictType.USER_INFO.getCode(), userList);

        // [新增注释] 4. 加载车间信息
        List<DictDataVo> workShopList = deptInfoMapper.workShopSelect();
        dict.put(DictType.WORK_SHOP.getCode(), workShopList);

        // [新增注释] 5. 加载仓库信息
//        List<DictDataVo> locations = warehouseInfoMapper.selected();
//        dict.put(DictType.WARE_HOUSE.getCode(), locations);

        // [新增注释] 6. 加载设备信息
        List<DictDataVo> deviceList = deviceInfoMapper.selected();
        dict.put(DictType.DEVICE.getCode(), deviceList);

        // [新增注释] 将所有基础数据写入缓存
        DictUtil.setDictCache(dict);

        // [新增注释] 记录结束
        log.info("Cache load start：装载用户数据资源结束………………");
    }


}
