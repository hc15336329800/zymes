package cn.jb.boot.biz.agvcar.service.impl;

import cn.jb.boot.biz.agvcar.entity.AgvManageInfo;
import cn.jb.boot.biz.agvcar.mapper.AgvManageInfoMapper;
import cn.jb.boot.biz.agvcar.mapper.BomManageInfoMapper;
import cn.jb.boot.biz.agvcar.service.AgvManageInfoService;
import cn.jb.boot.biz.agvcar.vo.request.AgvManageInfoPageRequest;
import cn.jb.boot.biz.agvcar.vo.response.AgvManageInfoPageResponse;
import cn.jb.boot.biz.item.entity.DeviceLocationInfo;
import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.entity.MesItemUse;
import cn.jb.boot.biz.item.entity.MesProcedure;
import cn.jb.boot.biz.item.enums.ItemType;
import cn.jb.boot.biz.item.mapper.DeviceLocationMapper;
import cn.jb.boot.biz.item.mapper.MesItemStockMapper;
import cn.jb.boot.biz.item.service.DeviceLocationService;
import cn.jb.boot.biz.item.service.MesItemStockService;
import cn.jb.boot.biz.item.service.MesItemUseService;
import cn.jb.boot.biz.item.service.MesProcedureService;
import cn.jb.boot.biz.item.service.impl.MesItemUseServiceImpl;
import cn.jb.boot.biz.item.service.impl.MesProcedureServiceImpl;
import cn.jb.boot.biz.item.vo.request.MesItemUsedUploadRequest;
import cn.jb.boot.biz.item.vo.response.MesProcedureUploadResponse;
import cn.jb.boot.biz.tray.mapper.TrayManageInfoMapper;
import cn.jb.boot.framework.com.response.RequestResponseTool;
import cn.jb.boot.util.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mortbay.util.ajax.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AGV叉车信息
 *
 * @author shihy
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-04-08 20:04:08
 */
@Service
public class AgvManageInfoServiceImpl extends ServiceImpl<AgvManageInfoMapper, AgvManageInfo> implements AgvManageInfoService {

    @Autowired
    AgvManageInfoMapper mapper;
    @Autowired
    DeviceLocationService dlsevice;
    @Autowired
    TrayManageInfoMapper traymapper;
    @Autowired
    BomManageInfoMapper bomManageInfoMapper;
    @Autowired
    MesItemStockMapper mesItemStockMapper;
    @Autowired
    DeviceLocationMapper deviceLocationMapper;
    @Autowired
    DeviceLocationService deviceLocationService;
    @Resource
    private MesItemStockService mesItemStockService;
    @Resource
    private MesItemUseService mesItemUseService;
    @Resource
    private MesProcedureService mesProcedureService;


    @Resource
    private MesItemUseServiceImpl mesItemUseServiceImpl;
    @Resource
    private MesProcedureServiceImpl mesProcedureServiceImpl;


//    把业务参数中的起点/终点转换成AGV实际的点位坐标和命令字，然后新增进库
    @Override
    public void createInfo(AgvManageInfoPageRequest params) {
        //查询起始点和结束点
        List<Map<String, Object>> beginAndEndLocationList = mapper.location();
        //获取起始点名称和结束点AGV点位名称
        Map<String, Object> mapparam = new HashMap<>();
        String beginStation = params.getBegin();
        String beginLocation = params.getKuwei();
        mapparam.put("Station", beginStation);
        mapparam.put("Location", beginLocation);
        Map<String, Object> agvLocation = dlsevice.selectAGVLocation(mapparam);
        for (Map<String, Object> beginAndEndL : beginAndEndLocationList) {
            String beLocation = agvLocation.get("agvlocation").toString();
            if (beginAndEndL.get("name").toString().equals(beLocation)) {
                params.setBegin(beginAndEndL.get("position").toString());
                break;
            }
        }
        String endStation = params.getEnd();
        String endLocation = params.getLocation();
        mapparam.put("Station", endStation);
        mapparam.put("Location", endLocation);
        Map<String, Object> agvendLocation = dlsevice.selectAGVLocation(mapparam);
        for (Map<String, Object> beginAndEndL : beginAndEndLocationList) {
            String enLocation = agvendLocation.get("agvlocation").toString();
            if (beginAndEndL.get("name").toString().equals(enLocation)) {
                params.setEnd(beginAndEndL.get("position").toString());
                break;
            }
        }
        //查询不同工位的起始命令字和结束命令字
        List<Map<String, Object>> locationgList = mapper.locationCmd();
        for (Map<String, Object> location : locationgList) {
            String pickdown = location.get("pickdown").toString();
            String pickup = location.get("pickup").toString();
            String name = location.get("name").toString();
            if (agvLocation.get("agvlocation").toString().equals(name)) {
                params.setBeginCmd(pickup);
            }
            if (agvendLocation.get("agvlocation").toString().equals(name)) {
                params.setEndCmd(pickdown);
            }
        }
        mapper.add(params);
    }

    @Override
    public List<AgvManageInfoPageResponse> usingCarInfo() {
        return mapper.usingCar();
    }

    @Override
    public String getStation(Map<String, Object> params) {
        String result = "";
        try {
            if (params.containsKey("params")) {
                params = (Map) params.get("params");
                if (params.containsKey("end") && !params.get("end").toString().isEmpty()) {
                    params.put("station", params.get("end"));
                } else if (params.containsKey("begin")) {
                    params.put("station", params.get("begin"));
                }
            }
            List<Map<String, Object>> stationList = traymapper.getStation(params);
            result = JSON.toString(stationList);
            result = RequestResponseTool.getJsonMessage("00", "处理成功", result);
        } catch (Exception e) {
            RequestResponseTool.getJsonMessage("00", "处理失败");
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 从 ERP/BOM 系统同步物料基础数据到 mes_item_stock 表中  ?    错误的旧版本
     * @return
     */
    public List<Map<String, Object>> bomInfo_err() {
        // 1. 清空原有数据（四张表）
        mesItemStockMapper.deleteBomData();
        // 2. 从 ERP 获取原始物料数据
        List<Map<String, Object>> jspMaterialList = bomManageInfoMapper.materialMessage();
        // 3. 遍历 ERP 数据，构造物料对象写入 mes_item_stock
        for(Map<String,Object> jspMaterialMap:jspMaterialList){
            Map<String,Object> itemStock = new HashMap<>();
            //获取32位随机数作为主键
            UUID uuid = UUID.randomUUID();
            String randomCode = uuid.toString().replace("-", "");
            itemStock.put("id",randomCode.substring(0, 32));
            //物品编码
            String strItemCode = jspMaterialMap.get("STRITEMCODE").toString();
            itemStock.put("itemNo",strItemCode);
            //物品名称
            String strItemName = jspMaterialMap.get("STRITEMNAME").toString();
            itemStock.put("itemName",strItemName);
            //规格型号
            String strItemStyle = jspMaterialMap.get("STRITEMSTYLE") == null?"":jspMaterialMap.get("STRITEMSTYLE").toString();
            itemStock.put("itemModel",strItemStyle);
            //图纸号
            String strBomCode = "";
            //计量单位
            String strUnitName = jspMaterialMap.get("STRUNITNAME").toString();
            itemStock.put("itemMeasure",strUnitName);
            //数量
            String strCount = "0";
            itemStock.put("itemCount",strCount);
            itemStock.put("erpCount",strCount);
            //来源（自制或采购）
            String strFrom = "采购";
            itemStock.put("itemOrigin",strFrom);
            //辅助数量
            String strDBCount = "0";
            itemStock.put("itemCountAssist",strDBCount);
            if(jspMaterialMap.get("STRITEMTYPENAME").equals("辅助材料") || jspMaterialMap.get("STRITEMTYPENAME").equals("主要材料")){
                itemStock.put("itemType","00");
                itemStock.put("bomNo",strBomCode);
            }else if(jspMaterialMap.get("STRITEMTYPENAME").equals("半成品") || jspMaterialMap.get("STRITEMTYPENAME").equals("产成品")){
                itemStock.put("itemType","01");
                itemStock.put("bomNo",strItemStyle);
            }else{
                continue;
            }
            //辅助计量单位
            String strdbUnit = jspMaterialMap.get("STRUNITNAME").toString();
            itemStock.put("itemMeasureAssist",strdbUnit);
            itemStock.put("isValid","01");
            itemStock.put("createdBy","1");
            itemStock.put("createdTime", DateUtil.nowDateTime());
            itemStock.put("updatedBy","1");
            itemStock.put("updatedTime",DateUtil.nowDateTime());
            mesItemStockMapper.insertItemStock(itemStock); // 插入单条数据
        }

        //bom基础数据
        List<Map<String, Object>> jspBomList = bomManageInfoMapper.bomMessage();
        List<String> bomId = new ArrayList<>();
        List<MesItemUsedUploadRequest> list = new ArrayList<>();
        List<Map<String,Object>> itemList = new ArrayList<>();

        //用料
        for(Map<String, Object> jspBom:jspBomList){
            String strItemCode = jspBom.get("STRITEMCODE").toString();
            String strNexItemCode = jspBom.get("STRNEXTITEMCODE").toString();
            boolean check = false;
            for(Map<String,Object> itemMap:itemList){
                String itemCode = itemMap.get("strItemCode").toString();
                String nesItemCode = itemMap.get("strNexItemCode").toString();
                if(strItemCode.equals(itemCode) && strNexItemCode.equals(nesItemCode)){
                    check = true;
                    break;
                }
            }
            if(check){
                continue;
            }else{
                Map<String,Object> itemMap = new HashMap<>();
                itemMap.put("strItemCode",strItemCode);
                itemMap.put("strNexItemCode",strNexItemCode);
                itemList.add(itemMap);
            }
            MesItemUsedUploadRequest usedItem = new MesItemUsedUploadRequest();
            String strBomCode = jspBom.get("STRBOMCODE").toString();
            usedItem.setBomNo(strBomCode);
            usedItem.setUseItemNo(strNexItemCode);
            String strItemName = jspBom.get("STRNEXTITEMNAME").toString();
            usedItem.setUseItemName(strItemName);
            String strCount = jspBom.get("DBLQUANTITY").toString();
            BigDecimal strCountBig = new BigDecimal(strCount);
            usedItem.setFixedUse(strCountBig);
            usedItem.setVariUse(strCountBig);
            usedItem.setFixedUseAssist(new BigDecimal("1"));
            usedItem.setVariUseAssist(new BigDecimal("1"));
            String strUnitName = jspBom.get("STRUNITNAME").toString();
            usedItem.setItemMeasureAssist(strUnitName);
            list.add(usedItem);
        }
        mesItemUseServiceImpl.insertItemUsed(list);
        //工序数据
        List<Map<String, Object>> jspBomRouterList = bomManageInfoMapper.bomRouter();
        List<MesProcedureUploadResponse> stockList = new ArrayList<>();
        for(Map<String, Object> jspBomRouter:jspBomRouterList){
            MesProcedureUploadResponse procedureItem = new MesProcedureUploadResponse();
            List<String> param = new ArrayList<>();
            param.add(jspBomRouter.get("STRBOMCODE").toString());
            Map<String, MesItemStock> MesItemStockMap = mesItemStockMapper.getByBomNos(param);
            MesItemStock MesItemStock = MesItemStockMap.get(jspBomRouter.get("STRBOMCODE").toString());
            procedureItem.setBomNo(jspBomRouter.get("STRBOMCODE").toString());
            procedureItem.setSeqNo(Integer.valueOf(jspBomRouter.get("LNGORDER").toString()));
            procedureItem.setProcedureCode(jspBomRouter.get("STRROUTECODE").toString());
            procedureItem.setProcedureName(jspBomRouter.get("STRROUTENAME").toString());
            procedureItem.setDeviceName(getDeviceName(jspBomRouter.get("STRROUTECODE").toString()));
            procedureItem.setWorkShopName(jspBomRouter.get("STRDEPARTMENTNAME").toString());
            String outerationTime = jspBomRouter.get("DBLROUTERATIONTIME") == null?"0":jspBomRouter.get("DBLROUTERATIONTIME").toString();
            procedureItem.setHoursFixed(new BigDecimal(outerationTime));
            procedureItem.setHoursWork(new BigDecimal(outerationTime));
//            String outeProcessTime = jspBomRouter.get("DBLROUTEPROCESSTIME") == null?"0":jspBomRouter.get("DBLROUTEPROCESSTIME").toString();
//            procedureItem.setHoursWork(new BigDecimal(outeProcessTime));
            String dblRoutePrepareTime = jspBomRouter.get("DBLROUTEPREPARETIME") == null?"0":jspBomRouter.get("DBLROUTEPREPARETIME").toString();
            procedureItem.setHoursPrepare(new BigDecimal(dblRoutePrepareTime));
            if(MesItemStock != null && MesItemStock.getItemNo() != null){
                procedureItem.setItemNo(MesItemStock.getItemNo());
            }
            stockList.add(procedureItem);
        }
        mesProcedureServiceImpl.insertItemProcedure(stockList);
        return jspMaterialList;
    }



    /**
     * 从 ERP/BOM 系统同步物料基础数据到 mes_item_stock 表中  ?    错误的旧版本
     * @return
     */    @Override
    public List<Map<String, Object>> bomInfo() {
        List<Map<String, Object>> jspMaterialList = new ArrayList<>();
        try{
            System.out.println("开始同步物料档案");
            //物料基础数据
            jspMaterialList = bomManageInfoMapper.materialMessage();
            List<MesItemStock> mesItemStockList = new ArrayList<>();
            List<String> itemLists = new ArrayList<>();
            for(Map<String,Object> jspMaterialMap:jspMaterialList){
                MesItemStock itemStock = new MesItemStock();
                //物品编码
                String strItemCode = jspMaterialMap.get("STRITEMCODE").toString();
                itemStock.setItemNo(strItemCode);
                itemLists.add(strItemCode);
                //主键
                List<String> itemNos = new ArrayList<>();
                itemNos.add(strItemCode);
                Map<String,MesItemStock> mesItemStockLists = mesItemStockService.getByItemNos(itemNos);
                if(mesItemStockLists != null && mesItemStockLists.size() != 0){
                    MesItemStock mesItemStock = mesItemStockLists.get(strItemCode);
                    itemStock.setId(mesItemStock.getId());
                }else{
                    //获取32位随机数作为主键
                    UUID uuid = UUID.randomUUID();
                    String randomCode = uuid.toString().replace("-", "");
                    itemStock.setId(randomCode.substring(0, 32));
                }
                //物品名称
                String strItemName = jspMaterialMap.get("STRITEMNAME").toString();
                itemStock.setItemName(strItemName);
                //规格型号
                String strItemStyle = jspMaterialMap.get("STRITEMSTYLE") == null?"":jspMaterialMap.get("STRITEMSTYLE").toString();
                itemStock.setItemModel(strItemStyle);
                //计量单位
                String strUnitName = jspMaterialMap.get("STRUNITNAME").toString();
                itemStock.setItemMeasure(strUnitName);
                //数量
                BigDecimal strCount = new BigDecimal(0);
                itemStock.setItemCount(strCount);
                itemStock.setErpCount(strCount);
                //来源（自制或采购）
                String strFrom = jspMaterialMap.get("BYTSOURCE").toString();
                itemStock.setItemOrigin(strFrom);
                if(strFrom.equals("0")){
                    itemStock.setItemType("00");
                    itemStock.setBomNo("");
                }else{
                    itemStock.setItemType("01");
                    itemStock.setBomNo(strItemStyle);
                }
                //辅助数量
                BigDecimal strDBCount = new BigDecimal(0);
                itemStock.setItemCountAssist(strDBCount);
                //辅助计量单位
                String strdbUnit = jspMaterialMap.get("STRUNITNAME").toString();
                itemStock.setItemMeasureAssist(strdbUnit);
                itemStock.setIsValid("01");
                itemStock.setCreatedBy("1");
                itemStock.setCreatedTime(DateUtil.nowDateTime());
                itemStock.setUpdatedBy("1");
                itemStock.setUpdatedTime(DateUtil.nowDateTime());
                mesItemStockList.add(itemStock);
            }
            if(mesItemStockList != null && mesItemStockList.size() != 0){
                mesItemStockService.saveOrUpdateBatch(mesItemStockList);
            }
            Integer times = itemLists.size()/1000;
            if(itemLists.size()%1000 != 0){
                times = times +1;
            }
            for(int i=1;i<=times;i++){
                List<String> datas = new ArrayList<>();
                if(i == times){
                    datas = itemLists.subList((i-1)*1000,itemLists.size());
                }else{
                    datas = itemLists.subList((i-1)*1000,i*1000);
                }
                if(datas.size() != 0){
                    bomManageInfoMapper.materUpdate(datas);
                }
            }




            System.out.println("开始同步用料数据");
            //用料数据
            List<Map<String, Object>> jspBomList = bomManageInfoMapper.bomMessage();
            List<MesItemUse> list = new ArrayList<>();
            //用料
            List<Integer> bomIdList = new ArrayList<>();
            for(Map<String, Object> jspBom:jspBomList){
                bomIdList.add(Integer.valueOf(jspBom.get("LNGBOMID").toString()));
                MesItemUse mesItemUse = new MesItemUse();
                //产品编码
                String strItemCode = jspBom.get("STRITEMCODE").toString();
                mesItemUse.setItemNo(strItemCode);
                //用料编码
                String strNexItemCode = jspBom.get("STRNEXTITEMCODE").toString();
                mesItemUse.setUseItemNo(strNexItemCode);
                List<MesItemUse> mesItemUseList = mesItemUseService.list(new LambdaQueryWrapper<MesItemUse>().eq(MesItemUse::getItemNo, strItemCode).eq(MesItemUse::getUseItemNo,strNexItemCode));
                if(mesItemUseList != null && mesItemUseList.size() != 0){
                    mesItemUse.setId(mesItemUseList.get(0).getId());
                }else{
                    //获取32位随机数作为主键
                    UUID uuid = UUID.randomUUID();
                    String randomCode = uuid.toString().replace("-", "");
                    mesItemUse.setId(randomCode.substring(0, 32));
                }
                //用料量
                String strCount = jspBom.get("DBLQUANTITY").toString();
                BigDecimal strCountBig = new BigDecimal(strCount);
                mesItemUse.setUseItemCount(strCountBig);
                //变动用量
                mesItemUse.setVariUse(new BigDecimal(0));
                //固定用量
                mesItemUse.setFixedUse(strCountBig);
                //useItemMeasure
                String useItemMeasure = jspBom.get("STRNEXTITEMUNIT").toString();
                mesItemUse.setUseItemMeasure(useItemMeasure);
                //辅助计量单位
                String useItemMeasureAssist = jspBom.get("STRUNITNAMEAUX").toString();
                mesItemUse.setItemMeasureAssist(useItemMeasureAssist);
                //辅助固定用量
                String fixUseAssist = jspBom.get("DBLQUANTITYAUX").toString();
                mesItemUse.setFixedUseAssist(new BigDecimal(fixUseAssist));
                //variUseAssist
                mesItemUse.setVariUseAssist(new BigDecimal(0));
                String itemStyle = jspBom.get("BYTITEMSOURCE").toString();
                if(itemStyle.equals("0")){
                    mesItemUse.setUseItemType("00");
                }else{
                    mesItemUse.setUseItemType("01");
                }
                list.add(mesItemUse);
            }
            if(list != null && list.size() != 0){
                mesItemUseService.saveOrUpdateBatch(list);
            }
            Integer timeBoms = bomIdList.size()/1000;
            if(bomIdList.size()%1000 != 0){
                timeBoms = timeBoms +1;
            }
            for(int i=1;i<=timeBoms;i++){
                List<Integer> datas = new ArrayList<>();
                if(i== timeBoms){
                    datas =bomIdList.subList((i-1)*1000,bomIdList.size());
                }else{
                    datas = bomIdList.subList((i-1)*1000,i*1000);
                }
                if(datas.size() != 0){
                    bomManageInfoMapper.bomUpdate(datas);
                }
            }
            Set<String> items = list.stream().filter(d -> ItemType.isMaterials(d.getUseItemType())).map(MesItemUse::getUseItemNo).collect(Collectors.toSet());
            mesItemUseService.useDateFromErp(items);

            //工序数据
            System.out.println("开始同步工序数据");
            List<Map<String, Object>> jspBomRouterList = bomManageInfoMapper.bomRouter();
            List<MesProcedure> stockList = new ArrayList<>();
            List<Integer> routerIdList = new ArrayList<>();
            for(Map<String, Object> jspBomRouter:jspBomRouterList){
                routerIdList.add(Integer.valueOf(jspBomRouter.get("LNGBOMID").toString()));
                MesProcedure procedureItem = new MesProcedure();
                List<String> param = new ArrayList<>();
                param.add(jspBomRouter.get("STRBOMCODE").toString());
                Map<String, MesItemStock> MesItemStockMap = mesItemStockMapper.getByBomNos(param);
                //产品编码
                MesItemStock mesItemStock = MesItemStockMap.get(jspBomRouter.get("STRBOMCODE").toString());
                if(mesItemStock == null || mesItemStock.getItemNo() == null || mesItemStock.getItemNo().isEmpty()){
                    continue;
                }
                procedureItem.setItemNo(mesItemStock.getItemNo());
                //序号
                procedureItem.setSeqNo(Integer.valueOf(jspBomRouter.get("LNGORDER").toString()));
                //工序编码
                procedureItem.setProcedureCode(jspBomRouter.get("STRROUTECODE").toString());
                List<MesProcedure> mesProcedureList = mesProcedureService.list(new LambdaQueryWrapper<MesProcedure>().eq(MesProcedure::getItemNo, jspBomRouter.get("STRBOMCODE").toString()).eq(MesProcedure::getProcedureCode,jspBomRouter.get("STRROUTECODE").toString()));
                if(mesProcedureList != null && mesProcedureList.size() !=0){
                    procedureItem.setId(mesProcedureList.get(0).getId());
                }else{
                    UUID uuid = UUID.randomUUID();
                    String randomCode = uuid.toString().replace("-", "");
                    procedureItem.setId(randomCode.substring(0, 32));
                }
                //工序名称
                procedureItem.setProcedureName(jspBomRouter.get("STRROUTENAME").toString());
                //加工工时、hoursFixed
                String outerationTime = jspBomRouter.get("DBLROUTERATIONTIME") == null?"0":jspBomRouter.get("DBLROUTERATIONTIME").toString();
                procedureItem.setHoursFixed(new BigDecimal(outerationTime));
                procedureItem.setHoursWork(new BigDecimal(outerationTime));
                //准备工时
                String dblRoutePrepareTime = jspBomRouter.get("DBLROUTEPREPARETIME") == null?"0":jspBomRouter.get("DBLROUTEPREPARETIME").toString();
                procedureItem.setHoursPrepare(new BigDecimal(dblRoutePrepareTime));
                //工作车间Id
                String deptName = jspBomRouter.get("STRDEPARTMENTNAME") == null?"":jspBomRouter.get("STRDEPARTMENTNAME").toString();
                if(deptName.equals("制造部")){
                    procedureItem.setDeptId("312905765054574592");
                }else{
                    procedureItem.setDeptId("316142126431625216");
                }
                //设备Id
                String deviceId = jspBomRouter.get("STRWORKCENTERNAME") == null?"":jspBomRouter.get("STRWORKCENTERNAME").toString();
                procedureItem.setDeviceId(getDeviceName(jspBomRouter.get("STRROUTECODE").toString()));
                stockList.add(procedureItem);
            }
            if(stockList != null && stockList.size() !=0){
                mesProcedureService.saveOrUpdateBatch(stockList);
            }
            Integer timeRouters = routerIdList.size()/1000;
            if(routerIdList.size()%1000 != 0){
                timeRouters = timeRouters +1;
            }
            for(int i=1;i<=timeRouters;i++){
                List<Integer> datas = new ArrayList<>();
                if(i==timeRouters){
                    datas = routerIdList.subList((i-1)*1000,routerIdList.size());
                }else{
                    datas = routerIdList.subList((i-1)*1000,i*1000);
                }
                if(datas.size() != 0){
                    bomManageInfoMapper.routerUpdate(datas);
                }
            }
            System.out.println("同步结束");
        }catch (Exception e){
            e.printStackTrace();
        }
        return jspMaterialList;
    }



    @Override
    public String getTaskOfWeek() {
        String result = "";
        try {
            Map<String,Object> taskMap = new HashMap<>();
            List<Map<String, Object>> taskList = mapper.getTaskOfWeek();
            List<AgvManageInfoPageResponse> usingTask= mapper.usingCar();
            String[] datas =new String[taskList.size()];
            String[] dates =new String[taskList.size()];
            for(int i = 0;i < taskList.size();i++){
                datas[i] = taskList.get(i).get("count").toString();
                dates[i] = taskList.get(i).get("time").toString();
            }
            List<Map<String,Object>> loactionList = mapper.location();
            Map<String, Object> loactionMap = new HashMap<>();
            for (Map<String, Object> loaction : loactionList) {
                loactionMap.put(loaction.get("position").toString(), loaction);
            }
            //库位数据
            List<DeviceLocationInfo> deviceLocationList = deviceLocationMapper.selectList(new LambdaQueryWrapper<>());
            Map<String, Object> eviceLocationMap = new HashMap<>();
            for(DeviceLocationInfo deviceLocation:deviceLocationList){
                eviceLocationMap.put(deviceLocation.getAgvlocation(), deviceLocation);
            }
            List<Map<String,Object>> usTaskList = new ArrayList<>();
            for(AgvManageInfoPageResponse task:usingTask){
                Map<String,Object> usTask = new HashMap<>();
                usTask.put("agvName",task.getAgvName());
                Map<String, Object> loactions = (Map)loactionMap.get(task.getBegin().replace("-","")); //xinzne
                DeviceLocationInfo deviceLocations = (DeviceLocationInfo)eviceLocationMap.get(loactions.get("name"));
                if(deviceLocations == null){ //后增
                    continue;
                }
                usTask.put("begin",deviceLocations.getStation());
                usTask.put("beginLocation",deviceLocations.getLocation());
                Map<String, Object> loactionEnd = (Map)loactionMap.get(task.getEnd().replace("-","")); //新增
                DeviceLocationInfo deviceLocationEnd = (DeviceLocationInfo)eviceLocationMap.get(loactionEnd.get("name"));
                usTask.put("end",deviceLocationEnd.getStation());
                usTask.put("endLocation",deviceLocationEnd.getLocation());
                usTaskList.add(usTask);
            }
            taskMap.put("count",usingTask.size());
            taskMap.put("using",usTaskList);
            taskMap.put("taskCout",datas);
            taskMap.put("taskDate",dates);
            result = JSON.toString(taskMap);
            result = RequestResponseTool.getJsonMessage("00", "处理成功", result);
        } catch (Exception e) {
            RequestResponseTool.getJsonMessage("00", "处理失败");
            e.printStackTrace();
        }
        return result;
    }
    private String getDeviceName(String procedureCode){
        String resultValue = "";
        switch(procedureCode){
            case "1":
                resultValue = "全自动机器人智能分拣激光切割单元（激光切割三单元）";
                break;
            case "2":
                resultValue = "数控折弯机(ZY-ZN-022)";
                break;
            case "3":
                resultValue = "激光切管机(ZY-ZN-028)";
                break;
            case "4":
            case "9":
                resultValue = "打磨机(ZY-ZN-053)";
                break;
            case "5":
                resultValue = "调平机(ZY-ZN-032)";
                break;
            case "6":
                resultValue = "摇臂钻床(ZY-ZN-046)";
                break;
            case "7":
                resultValue = "摇臂钻床(ZY-ZN-046)";
                break;
            case "10":
            case "11":
            case "12":
            case "20":
            case "21":
            case "33":
            case "34":
            case "37":
            case "45":
            case "52":
                resultValue = "二保焊机";
                break;
            case "14":
            case "15":
                resultValue = "立式加工中心(ZY-ZN-025)";
                break;
            case "16":
                resultValue = "喷砂机";
                break;
            case "17":
                resultValue = "人工打磨";
                break;
            case "18":
                resultValue = "摇臂钻床(ZY-ZN-046)";
                break;
            case "19":
                resultValue = "调平机(ZY-ZN-032)";
                break;
            case "22":
                resultValue = "人工清渣修磨";
                break;
            case "23":
                resultValue = "人工调整检验";
                break;
            case "24":
                resultValue = "叉车转运";
                break;
            case "25":
                resultValue = "总装吊装及挂件";
                break;
            case "32":
                resultValue = "立式加工中心(ZY-ZN-025)";
                break;
            case "36":
                resultValue = "摇臂钻床(ZY-ZN-046)";
                break;
            case "38":
                resultValue = "数控火焰切割机(ZY-ZN-019)";
                break;
            case "43":
                resultValue = "数控火焰切割机(ZY-ZN-019)";
                break;
            case "44":
            case "47":
            case "48":
                resultValue = "电动攻丝机(ZY-ZN-083)";
                break;
            case "49":
                resultValue = "全自动机器人智能分拣激光切割单元（激光切割三单元）";
                break;
            case "50":
                resultValue = "电动攻丝机(ZY-ZN-083)";
                break;
            case "51":
                resultValue = "全自动机器人智能分拣激光切割单元（激光切割三单元）";
                break;
            case "55":
                resultValue = "摇臂钻床(ZY-ZN-046)";
                break;
            case "56":
                resultValue = "数控折弯机(ZY-ZN-022)";
                break;
            case "59":
                resultValue = "摇臂钻床(ZY-ZN-046)";
                break;
            case "61":
                resultValue = "二保焊机";
                break;
            case "64":
                resultValue = "激光切管机(ZY-ZN-028)";
                break;
            case "65":
                resultValue = "机器人焊接";
                break;
            case "66":
                resultValue = "围弯机";
                break;
            case "67":
            case "69":
                resultValue = "全自动机器人智能分拣激光切割单元（激光切割一单元）";
                break;
            case "72":
                resultValue = "打磨机(ZY-ZN-053)";
                break;
            case "73":
            case "74":
                resultValue = "围弯机";
                break;
            case "80":
                resultValue = "数控折弯机(ZY-ZN-022)";
                break;
            case "84":
                resultValue = "二保焊机";
                break;
            case "87":
                resultValue = "全自动机器人智能分拣激光切割单元（激光切割三单元）";
                break;
            case "90":
                resultValue = "四柱液压机(ZY-ZN-076)";
                break;
            case "96":
                resultValue = "电动攻丝机(ZY-ZN-083)";
                break;
            case "97":
                resultValue = "二保焊机";
                break;
            case "98":
            case "99":
                resultValue = "涂装线";
                break;
            case "100":
                resultValue = "打标机(ZY-ZN-077)";
                break;
            case "104":
                resultValue = "立式加工中心(ZY-ZN-025)";
                break;
            case "105":
                resultValue = "电动攻丝机(ZY-ZN-083)";
                break;
            case "106":
            case "107":
            case "108":
            case "109":
                resultValue = "二保焊机";
                break;
            case "110":
            case "111":
            case "122":
                resultValue = "四柱液压机(ZY-ZN-076)";
                break;
            case "113":
            case "114":
            case "115":
                resultValue = "涂装线";
                break;
            case "119":
            case "120":
            case "121":
                resultValue = "二保焊机";
                break;
            case "124":
            case "127":
            case "128":
            case "129":
            case "131":
            case "132":
            case "134":
            case "135":
            case "136":
            case "138":
                resultValue = "电动攻丝机(ZY-ZN-083)";
                break;
            case "125":
            case "126":
            case "130":
            case "133":
            case "137":
            case "140":
            case "141":
            case "142":
            case "144":
            case "145":
            case "146":
            case "149":
            case "150":
            case "159":
            case "161":
            case "163":
            case "164":
            case "168":
            case "171":
            case "173":
            case "174":
            case "180":
                resultValue = "二保焊机";
                break;
            case "139":
            case "148":
            case "151":
            case "154":
            case "155":
                resultValue = "摇臂钻床(ZY-ZN-046)";
                break;
            case "152":
            case "167":
            case "182":
                resultValue = "龙门铣床(ZY-ZN-027)";
                break;
            case "157":
                resultValue = "铆钉枪";
                break;
            case "178":
            case "179":
                resultValue = "立式加工中心(ZY-ZN-025)";
                break;
            case "26":
            case "27":
            case "28":
            case "29":
            case "30":
            case "31":
            case "35":
            case "39":
            case "40":
            case "41":
            case "53":
            case "54":
            case "60":
            case "63":
            case "102":
            case "112":
            case "116":
            case "117":
            case "118":
            case "123":
            case "143":
            case "165":
            case "166":
            case "169":
            case "170":
            case "175":
                resultValue = "人工";
        }
        return resultValue;
    }

    @Override
    public List<Map<String,Object>>  setKukaStatus() {
        List<Map<String,Object>> stationList = new ArrayList<>();
        try {
            List<Map<String,Object>> locationList = deviceLocationMapper.selectLocationStatus();
            for(Map<String,Object> locationMap:locationList){
                Map<String, Object> station = new HashMap<>();
                String location = locationMap.get("agvlocation").toString();
                if(location.equals("T3-2")){
                    location = "1";
                }else{
                    location = "2";
                }
                String status = locationMap.get("status").toString();
                if(status.equals("0")){
                    status = "1";
                }else{
                    status = "0";
                }
                station.put("location",location);
                station.put("status",status);
                stationList.add(station);
            }
        } catch (Exception e) {
            RequestResponseTool.getJsonMessage("00", "处理失败");
            e.printStackTrace();
        }
        return stationList;
    }
    @Override
    public String getKukaStatus(Map<String, Object> params) {
        System.out.println("------接收消息为：" + params + "--------------------------");
        String result = "";
        try {
            List<AgvManageInfoPageResponse> usingAgvList = usingCarInfo();
            if(params.get("status").equals("1")){
                //托盘货物已满，呼叫AGV叉车
                String location = params.get("location").toString();
                if(location.equals("1")){
                    //如果有这条任务则不再新建
                    boolean check = true;
                    for(AgvManageInfoPageResponse usingAgv:usingAgvList){
                        if(usingAgv.getBegin().equals("73") || usingAgv.getEnd().equals("73")){
                            check = false;
                        }
                    }
                    if(check){
                        Map<String, Object> param = new HashMap<>();
                        param.put("status","1");
                        param.put("agvlocation","T3-2");
                        deviceLocationMapper.updateEmportLocation(param);
                        AgvManageInfoPageRequest agvinfo = new AgvManageInfoPageRequest();
                        String agvName = "Fork_139";
                        agvinfo.setAgvName(agvName);
                        //库位
                        agvinfo.setBeginCmd("取货");
                        agvinfo.setBegin("桥壳取料（无托盘）");
                        agvinfo.setKuwei("取料1");
                        agvinfo.setEndCmd("放货");
                        //判断放料区是否无货
                        List<String> locationList = new ArrayList<>();
                        locationList.add("X11");
                        locationList.add("X12");
                        locationList.add("X13");
                        locationList.add("X14");
                        locationList.add("X15");
                        List<Map<String, Object>> emptyLocationList = deviceLocationMapper.getEmportLocation1(locationList);
                        if(emptyLocationList != null && emptyLocationList.size() != 0){
                            agvinfo.setEnd(emptyLocationList.get(0).get("station").toString());
                            agvinfo.setLocation(emptyLocationList.get(0).get("location").toString());
                        }else{
                            Thread.sleep(5000);
                            getKukaStatus(params);
                        }
                        createInfo(agvinfo);
                        param.put("status","0");
                        param.put("agvlocation","T3-2");
                        param.put("end","73");
                        isCompleted(param);
                    }
                }else{
                    //2号取料区托盘已满
                    boolean check = true;
                    for(AgvManageInfoPageResponse usingAgv:usingAgvList){
                        if(usingAgv.getBegin().equals("214") || usingAgv.getEnd().equals("214")){
                            check = false;
                        }
                    }
                    if(check){
                        String empptyBegin = "";
                        Map<String, Object> param = new HashMap<>();
                        param.put("status","1");
                        param.put("agvlocation","Q");
                        deviceLocationMapper.updateEmportLocation(param);
                        AgvManageInfoPageRequest agvinfo = new AgvManageInfoPageRequest();
                        String agvName = "Fork_139";
                        agvinfo.setAgvName(agvName);
                        //库位
                        agvinfo.setBeginCmd("取货");
                        agvinfo.setBegin("桥壳取料");
                        agvinfo.setKuwei("取料2");
                        agvinfo.setEndCmd("放货");
                        //判断放料区是否无货
                        List<String> locationList = new ArrayList<>();
                        locationList.add("X1");
                        locationList.add("X2");
                        locationList.add("X3");
                        locationList.add("X32");
                        List<Map<String, Object>> emptyLocationList = deviceLocationMapper.getEmportLocation2(locationList);
                        if(emptyLocationList != null && emptyLocationList.size() != 0){
                            agvinfo.setEnd(emptyLocationList.get(0).get("station").toString());
                            agvinfo.setLocation(emptyLocationList.get(0).get("location").toString());
                            if("X1".equals(emptyLocationList.get(0).get("agvlocation"))){
                                empptyBegin = "X2";
                            }else if("X2".equals(emptyLocationList.get(0).get("agvlocation"))){
                                empptyBegin = "X3";
                            }else if("X3".equals(emptyLocationList.get(0).get("agvlocation"))){
                                empptyBegin = "X32";
                            }else if("X32".equals(emptyLocationList.get(0).get("agvlocation"))){
                                empptyBegin = "X1";
                            }
                        }else{
                            Thread.sleep(5000);
                            getKukaStatus(params);
                        }
                        createInfo(agvinfo);
                        //设置库点为入料
                        Map<String,Object> outParam = new HashMap<>();
                        outParam.put("agvlocation",emptyLocationList.get(0).get("agvlocation").toString());
                        outParam.put("status","0");
                        deviceLocationMapper.updateEmportLocation(outParam);

                        //将空托盘放到知起始点
                        Thread.sleep(5000);
                        DeviceLocationInfo deviceLocation = deviceLocationService.getOne(new LambdaQueryWrapper<DeviceLocationInfo>().eq(DeviceLocationInfo::getAgvlocation, empptyBegin));
                        AgvManageInfoPageRequest emptyTray = new AgvManageInfoPageRequest();
                        emptyTray.setAgvName("Fork_139");
                        emptyTray.setBeginCmd("取货");
                        emptyTray.setBegin(deviceLocation.getStation());
                        emptyTray.setKuwei(deviceLocation.getLocation());
                        emptyTray.setEndCmd("放货");
                        emptyTray.setEnd("桥壳取料");
                        emptyTray.setLocation("取料2");
                        createInfo(emptyTray);
                        //入料
                        Map<String,Object> inParam = new HashMap<>();
                        inParam.put("agvlocation",deviceLocation.getAgvlocation());
                        inParam.put("status","1");
                        deviceLocationMapper.updateEmportLocation(inParam);

                        param.put("status","0");
                        param.put("end","214");
                        param.put("agvlocation","Q");
                        isCompleted(param);
                    }
                }
                result = RequestResponseTool.getJsonMessage("00", "处理成功", "");
            }
        } catch (Exception e) {
            RequestResponseTool.getJsonMessage("00", "处理失败");
            e.printStackTrace();
        }
        return result;
    }
    public void isCompleted(Map<String, Object> param){
        try{
            List<AgvManageInfoPageResponse> usingAgvList = usingCarInfo();
            boolean check = false;
            //如果包含当前任务则认为任务没结束
            for(AgvManageInfoPageResponse usingAgv:usingAgvList){
                if(usingAgv.getBegin().equals(param.get("end")) || usingAgv.getEnd().equals("end")){
                    check = true;
                }
            }
            if(!check) {
                deviceLocationMapper.updateEmportLocation(param);
            }else{
                Thread.sleep(5000);
                isCompleted(param);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
