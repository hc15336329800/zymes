package cn.jb.boot.framework.com.listenter;


import cn.jb.boot.framework.com.entity.ImportExcelResult;
import cn.jb.boot.util.PojoUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * 读取excel  监听
 *
 * @Author: xyb
 * @Description:
 * @Date: 2023-04-30 下午 01:36
 **/
public class EasyExcelReadListener<T, U> extends AnalysisEventListener<T> {

    /**
     * 数据转化
     */
    private final Function<T, U> function;

    /**
     * 处理结果
     */
    private final ImportExcelResult<U> result;

    private final Logger log = LoggerFactory.getLogger(EasyExcelReadListener.class);

    public EasyExcelReadListener(Function<T, U> function, ImportExcelResult<U> result) {
        this.function = function;
        this.result = result;
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException e = (ExcelDataConvertException) exception;
            String detail = String.format("第%d行，第%d列解析异常，数据为：%s", e.getRowIndex(), e.getColumnIndex(), e.getCellData().getStringValue());
            log.error(detail, e);
            PojoUtil.setBeanList(result, detail, ImportExcelResult::getErrorInfo, ImportExcelResult::setErrorInfo);
        }
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        PojoUtil.setBeanList(result, function.apply(data), ImportExcelResult::getData, ImportExcelResult::setData);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}