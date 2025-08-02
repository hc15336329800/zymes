package cn.jb.boot.biz.item.service.impl;

import cn.jb.boot.biz.device.entity.DeviceInfo;
import cn.jb.boot.biz.device.service.DeviceInfoService;
import cn.jb.boot.biz.item.dto.MesProcedureImportResult;
import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.entity.MesProcedure;
import cn.jb.boot.biz.item.mapper.MesProcedureMapper;
import cn.jb.boot.biz.item.service.MesItemStockService;
import cn.jb.boot.biz.item.service.MesProcedureService;
import cn.jb.boot.biz.item.vo.request.ItemNoRequest;
import cn.jb.boot.biz.item.vo.request.ItemProcedureRequest;
import cn.jb.boot.biz.item.vo.request.MesProcedureCreateRequest;
import cn.jb.boot.biz.item.vo.request.MesProcedurePageRequest;
import cn.jb.boot.biz.item.vo.request.MesProcedureUpdateRequest;
import cn.jb.boot.biz.item.vo.request.ShortCodeReq;
import cn.jb.boot.biz.item.vo.response.ItemProcedureResponse;
import cn.jb.boot.biz.item.vo.response.MesProcedureHeaderResponse;
import cn.jb.boot.biz.item.vo.response.MesProcedureInfoResponse;
import cn.jb.boot.biz.item.vo.response.MesProcedurePageResponse;
import cn.jb.boot.biz.item.vo.response.MesProcedureUploadResponse;
import cn.jb.boot.biz.item.vo.response.ProcListResp;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.exception.CavException;
import cn.jb.boot.system.vo.DictDataVo;
import cn.jb.boot.util.DictUtil;
import cn.jb.boot.util.EasyExcelUtil;
import cn.jb.boot.util.FileUtil;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PingYinUtil;
import cn.jb.boot.util.PojoUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 工序表 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 16:55:58
 */
@Service
public class MesProcedureServiceImpl extends ServiceImpl<MesProcedureMapper, MesProcedure> implements MesProcedureService {

    @Resource
    private MesProcedureMapper mapper;
    @Resource
    private MesItemStockService stockService;
    @Resource
    private DeviceInfoService deviceInfoService;

    @Override
    public void export(HttpServletResponse response, String id) {

    }


    @Override
    public List<MesProcedureInfoResponse> listByItem(ItemNoRequest request) {
        List<MesProcedure> list = this.list(new LambdaQueryWrapper<MesProcedure>()
                .eq(MesProcedure::getItemNo, request.getItemNo())
                .orderByAsc(MesProcedure::getSeqNo)
        );
        return PojoUtil.copyList(list, MesProcedureInfoResponse.class);
    }

    /////////////////////////////////////导入工序  增加执行结果/////////////////////////////////////////
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public MesProcedureImportResult upload(HttpServletRequest request) {
        // === 新增：返回结构体，包含成功失败汇总 ===
        MesProcedureImportResult result = new MesProcedureImportResult();
        MultipartFile file = FileUtil.getFile(request);

        // === 新增：必须字段检查 ===
        try {
            // 定义必须字段列表
            String[] requiredFields = {"母件BOM", "序号", "工序编码", "工序名称", "工作车间", "定额工时", "加工工时"};

            // 检查Excel是否包含所有必须字段
            List<List<String>> headList = EasyExcel.read(file.getInputStream()).sheet().doReadSync().get(0);
            Set<String> existingHeaders = headList.stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toSet());

            // 检查缺失的必须字段
            List<String> missingFields = Arrays.stream(requiredFields)
                    .filter(field -> !existingHeaders.contains(field))
                    .collect(Collectors.toList());

            if (!missingFields.isEmpty()) {
                MesProcedureImportResult.FailDetail fail = new MesProcedureImportResult.FailDetail();
                fail.setRowNum(0);
                fail.setReason("导入失败：文件缺少必须字段 - " + String.join(", ", missingFields));
                result.getFailList().add(fail);
                result.setFailCount(1);
                return result;
            }
        } catch (Exception e) {
            MesProcedureImportResult.FailDetail fail = new MesProcedureImportResult.FailDetail();
            fail.setRowNum(0);
            fail.setReason("导入失败：文件头读取异常 - " + e.getMessage());
            result.getFailList().add(fail);
            result.setFailCount(1);
            return result;
        }

        List<MesProcedureUploadResponse> list = EasyExcelUtil.importExcel(file, MesProcedureUploadResponse.class);

        if (CollectionUtils.isEmpty(list)) {
            // === 新增：无数据失败统计 ===
            MesProcedureImportResult.FailDetail fail = new MesProcedureImportResult.FailDetail();
            fail.setRowNum(0);
            fail.setReason("导入失败：文件无有效数据");
            result.getFailList().add(fail);
            result.setFailCount(1);
            return result;
        }

        List<String> boms = list.stream().map(MesProcedureUploadResponse::getBomNo).distinct().collect(Collectors.toList());
        Map<String, MesItemStock> bomMap = stockService.getByBomNos(boms);
        Map<String, String> deviceMap = getDeviceMap(list);
        Map<String, String> workShopMap = getWorkShopMap();

        List<MesProcedure> mps = new ArrayList<>();

        // === 新增：每行校验与成功失败分流 ===
        int rowNum = 2; // Excel数据行（从2开始）
        for (MesProcedureUploadResponse response : list) {
            boolean hasMissing = false;
            StringBuilder reason = new StringBuilder();

            // 必填项判定
            if (StringUtils.isBlank(response.getBomNo())) {
                hasMissing = true; reason.append("BOM编号为空;");
            }
            if (StringUtils.isBlank(response.getProcedureName())) {
                hasMissing = true; reason.append("工序名称为空;");
            }
            if (StringUtils.isBlank(response.getProcedureCode())) {
                hasMissing = true; reason.append("工序编码为空;");
            }
            if (response.getSeqNo() == null) {
                hasMissing = true; reason.append("工序顺序为空;");
            }

            // BOM编号必须存在于系统
            if (!hasMissing && !bomMap.containsKey(response.getBomNo())) {
                hasMissing = true; reason.append("BOM编号在系统不存在;");
            }

            if (hasMissing) {
                // === 新增：统计失败 ===
                MesProcedureImportResult.FailDetail fail = new MesProcedureImportResult.FailDetail();
                fail.setRowNum(rowNum);
                fail.setBomNo(response.getBomNo());
                fail.setReason(reason.toString());
                result.getFailList().add(fail);
                result.setFailCount(result.getFailCount() + 1);
                rowNum++;
                continue;
            }

            try {
                // === 原有核心业务逻辑 不删减 ===
                if (bomMap.containsKey(response.getBomNo())) {
                    MesProcedure mp = PojoUtil.copyBean(response, MesProcedure.class);
                    MesItemStock mis = bomMap.get(response.getBomNo());
                    mp.setItemNo(mis.getItemNo());
                    mp.setDeviceId(deviceMap.get(response.getDeviceName()));
                    mp.setDeptId(workShopMap.get(response.getWorkShopName()));
                    mp.setShortCode(PingYinUtil.converterToFirstSpell(response.getProcedureName()));
                    mps.add(mp);
                }
                result.setSuccessCount(result.getSuccessCount() + 1); // === 新增成功统计 ===
            } catch (Exception e) {
                MesProcedureImportResult.FailDetail fail = new MesProcedureImportResult.FailDetail();
                fail.setRowNum(rowNum);
                fail.setBomNo(response.getBomNo());
                fail.setReason("数据处理异常:" + e.getMessage());
                result.getFailList().add(fail);
                result.setFailCount(result.getFailCount() + 1);
            }
            rowNum++;
        }

        List<String> itemNos = mps.stream().map(MesProcedure::getItemNo).collect(Collectors.toList());
        if (!itemNos.isEmpty()) {
            this.remove(new LambdaQueryWrapper<MesProcedure>().in(MesProcedure::getItemNo, itemNos));
            this.saveBatch(mps);
        }

        return result;
    }

    //////////////////////////////////////////////////////////////////////////////

    @Transactional(rollbackFor = Throwable.class)
    public void uploadV1(HttpServletRequest request) {
        MultipartFile file = FileUtil.getFile(request);
        List<MesProcedureUploadResponse> list = EasyExcelUtil.importExcel(file, MesProcedureUploadResponse.class);
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> boms = list.stream().map(MesProcedureUploadResponse::getBomNo).distinct().collect(Collectors.toList());
            Map<String, MesItemStock> bomMap = stockService.getByBomNos(boms);
            Map<String, String> deviceMap = getDeviceMap(list);
            Map<String, String> workShopMap = getWorkShopMap();

            List<MesProcedure> mps = new ArrayList<>();
            for (MesProcedureUploadResponse response : list) {
                if (bomMap.containsKey(response.getBomNo())) {
                    MesProcedure mp = PojoUtil.copyBean(response, MesProcedure.class);
                    MesItemStock mis = bomMap.get(response.getBomNo());
                    mp.setItemNo(mis.getItemNo());
                    mp.setDeviceId(deviceMap.get(response.getDeviceName()));
                    mp.setDeptId(workShopMap.get(response.getWorkShopName()));
                    mp.setShortCode(PingYinUtil.converterToFirstSpell(response.getProcedureName()));
                    mps.add(mp);
                }
            }
            List<String> itemNos = mps.stream().map(MesProcedure::getItemNo).collect(Collectors.toList());
            this.remove(new LambdaQueryWrapper<MesProcedure>().in(MesProcedure::getItemNo, itemNos));
            this.saveBatch(mps);
        }

    }


    @Override
    public void createInfo(MesProcedureCreateRequest params) {
        MesProcedure entity = PojoUtil.copyBean(params, MesProcedure.class);
        this.checkUniqueCode(entity.getProcedureCode(), entity.getItemNo(), entity.getId());
        this.save(entity);
    }

    private void checkUniqueCode(String procedureCode, String itemNo, String id) {
        MesProcedure one = this.getOne(new LambdaQueryWrapper<MesProcedure>()
                .eq(MesProcedure::getProcedureCode, procedureCode)
                .eq(MesProcedure::getItemNo, itemNo)
        );
        if (Objects.nonNull(one)) {
            if (StringUtils.isEmpty(id)) {
                throw new CavException("工序编码已存在！！！");
            } else if (!one.getId().equals(id)) {
                throw new CavException("工序编码已存在！！！");
            }

        }


    }

    @Override
    public MesProcedureInfoResponse getInfoById(String id) {
        MesProcedure entity = this.getById(id);
        MesProcedureInfoResponse response = PojoUtil.copyBean(entity, MesProcedureInfoResponse.class);
        response.setItemName(DictUtil.getDictName(DictType.BOM_NO, response.getItemNo()));
        return response;
    }

    @Override
    public BaseResponse<List<MesProcedurePageResponse>> pageInfo(Paging page, MesProcedurePageRequest params) {
        PageUtil<MesProcedurePageResponse, MesProcedurePageRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }

    @Override
    public MesProcedureHeaderResponse headerInfo(ItemNoRequest params) {
        return mapper.headerInfo(params);
    }

    @Override
    public List<ProcListResp> listNameByShortCode(ShortCodeReq params) {
        return mapper.listNameByShortCode(params);
    }

    @Override
    public List<ItemProcedureResponse> listProcedureByItem(ItemProcedureRequest params) {
        return mapper.listProcedureByItem(params);
    }

    @Override
    public void updateInfo(MesProcedureUpdateRequest params) {
        MesProcedure entity = PojoUtil.copyBean(params, MesProcedure.class);
        checkUniqueCode(entity.getProcedureCode(), entity.getItemNo(), entity.getId());
        this.updateById(entity);
    }


    private static Map<String, String> getWorkShopMap() {
        List<DictDataVo> deptList = DictUtil.getDictCache(DictType.WORK_SHOP);
        return deptList.stream().collect(Collectors.toMap(DictDataVo::getDictLabel, DictDataVo::getDictValue, (k1, k2) -> k1));
    }

    private Map<String, String> getDeviceMap(List<MesProcedureUploadResponse> list) {
        List<String> dNames = list.stream().map(MesProcedureUploadResponse::getDeviceName).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(dNames)) {
            List<DeviceInfo> diList = deviceInfoService.list(new LambdaQueryWrapper<DeviceInfo>().in(DeviceInfo::getDeviceName, dNames));
            Map<String, String> dMap = diList.stream().collect(Collectors.toMap(DeviceInfo::getDeviceName, DeviceInfo::getId, (k1, k2) -> k1));
            return dMap;
        }
        return new HashMap<>();

    }

    public void insertItemProcedure(List<MesProcedureUploadResponse> list){
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> boms = list.stream().map(MesProcedureUploadResponse::getBomNo).distinct().collect(Collectors.toList());
            Map<String, MesItemStock> bomMap = stockService.getByBomNos(boms);
            Map<String, String> deviceMap = getDeviceMap(list);
            Map<String, String> workShopMap = getWorkShopMap();

            List<MesProcedure> mps = new ArrayList<>();
            for (MesProcedureUploadResponse response : list) {
                if (bomMap.containsKey(response.getBomNo())) {
                    MesProcedure mp = PojoUtil.copyBean(response, MesProcedure.class);
                    MesItemStock mis = bomMap.get(response.getBomNo());
                    mp.setItemNo(mis.getItemNo());
                    mp.setDeviceId(deviceMap.get(response.getDeviceName()));
                    mp.setDeptId(workShopMap.get(response.getWorkShopName()));
                    mp.setShortCode(PingYinUtil.converterToFirstSpell(response.getProcedureName()));
                    mps.add(mp);
                }
            }
            List<String> itemNos = mps.stream().map(MesProcedure::getItemNo).collect(Collectors.toList());
            if(itemNos != null && itemNos.size() != 0){
                this.remove(new LambdaQueryWrapper<MesProcedure>().in(MesProcedure::getItemNo, itemNos));
                this.saveBatch(mps);
            }
        }
    }

}
