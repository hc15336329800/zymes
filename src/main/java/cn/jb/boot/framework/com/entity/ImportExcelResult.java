package cn.jb.boot.framework.com.entity;

import java.util.List;

/**
 * 封装导入的结果信息
 *
 * @Author: xyb
 * @Description:
 * @Date: 2023-05-01 下午 02:42
 **/
public class ImportExcelResult<T> {

    /**
     * 导入过程中的错误信息 (可用于记录解析excel数据时存储异常信息，用于业务的事务的回滚)
     */
    private List<String> errorInfo;

    /**
     * 结果集
     */
    private List<T> data;

    /**
     * 获取 导入过程中的错误信息
     *
     * @return errorInfo 导入过程中的错误信息
     */
    public List<String> getErrorInfo() {
        return this.errorInfo;
    }

    /**
     * 设置 导入过程中的错误信息
     *
     * @param errorInfo 导入过程中的错误信息
     */
    public void setErrorInfo(List<String> errorInfo) {
        this.errorInfo = errorInfo;
    }

    /**
     * 获取 结果集
     *
     * @return data 结果集
     */
    public List<T> getData() {
        return this.data;
    }

    /**
     * 设置 结果集
     *
     * @param data 结果集
     */
    public void setData(List<T> data) {
        this.data = data;
    }
}