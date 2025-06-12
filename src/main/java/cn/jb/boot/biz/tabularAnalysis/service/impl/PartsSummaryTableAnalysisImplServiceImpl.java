package cn.jb.boot.biz.tabularAnalysis.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.jb.boot.biz.depository.vo.request.OutStoreMidPageRequest;
import cn.jb.boot.biz.depository.vo.response.OutStoreMidPageResponse;
import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.enums.ItemType;
import cn.jb.boot.biz.item.vo.request.BomPageRequest;
import cn.jb.boot.biz.item.vo.request.MesItemUploadRequest;
import cn.jb.boot.biz.item.vo.response.MesItemStockPageResponse;
import cn.jb.boot.biz.tabularAnalysis.entity.Part;
import cn.jb.boot.biz.tabularAnalysis.mapper.PartsSummaryTableAnalysisMapper;
import cn.jb.boot.biz.tabularAnalysis.service.PartsSummaryTableAnalysisService;
import cn.jb.boot.biz.tabularAnalysis.vo.request.PartRequest;
import cn.jb.boot.biz.tabularAnalysis.vo.response.PartResponse;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.com.response.PagingResponse;
import cn.jb.boot.system.vo.DictDataVo;
import cn.jb.boot.util.*;
import cn.jb.boot.framework.common.utils.poi.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.x.protobuf.MysqlxCrud;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 托盘信息
 *
 * @author shihy
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-04-11 20:04:08
 */
@Service
public class PartsSummaryTableAnalysisImplServiceImpl extends ServiceImpl<PartsSummaryTableAnalysisMapper, Part> implements PartsSummaryTableAnalysisService {

    @Autowired
    PartsSummaryTableAnalysisMapper mapper;

    List<Part> allPart = new ArrayList<>();

    Map<String,Object> part = new HashMap<>();

//    String[] chineseNumbers = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十",
//            "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十"};
    String[] chineseNumbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",};

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void upload(HttpServletRequest request) {
        MultipartFile file = FileUtil.getFile(request);

        ExcelUtil<Part> util  = new ExcelUtil<Part>(Part.class);
        try {
            part = new HashMap<>();

            for (String s : chineseNumbers) {
                String sheetName = "排版"+s;
                List<Part> userList = util.importExcel(sheetName, file.getInputStream(),24);
                if (CollectionUtils.isEmpty(userList)) {continue;}
                List<Part> resUserList = new ArrayList<>();
                for (Part part : userList) {
                    if (ObjectUtil.isEmpty(part) || ObjectUtil.isEmpty(part.getPartName())) {continue;}
                    String input = part.getPartName();
                    int index = 0;
                    // 遍历直到遇到非数字或字符串结束
                    while (index < input.length() && Character.isDigit(input.charAt(index))) {
                        index++;
                    }
                    String result = input.substring(0, index);
                    part.setFigureNumber(result);

                    String number = part.getNumber();
                    String getNumber = number.split("/")[0].trim().replaceAll("[^0-9]", "");
                    part.setNumber(getNumber);
                    resUserList.add(part);

                }
                if (ObjectUtil.isNotEmpty(resUserList)) {
                    part.put(sheetName,resUserList);
                }
//                allPart = userList;
            }



        } catch (Exception e) {
                        e.printStackTrace();

//            throw new RuntimeException(e);
//            throw new RuntimeException("文件sheet不存在");
//            e.printStackTrace();
//            System.out.println("未找到排版");
        }
    }





    @Override
    public BaseResponse<List<PartResponse>> getListPage(Paging page, PartRequest params) {
        PageUtil<PartResponse, PartRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }
    @Override
    public List<Part> getList(){
        return allPart;
    }
    @Override
    public Map<String, Object> getMap(){
        return part;
    }
    @Override
    public void cleanList(){
        allPart = new ArrayList<>();
    }
    public Map<String, Object> statistics(){
        return mapper.statistics();
    }
    @Override
    public int install(List<Part> parts){
        int add = 0;
        int update = 0;
        for (Part part : parts) {
            // 根据图号判定是否有重复导入的工件  如果有则修改数据并累计数量
//            QueryWrapper<Part> objectQueryWrapper = new QueryWrapper<>();
//            objectQueryWrapper.eq("figure_number",part.getFigureNumber());
//            List<Part> partsByFig = mapper.selectList(objectQueryWrapper);
//            if (CollectionUtils.isNotEmpty(partsByFig)) {
//                Part part1 = partsByFig.get(0);
//                BigDecimal numberBigDecimal = new BigDecimal(part1.getNumber()).add(new BigDecimal(part.getNumber()));
//                part.setNumber(numberBigDecimal.toString());
//                update = mapper.update(part, objectQueryWrapper);
//            }else {
                add+=mapper.insert(part);
//            }
        }
        return add;
    }

    @Override
    public List<Map<String, Object>> getProcTodayDatas(){
        String nowDate = DateUtil.formatDateTime(DateUtil.nowDateTime(),"yyyy-MM-dd");
        List<Map<String, Object>> returnDataList = mapper.getProcTodayDatas(nowDate);
        List<Map<String, Object>> mergedList = new ArrayList<>();
        List<String> figureNumberList = new ArrayList<>();
        for(Map<String, Object> returnData:returnDataList){
            String figureNumber = returnData.get("figureNumber").toString();
            String updateTime = returnData.get("updatedTime").toString().replace("T"," ");
            String number = returnData.get("number1").toString().replace(" ","").replace("　","").replace("　","");
            if(figureNumberList ==null || !figureNumberList.contains(figureNumber)){
                figureNumberList.add(figureNumber);
                mergedList.add(returnData);
            }else{
                for(Map<String, Object> merged:mergedList){
                    String figureNumberNew = merged.get("figureNumber").toString();
                    String numberNew = merged.get("number1").toString().replace(" ","").replace("　","").replace("　","");
                    String updateTimeNew = merged.get("updatedTime").toString().replace("T"," ");
                    if(figureNumberNew.equals(figureNumber)){
                        merged.put("number1",Integer.valueOf(numberNew) + Integer.valueOf(number));
                    }
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime dateTime1 = LocalDateTime.parse(updateTime, formatter);
                    LocalDateTime dateTime2 = LocalDateTime.parse(updateTimeNew, formatter);

                    if (dateTime1.isBefore(dateTime2)) {
                        merged.put("updatedTime",updateTimeNew);
                    } else if (dateTime1.isAfter(dateTime2)) {
                        merged.put("updatedTime",updateTime);
                    } else {
                        merged.put("updatedTime",updateTimeNew);
                    }
                }
            }
        }
        return mergedList;
    }
}
