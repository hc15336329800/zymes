package cn.jb.boot.util;


import cn.hutool.poi.excel.StyleSet;
import cn.jb.boot.biz.sales.entity.SaleOrder;
import cn.jb.boot.framework.com.Constants;
import cn.jb.boot.framework.com.entity.ImportExcelResult;
import cn.jb.boot.framework.com.listenter.EasyExcelReadListener;
import cn.jb.boot.framework.com.listenter.SaleOrderReadListener;
import cn.jb.boot.framework.exception.CavException;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;





/**
 * 导入导出封装 依赖 EasyExcel
 * 简单的封装 复杂的需要自己去实现
 *
 * @Author: xyb
 * @Description:
 * @Date: 2023-05-01 上午 12:50
 **/
public class EasyExcelUtil {

    private static final Logger log = LoggerFactory.getLogger(EasyExcelUtil.class);



////////////////////////////////////////////////////////////////////////////新增////////////////////////////////////////////////////////////////////////////////////////


    /**
     * 读取 Excel 表头（第一行）
     */
    public static List<String> readHead(MultipartFile file) {
        List<String> header = new ArrayList<>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row row   = sheet.getRow(0);
            if (row == null) {
                throw new CavException("导入失败：找不到表头行");
            }
            int lastCell = row.getLastCellNum();
            for (int i = 0; i < lastCell; i++) {
                Cell cell = row.getCell(i);
                String v  = cell == null ? "" : cell.getStringCellValue().trim();
                header.add(v);
            }
        } catch (IOException e) {
            throw new CavException("读取表头失败：" + e.getMessage(), e);
        }
        return header;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 通过实体对象，直接读取导入的excel
     * <pre> {@code
     * List<UserImportDto> list = EasyExcelUtil.importExcel(multipartFile, UserImportVo.class);
     * }</pre>
     *
     * @param file  上传的文件
     * @param clazz 导入实体类型
     * @param <T>   导入实体类型
     * @return 获取到的结果
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> clazz) {
        return importExcel(file, f -> f, clazz).getData();
    }

    /**
     * 导入excel<p>
     * 导入的实体bean 需要转化成db存储对象
     * <pre> {@code
     *         ImportExcelResult<UserImportDto> result = EasyExcelUtil.importExcel(multipartFile, f -> {
     *             UserImportDto dto = PojoUtil.copyBean(f, UserImportDto.class);
     *             dto.setNonce(SnowFlake.genId());
     *             return dto;
     *         } , UserImportVo.class);
     * }</pre>
     *
     * @param file     上传的文件
     * @param function 导入的实体bean 转为db的entity
     * @param clazz    导入实体类型
     * @param <T>      导入实体类型
     * @param <U>      db实体类型
     * @return 处理后的集合
     */
    public static <T, U> ImportExcelResult<U> importExcel(MultipartFile file, Function<T, U> function, Class<T> clazz) {
        ImportExcelResult<U> result = new ImportExcelResult<>();
        // 此处将前端传过来的流变为文件 不然会导致识别错误
        if (!FileUtil.judgeFile(file, Constants.XLS, Constants.XLSX)) {
            throw new CavException("文件格式不对");
        }
        EasyExcelReadListener<T, U> listener = new EasyExcelReadListener<>(function, result);
        try (InputStream is = file.getInputStream()) {
            EasyExcel.read(is, listener).head(clazz).sheet().doRead();
        } catch (Exception e) {
            log.error("读取excel错误", file.getOriginalFilename(), e);
            throw new CavException("读取excel错误");
        }
        return result;
    }


    /**
     * 导出excel 下载
     * 使用bean的方式直接下载导出
     * <pre> {@code
     * EasyExcelUtil.exportSingleFile(response, null, list, "sheet_1");
     * }</pre>
     *
     * @param response   response
     * @param fileName   文件名称
     * @param exportData 导出数据
     * @param sheetName  sheet 名称
     * @param <T>        实体类型
     */
    public static <T> void exportSingleFile(HttpServletResponse response, String fileName, List<T> exportData, String sheetName) {
        if (CollectionUtils.isEmpty(exportData)) {
            throw new CavException("BIZ000100025");
        }
        exportFile(fileName, PojoUtil.arrayToList(exportData), response, sheetName);
    }

    /**
     * excel文件导出(可以包含多个sheet页)，固定表头(通过实体指定属性的方式)
     *
     * @param fileName   导出文件名
     * @param exportData 需要导出数据
     * @param response   response
     * @param sheetNames sheet页的名称，为空则默认以:sheet + 数字规则命名
     * @param <T>        类型
     */
    public static <T> void exportFile(String fileName, List<List<T>> exportData, HttpServletResponse response, String... sheetNames) {
        if (Objects.isNull(response) || CollectionUtils.isEmpty(exportData)) {
            log.error("EasyExcelUtil exportFile required param can't be empty");
            throw new CavException("BIZ000100025");
        }
        setResponse(response, fileName);
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = getExportDefaultStyle();
        try (OutputStream os = response.getOutputStream();
             BufferedOutputStream bos = new BufferedOutputStream(os);
             ExcelWriter writer = EasyExcel.write(bos).registerWriteHandler(horizontalCellStyleStrategy).build()) {
            // 设置导出的表格样式
            for (int itemIndex = 0; itemIndex < exportData.size(); itemIndex++) {
                // sheet页的数据
                List<T> list = exportData.get(itemIndex);
                // 表头数据
                Class<?> clazz = list.get(0).getClass();
                String sheetName = "sheet" + (itemIndex + 1);
                if (ArrayUtils.isNotEmpty(sheetNames) && sheetNames.length >= itemIndex) {
                    sheetName = sheetNames[itemIndex];
                }
                WriteSheet sheet = EasyExcel.writerSheet(itemIndex, sheetName)
                        .head(clazz)
                        .build();
                writer.write(list, sheet);
            }
        } catch (Exception e) {
            log.error("EasyExcelUtil exportFile in error:", e);
            throw new CavException("BIZ000100024");
        }
    }

    /**
     * 下载excel 模板
     *
     * @param response HttpServletResponse
     * @param tempName 模板路径
     */
    public static void downloadExcelTemp(HttpServletResponse response, String tempName) {
        tempName = "/template/" + tempName;

        ClassPathResource classPathResource = new ClassPathResource(tempName);
        try (InputStream inputStream = classPathResource.getInputStream()) {
            FileUtil.download(response, tempName, IOUtils.toByteArray(inputStream), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置 response
     *
     * @param response response
     * @param fileName 文件名
     */
    private static void setResponse(HttpServletResponse response, String fileName) {
        if (StringUtils.isBlank(fileName)) {
            fileName = SnowFlake.genId() + Constants.XLSX;
        } else {
            String[] arrays = StringUtils.split(fileName, Constants.SPOT);
            fileName = String.format("%s(%s).%s", arrays[0], DateUtil.formatDateTime(LocalDateTime.now(), "MMddHHmmssSSS"), arrays[1]);
        }
        FileUtil.setResponse(response, fileName, null);
    }

    /**
     * 配置默认的excel表格样式对象
     *
     * @return HorizontalCellStyleStrategy
     */
    private static HorizontalCellStyleStrategy getExportDefaultStyle() {
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 设置头字体
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setBold(true);
        headWriteCellStyle.setWriteFont(headWriteFont);
        // 设置头居中
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 内容策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 设置 水平居中
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }


    public static List<SaleOrder> importSalesOrder(MultipartFile file, String orderNo) {
        List<SaleOrder> saleOrders = new ArrayList<>();
        // 此处将前端传过来的流变为文件 不然会导致识别错误
        if (!FileUtil.judgeFile(file, Constants.XLS, Constants.XLSX)) {
            throw new CavException("文件格式不对");
        }
        SaleOrderReadListener listener = new SaleOrderReadListener(saleOrders, orderNo);
        try (InputStream is = file.getInputStream()) {
            EasyExcel.read(is, listener).sheet(0).doRead();
        } catch (Exception e) {
            log.error("读取excel错误", file.getOriginalFilename(), e);
            throw new CavException("读取excel错误");
        }
        return saleOrders;
    }

    public static void writeExcel(HttpServletResponse response, String name, Consumer<cn.hutool.poi.excel.ExcelWriter> consumer) {
        String errorMsg = "导出" + name + "异常！";
        name += "_" + DateUtil.nowDateTime(DateUtil.ALL_PATTERN);
        setResponse(response, name);
        try (cn.hutool.poi.excel.ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(true);
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
}
