package cn.jb.boot.biz.sales.service.impl;

import cn.jb.boot.biz.sales.entity.DeliveryDtl;
import cn.jb.boot.biz.sales.entity.DeliveryOrder;
import cn.jb.boot.biz.sales.service.DeliveryDtlService;
import cn.jb.boot.biz.sales.service.DeliveryOrderService;
import cn.jb.boot.biz.sales.service.ExportDeliveryService;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.framework.exception.CavException;

import cn.jb.boot.system.vo.DictDataVo;
import cn.jb.boot.util.DateUtil;
import cn.jb.boot.util.DictUtil;
import com.aspose.cells.BorderType;
import com.aspose.cells.Cell;
import com.aspose.cells.CellBorderType;
import com.aspose.cells.Cells;
import com.aspose.cells.Color;
import com.aspose.cells.Font;
import com.aspose.cells.License;
import com.aspose.cells.PageSetup;
import com.aspose.cells.PaperSizeType;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Style;
import com.aspose.cells.TextAlignmentType;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ExportDeliveryServiceImpl implements ExportDeliveryService {
    @Autowired
    private DeliveryOrderService deliveryOrderService;
    @Autowired
    private DeliveryDtlService dtlService;
    private static final String footer = "注      黄色单据留司机；蓝色单据留司机；红色单据留财务；白色单据留仓库和销售主管.绿色单据留门卫";

    private static final String header = "德州豪沃机械制造有限公司发运单";

    static {
        loadLicense();
    }

    @Override
    public void export(String idStr, HttpServletResponse response) {
        log.info("导出发运单ID:{}", idStr);
        if (StringUtils.isEmpty(idStr)) {
            throw new CavException("id不能为空");
        }
        excelResponse(response, "123");
        List<DeliveryOrder> orders = deliveryOrderService.listByIds(Arrays.asList(idStr.split(",")));
        exportExcel(response, orders);
    }

    @Override
    public void exportMain(String id, HttpServletResponse response) {
        excelResponse(response, "123");
        List<DeliveryOrder> orders = deliveryOrderService.list(new LambdaQueryWrapper<DeliveryOrder>().eq(DeliveryOrder::getMainId, id));
        exportExcel(response, orders);
    }

    private void exportExcel(HttpServletResponse response, List<DeliveryOrder> orders) {
        List<String> ids = orders.stream().map(DeliveryOrder::getId).collect(Collectors.toList());
        List<DeliveryDtl> dtls = dtlService.list(new LambdaQueryWrapper<DeliveryDtl>().in(DeliveryDtl::getDeliveryId,
                ids));
        Map<String, List<DeliveryDtl>> dtlMap =
                dtls.stream().collect(Collectors.groupingBy(DeliveryDtl::getDeliveryId));
        Workbook workbook = new Workbook();
        workbook.getWorksheets().get(0).setName("发运单");
        setDeliveryData(orders, dtlMap, workbook);
        try {
            workbook.save(response.getOutputStream(), SaveFormat.PDF);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadLicense() {
        InputStream in = null;
        try {
            in = ExportDeliveryServiceImpl.class.getClassLoader().getResourceAsStream("license.xml");
            License license = new License();
            license.setLicense(in);
        } catch (Exception e) {
            log.error("记载证书异常", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void setDeliveryData(List<DeliveryOrder> orders, Map<String, List<DeliveryDtl>> dtlMap,
                                        Workbook workbook) {
        Worksheet sheet = workbook.getWorksheets().get(0);
        setBorderStyle(sheet);
        setMargin(sheet);
        setWidthAndHeight(sheet);
        buildPrintInfo(orders, sheet, dtlMap);
    }

    private static void buildPrintInfo(List<DeliveryOrder> orders, Worksheet sheet,
                                       Map<String, List<DeliveryDtl>> dtlMap) {
        int total = orders.size();
        int pageSize = 3;
        int totalPage = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
        int start = -1;
        int current = 0;

        for (int i = 0; i < totalPage; i++) {
            setBlankCell(sheet, ++start);
            for (int j = 0; j < pageSize; j++) {
                if (current >= total) {
                    break;
                }
                DeliveryOrder order = orders.get(current);
                List<DeliveryDtl> dtls = dtlMap.get(order.getId());
                start = setColumnValue(sheet, ++start, order, dtls);
                if (j < 2) {
                    setBlankCell(sheet, ++start);
                    setBlankCell(sheet, ++start);
                    setBlankCell(sheet, ++start);
//                    setBlankCell(sheet, ++start);
                } else {
                    setBlankCell(sheet, ++start);
//                    setBlankCell(sheet, ++start);
                }
                current++;

            }
        }
    }

    private static int setColumnValue(Worksheet sheet, int rowNo, DeliveryOrder order, List<DeliveryDtl> dtls) {
        setDeliveryHeader(sheet, rowNo);
        rowNo++;
        Cells cells = sheet.getCells();

        setCellValue(cells, rowNo, 0, "目的地");
        setCellValue(cells, rowNo, 1, order.getDestination());
        setCellValue(cells, rowNo, 2, order.getCustomer());
        setCellValue(cells, rowNo, 4, "发货日期");

        LocalDate deliveryDate = order.getDeliveryDate();
        if (Objects.nonNull(deliveryDate)) {
            String str = DateUtil.formatDate(deliveryDate, DateUtil.DATE_PATTERN);
            setCellValue(cells, rowNo, 5, str);

        }
        sheet.getCells().merge(rowNo, 5, 1, 4);
        rowNo++;
        setTitle(sheet, rowNo);
        rowNo++;
        for (int i = 0; i < dtls.size(); i++) {
            int cr = rowNo + i;
            DeliveryDtl dtl = dtls.get(i);
            DictDataVo dictData = DictUtil.getDictData(DictType.BOM_NO, dtl.getItemNo());
            if (Objects.nonNull(dictData)) {
                setCellValue(cells, cr, 0, dictData.getName());
                setCellValue(cells, cr, 1, dictData.getDictLabel());
            }

            setCellValue(cells, cr, 2, dtl.getPlanCount());
            setCellValue(cells, cr, 3, dtl.getItemMeasure());
            setCellValue(cells, cr, 4, dtl.getRealCount());
            setCellValue(cells, cr, 5, dtl.getLoader());
            setCellValue(cells, cr, 6, dtl.getTruck());
            setCellValue(cells, cr, 7, dtl.getTruckNo());
            setCellValue(cells, cr, 8, dtl.getRemark());
        }


        rowNo += 8;

        setCellValue(cells, rowNo, 0, "制单");
        setCellValue(cells, rowNo, 1, order.getPreparedBy());
        setCellValue(cells, rowNo, 2, "发货人");
        setCellValue(cells, rowNo, 3, order.getDeliverer());

        setCellValue(cells, rowNo, 4, "质检员");
        setCellValue(cells, rowNo, 6, "司机");
        setCellValue(cells, rowNo, 7, order.getQualityBy());
        rowNo++;
        setCellValue(cells, rowNo, 0, "门卫");
        setCellValue(cells, rowNo, 1, order.getDoorman());

        setCellValue(cells, rowNo, 2, "接收人");
        setCellValue(cells, rowNo, 3, order.getAcceptedBy());

        setCellValue(cells, rowNo, 5, "用筐量");
        setCellValue(cells, rowNo, 6, order.getBoxNum());
        rowNo++;
        setFooter(sheet, rowNo);
        return rowNo;
    }

    private static void setCellValue(Cells cells, int row, int column, Object v) {
        Cell cell;
        cell = cells.get(row, column);
        Style style = cell.getStyle();
        Font font = style.getFont();
        font.setName("宋体");
        //setFontHeightInPoints
        font.setSize(12);
        font.setBold(true);
        cell.setStyle(style);
        cell.putValue(v);
    }

    private static void setBlankCell(Worksheet sheet, int rowNo) {
        Cell cell;
        cell = sheet.getCells().get(rowNo, 0);
        sheet.getCells().merge(rowNo, 0, 1, 9);
        for (int i = 0; i < 9; i++) {
            cell = sheet.getCells().get(rowNo, i);
            Style style = cell.getStyle();
            style.setHorizontalAlignment(TextAlignmentType.CENTER);
            style.setVerticalAlignment(TextAlignmentType.CENTER);
            style.getBorders().getByBorderType(BorderType.TOP_BORDER).setLineStyle(CellBorderType.NONE);
            style.getBorders().getByBorderType(BorderType.BOTTOM_BORDER).setLineStyle(CellBorderType.NONE);
            style.getBorders().getByBorderType(BorderType.LEFT_BORDER).setLineStyle(CellBorderType.NONE);
            style.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setLineStyle(CellBorderType.NONE);
            cell.setStyle(style);
        }

    }


    private static void setTitle(Worksheet sheet, int startRow) {
        String[] titles = {"产品名称", "规格型号", "发货计划", "单位", "发货数量", "装车人签名", "司机", "发货车号", "备注"};
        for (int i = 0; i < 9; i++) {
            Cell cell = sheet.getCells().get(startRow, i);
            setCellValue(sheet.getCells(), startRow, i, titles[i]);
        }
    }

    private static void setDeliveryHeader(Worksheet sheet, int rowNo) {
        //设置表格头样式
        sheet.getCells().merge(rowNo, 0, 1, 9);
        Cell cell = sheet.getCells().get(rowNo, 0);
        Style style = cell.getStyle();
        Font font = style.getFont();
        font.setName("黑体");
        font.setSize(12);
        font.setBold(true);
        cell.setStyle(style);
        cell.putValue(header);

    }

    private static void setFooter(Worksheet sheet, int rowNo) {
        //设置表格头样式
        sheet.getCells().merge(rowNo, 0, 1, 9);
        Cell cell = sheet.getCells().get(rowNo, 0);
        Style style = cell.getStyle();
        Font font = style.getFont();
        font.setName("宋体");
        font.setSize(10);
        font.setBold(true);
        cell.setStyle(style);
        cell.putValue(footer);

    }

    private static void setWidthAndHeight(Worksheet sheet) {
        Cells cells = sheet.getCells();
        int[] arr = {95, 125, 62, 43, 71, 84, 84, 64, 60};
        for (int i = 0; i < arr.length; i++) {
            cells.setColumnWidthPixel(i, arr[i]);

        }
        cells.setStandardHeight(16);

    }

    private static void setBorderStyle(Worksheet worksheet) {
        Style style = worksheet.getCells().getStyle();
        style.setHorizontalAlignment(TextAlignmentType.CENTER);
        style.setVerticalAlignment(TextAlignmentType.CENTER);
        style.getBorders().getByBorderType(BorderType.TOP_BORDER).setLineStyle(CellBorderType.THIN);
        style.getBorders().getByBorderType(BorderType.TOP_BORDER).setColor(Color.getBlack());
        style.getBorders().getByBorderType(BorderType.BOTTOM_BORDER).setLineStyle(CellBorderType.THIN);
        style.getBorders().getByBorderType(BorderType.BOTTOM_BORDER).setColor(Color.getBlack());
        style.getBorders().getByBorderType(BorderType.LEFT_BORDER).setLineStyle(CellBorderType.THIN);
        style.getBorders().getByBorderType(BorderType.LEFT_BORDER).setColor(Color.getBlack());
        style.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setLineStyle(CellBorderType.THIN);
        style.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setColor(Color.getBlack());
        worksheet.getCells().setStyle(style);

    }


    private static void setMargin(Worksheet sheet) {
        PageSetup pageSetup = sheet.getPageSetup();
        pageSetup.setTopMargin(1.04);
        pageSetup.setBottomMargin(1.04);
        pageSetup.setLeftMargin(0.41);
        pageSetup.setRightMargin(0.41);
        pageSetup.setFooterMargin(1.27);
        pageSetup.setHeaderMargin(1.27);
        pageSetup.setCenterHorizontally(true);
        pageSetup.setPrintQuality(600);
        pageSetup.setPaperSize(PaperSizeType.PAPER_A_4_ROTATED);

    }

    public static void excelResponse(HttpServletResponse response, String name) {
        response.reset();
    }


}
