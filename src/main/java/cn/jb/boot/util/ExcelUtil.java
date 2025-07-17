package cn.jb.boot.util;


import cn.hutool.core.lang.Dict;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import cn.jb.boot.biz.sales.entity.DeliveryDtl;
import cn.jb.boot.biz.sales.entity.DeliveryOrder;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.framework.exception.CavException;

import cn.jb.boot.system.vo.DictDataVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @author hewangtong
 */
@Slf4j
public class ExcelUtil {

    // 错误信息接收器
    private static String errorMsg;

    // 构造方法
    public ExcelUtil() {
    }

    /**
     * 导入 excel
     *
     * @param file
     * @param function
     * @param <R>
     * @return
     */
    public static <R> R importExcel(MultipartFile file, BiFunction<XSSFSheet, Integer, R> function) {
        if (file == null) {
            return null;
        }
        String fileName = file.getOriginalFilename();
        if (!StringUtils.endsWithIgnoreCase(fileName, "xlsx")) {
            throw new CavException("文件必须是xlsx！");
        }
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            int lastIndex = sheet.getLastRowNum() + 1;
            if (lastIndex > 5000) {
                throw new CavException("xlsx不能超过5000行！");
            }
            return function.apply(sheet, lastIndex);
        } catch (Exception e) {
            log.error("导入excel 错误", e);
            if (e instanceof CavException) {
                throw (CavException) e;
            }
            throw new CavException("读取xlsx失败！");
        }
    }

    /**
     * 读EXCEL文件，获取信息集合
     *
     * @return
     */
    public static Workbook getExcelInfo(MultipartFile mFile, boolean isMesProcedure) {
        String fileName = mFile.getOriginalFilename();// 获取文件名
        try {
            if (!validateExcel(fileName)) {// 验证文件名是否合格
                return null;
            }
            boolean isExcel2003 = true;// 根据文件名判断文件是2003版本还是2007版本
            if (isExcel2007(fileName)) {
                isExcel2003 = false;
            }
            return createExcel(mFile.getInputStream(), isExcel2003, isMesProcedure);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return null;
    }

    /**
     * 根据excel里面的内容读取客户信息
     *
     * @param is          输入流
     * @param isExcel2003 excel是2003还是2007版本
     * @return
     * @throws IOException
     */
    public static Workbook createExcel(InputStream is, boolean isExcel2003, boolean isMesProcedure) {
        try {
            Workbook wb = null;
            if (isExcel2003) {// 当excel是2003时,创建excel2003 xls
                wb = new HSSFWorkbook(is);//
            } else {// 当excel是2007时,创建excel2007 xlsx
                wb = new XSSFWorkbook(is);//原来：XSSFWorkbook 读写xls和xlsx格式时，HSSFWorkbook针对xls，XSSFWorkbook针对xlsx
            }
            return wb;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 验证EXCEL文件
     *
     * @param filePath
     * @return
     */
    public static boolean validateExcel(String filePath) {
        if (filePath == null || !(isExcel2003(filePath) || isExcel2007(filePath))) {
            errorMsg = "文件名不是excel格式";
            return false;
        }
        return true;
    }

    // @描述：是否是2003的excel，返回true是2003
    public static boolean isExcel2003(String filePath) {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    // @描述：是否是2007的excel，返回true是2007
    public static boolean isExcel2007(String filePath) {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }

    public static void downTemp(String name, HttpServletResponse response) {
        String tempPath = "/template/" + name;
        ClassPathResource resource = new ClassPathResource(tempPath);
        excelResponse(response, name);
        try (InputStream is = resource.getInputStream();
             OutputStream outputStream = response.getOutputStream()) {
            IOUtils.copy(is, outputStream);
            response.flushBuffer();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        String tempPath = "/template/delivery_order.xlsx";
        ClassPathResource resource = new ClassPathResource(tempPath);
        String path = resource.getPath();
        System.out.println(path);
    }

    public static void writeExcel(HttpServletResponse response, String name, Consumer<ExcelWriter> consumer) {
        String errorMsg = "导出" + name + "异常！";
        name += "_" + DateUtil.nowDateTime(DateUtil.ALL_PATTERN);
        excelResponse(response, name);
        try (ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(true);
             OutputStream out = response.getOutputStream()) {
            DataFormat dataFormat = writer.getWorkbook().createDataFormat();
            short format = dataFormat.getFormat(DateUtil.YYYY_MM_DD_HH_MM_SS);
            StyleSet styleSet = writer.getStyleSet();
            styleSet.getCellStyleForDate().setDataFormat(format);
            consumer.accept(writer);
            writer.flush(out, true);
        } catch (Exception e) {
            log.error(errorMsg, e);
            throw new CavException(errorMsg);
        }
    }

    public static void excelResponse(HttpServletResponse response, String name) {
        if (!StringUtils.endsWithAny(name.toLowerCase(), ".xls", ".xlsx")) {
            name += ".xlsx";
        }
        try {
            name = URLEncoder.encode(name, StringUtil.UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String mediaType = StringUtil.mediaType(name);
        response.reset();
        response.setCharacterEncoding(StringUtil.UTF8);
        response.setHeader(HttpHeaders.CONTENT_TYPE, mediaType);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + name);
    }

    public static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        return getCellValue(cell, cell.getCellType());
    }

    private static Object getCellValue(Cell cell, CellType cellType) {
        if (null == cell) {
            return null;
        } else {
            if (null == cellType) {
                cellType = cell.getCellType();
            }
            Object value;
            switch (cellType) {
                case NUMERIC:
                    value = numberValue(cell);
                    break;
                case BOOLEAN:
                    value = cell.getBooleanCellValue();
                    break;
                case FORMULA:
                    value = getCellValue(cell, cell.getCachedFormulaResultType());
                    break;
                case BLANK:
                    value = "";
                    break;
                case ERROR:
                    value = error(cell);
                    break;
                default:
                    value = cell.getStringCellValue();
            }

            return value;
        }
    }

    private static Object numberValue(Cell cell) {
        double value = cell.getNumericCellValue();
        CellStyle style = cell.getCellStyle();
        if (null != style) {
            String format = style.getDataFormatString();
            if (null != format && format.indexOf(46) < 0) {
                long longPart = (long) value;
                if ((double) longPart == value) {
                    return value;
                }
            }
        }
        return Double.parseDouble(NumberToTextConverter.toText(value));
    }

    private static String error(Cell cell) {
        FormulaError error = FormulaError.forInt(cell.getErrorCellValue());
        return null == error ? "" : error.getString();
    }

    public static String cellString(Cell cell) {
        DataFormatter dataFormatter = new DataFormatter();
        String value = dataFormatter.formatCellValue(cell);
//        Object o = getCellValue(cell);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return value.trim();
    }

    public static String getStringValue(Row row, int index) {
        return cellString(row.getCell(index));
    }

    public static BigDecimal getBigDecimalValue(Row row, int index) {
        if (Objects.nonNull(row.getCell(index))) {
            try {
                Cell cell = row.getCell(index);
                if (cell.getCellType() == CellType.NUMERIC) {
                    double numericCellValue = cell.getNumericCellValue();
                    BigDecimal bigDecimal = new BigDecimal(numericCellValue);
                    return bigDecimal;
                } else if (cell.getCellType() == CellType.STRING) {
                    String stringCellValue = cell.getStringCellValue();
                    BigDecimal bigDecimal = new BigDecimal(stringCellValue);
                    return bigDecimal;
                } else if (cell.getCellType().equals(CellType.FORMULA)) {
                    double numericCellValue = cell.getNumericCellValue();
                    BigDecimal bigDecimal = new BigDecimal(numericCellValue);
                    return bigDecimal;
                }

            } catch (Exception e) {
                System.out.println("======");
                return BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    public static Double needNum(Cell cell, int index) {
        Object num = ExcelUtil.getCellValue(cell);
        if (num == null) {
            throw new CavException("第" + (index) + "行数据格式错误！");
        }
        if (num instanceof Long) {
            return ((Long) num).doubleValue();
        } else if (num instanceof String) {
            return Double.parseDouble((String) num);
        }
        return (Double) num;
    }

    public static void writeDeliveryOrder(List<DeliveryOrder> orders, Map<String, List<DeliveryDtl>> dtlMap, HttpServletResponse out) {
        Workbook workbook = null;
        try {
            workbook = new HSSFWorkbook();
            setDeliveryData(orders, dtlMap, workbook);
            String fileName = "发运单-" + DateUtil.nowDateTime(DateUtil.ALL_PATTERN) + ".xlsx";
            excelResponse(out, fileName);
            workbook.write(out.getOutputStream());
            out.getOutputStream().flush();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != workbook) {
                    workbook.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private static void setDeliveryData(List<DeliveryOrder> orders, Map<String, List<DeliveryDtl>> dtlMap, Workbook workbook) {
        Sheet sheet = workbook.createSheet("发运单");
        setMargin(sheet);
        int rowIdx = 0;
        CellStyle style = getCommonStyle(sheet);
        setDeliveryColumn(sheet);
        Row row;
        for (int i = 0; i < orders.size(); i++) {
            createDeliveryExcel(sheet, rowIdx, style);
            DeliveryOrder order = orders.get(i);
            setDeliveryHeader(sheet, rowIdx);
            rowIdx++;
            row = sheet.getRow(rowIdx);
            row.setHeight((short) (16 * 20));
            style = getCellStyle(workbook);
            Cell cel = setCellValue(row, "目的地", 0);
            cel.setCellStyle(style);
            setCellValue(row, order.getDestination(), 1);
            setCellValue(row, order.getCustomer(), 2);
            setCellValue(row, "发货日期", 4);
            LocalDate deliveryDate = order.getDeliveryDate();
            if (Objects.nonNull(deliveryDate)) {
                String str = DateUtil.formatDate(deliveryDate, DateUtil.DATE_PATTERN);
                Cell cell = setCellValue(row, str, 5);
                cell.setCellType(CellType.STRING);
            }
            CellRangeAddress region = new CellRangeAddress(rowIdx, rowIdx, 5, 8);
            sheet.addMergedRegion(region);
            rowIdx++;
            row = sheet.getRow(rowIdx);
            row.setHeight((short) (16 * 20));
            setDeliveryTitle(row);
            //填充明细单元格
            List<DeliveryDtl> dtls = dtlMap.get(order.getId());
            for (int j = 0; j < 8; j++) {
                rowIdx++;
                row = sheet.getRow(rowIdx);
                row.setHeight((short) (16 * 20));
                int size = dtls.size();
                if (size < j + 1) {
                    continue;
                }
                DeliveryDtl dtl = dtls.get(j);
                DictDataVo vo = DictUtil.getDictData(DictType.BOM_NO, dtl.getItemNo());
                if (Objects.nonNull(vo)) {
                    setCellValue(row, vo.getName(), 0);
                    setCellValue(row, vo.getDictLabel(), 1);
                }

                Cell cell = row.getCell(2);
                cell.setCellValue(dtl.getPlanCount().doubleValue());
                setCellValue(row, dtl.getItemMeasure(), 3);
                cell = row.getCell(4);
                cell.setCellValue(dtl.getRealCount().doubleValue());
                setCellValue(row, dtl.getLoader(), 5);
                setCellValue(row, dtl.getTruck(), 6);
                setCellValue(row, dtl.getTruckNo(), 7);
                setCellValue(row, dtl.getRemark(), 8);
            }
            rowIdx++;
            row = sheet.getRow(rowIdx);
            row.setHeight((short) (16 * 20));
            setCellValue(row, "制单", 0);
            setCellValue(row, order.getPreparedBy(), 1);
            setCellValue(row, "发货人", 2);
            setCellValue(row, order.getDeliverer(), 3);
            setCellValue(row, "质检员", 6);
            setCellValue(row, order.getQualityBy(), 7);
            rowIdx++;
            row = sheet.getRow(rowIdx);
            row.setHeight((short) (16 * 20));
            setCellValue(row, "门卫", 0);
            setCellValue(row, order.getDoorman(), 1);
            setCellValue(row, "接收人", 2);
            setCellValue(row, order.getAcceptedBy(), 3);
            setCellValue(row, "用筐量", 5);
            setCellValue(row, order.getBoxNum(), 6);
            rowIdx++;
            row = sheet.getRow(rowIdx);
            row.setHeight((short) (16 * 20));
            setDeliveryFooter(row);
            rowIdx++;

        }
    }

    /**
     * 设置页边距
     *
     * @param sheet
     */
    private static void setMargin(Sheet sheet) {
        sheet.setMargin(Sheet.TopMargin, 1.04 / 2.54);
        sheet.setMargin(Sheet.BottomMargin, 1.04 / 2.54);
        sheet.setMargin(Sheet.LeftMargin, 0.41 / 2.54);
        sheet.setMargin(Sheet.RightMargin, 0.41 / 2.54);
        sheet.setMargin(Sheet.FooterMargin, 1.27 / 2.54);
        sheet.setMargin(Sheet.HeaderMargin, 1.27 / 2.54);
        sheet.setHorizontallyCenter(true);
        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setVResolution((short) 600);
        printSetup.setHResolution((short) 600);
//        printSetup.setLandscape(true);
        printSetup.setPaperSize(XSSFPrintSetup.A4_PAPERSIZE);
    }

    private static void createDeliveryExcel(Sheet sheet, int rowIdx, CellStyle style) {
        for (int i = rowIdx; i < rowIdx + 14; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < 9; j++) {
                Cell cell = row.createCell(j);
                cell.setCellStyle(style);
            }
        }
    }

    /**
     * 设置页脚
     *
     * @param row
     */
    private static void setDeliveryFooter(Row row) {
        int rowNum = row.getRowNum();
        Sheet sheet = row.getSheet();
        Workbook workbook = sheet.getWorkbook();
        //设置表格头样式
        CellStyle style = workbook.createCellStyle();
        // 生成一个字体
        Font font = workbook.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 10);
        setCommonStyle(style);
        // 把字体应用到当前的样式
        style.setFont(font);
        //行高 16磅
        row.setHeight((short) (16 * 20));
        Cell cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue("注      黄色单据留司机；蓝色单据留司机；红色单据留财务；白色单据留仓库和销售主管.绿色单据留门卫");
        CellRangeAddress region = new CellRangeAddress(rowNum, rowNum, 0, 8);
        sheet.addMergedRegion(region);
    }

    private static void setDeliveryTitle(Row row) {
        CellStyle style = getCommonStyle(row.getSheet());
        String[] titles = {"产品名称", "规格型号", "发货计划", "单位", "发货数量", "装车人签名", "司机", "发货车号", "备注"};
        Cell cell;
        for (int i = 0; i < titles.length; i++) {
            cell = row.getCell(i);
            cell.setCellStyle(style);
            cell.setCellValue(titles[i]);
        }

    }

    private static Cell setCellValue(Row row, String value, int column) {
        Cell cell;
        cell = row.getCell(column);
        cell.setCellValue(value);
        return cell;
    }

    /**
     * 设置列宽
     *
     * @param sheet
     */
    private static void setDeliveryColumn(Sheet sheet) {
        double[] arr = {12, 18, 9, 7, 11, 11, 12, 10, 5};
        for (int i = 0; i < arr.length; i++) {
            sheet.setColumnWidth(i, (int) (arr[i] * 256));
        }
    }

    private static void setDeliveryHeader(Sheet sheet, int index) {
        Workbook workbook = sheet.getWorkbook();
        //设置表格头样式
        CellStyle style = getCellStyle(workbook);
        Row row = sheet.getRow(index);
        //行高 16磅
        row.setHeight((short) (16 * 20));
        Cell cell = row.getCell(0);
        cell.setCellStyle(style);
        cell.setCellValue("德州豪沃机械制造有限公司发运单");
        CellRangeAddress region = new CellRangeAddress(index, index, 0, 8);
        sheet.addMergedRegion(region);
    }

    @NotNull
    private static CellStyle getCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = getFont(workbook);
        font.setBold(true);
        setCommonStyle(style);
        // 把字体应用到当前的样式
        style.setFont(font);
        return style;
    }

    private static CellStyle getCommonStyle(Sheet sheet) {
        Workbook workbook = sheet.getWorkbook();
        //设置表格头样式
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 12);
        setCommonStyle(style);
        // 把字体应用到当前的样式
        style.setFont(font);
        return style;
    }

    private static void setCommonStyle(CellStyle style) {
        style.setAlignment(HorizontalAlignment.CENTER);//水平居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
//        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);//设置背景色
//        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //设置边框
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
    }

    private static Font getFont(Workbook workbook) {
        // 生成一个字体
        Font font = workbook.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 12);
        return font;
    }
}

