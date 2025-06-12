package cn.jb.boot.biz.item.vo.response;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 工序表表 分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 17:04:05
 */
@Data
@Schema(name = "MesProcedureUploadResponse", description = "工序表 分页返回参数")
public class MesProcedureUploadResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 产品编码
     */
    @Schema(description = "图纸号")
    @ExcelProperty(index = 0)
    private String bomNo;

    /**
     * 序号，执行顺序
     */
    @Schema(description = "序号，执行顺序")
    @ExcelProperty(index = 1)
    private Integer seqNo;

    /**
     * 工序编码
     */
    @Schema(description = "工序编码")
    @ExcelProperty(index = 2)
    private String procedureCode;

    /**
     * 工序名称
     */
    @Schema(description = "工序名称")
    @ExcelProperty(index = 3)
    private String procedureName;

    /**
     * 设备本厂编码
     */
    @Schema(description = "设备本厂编码")
    @ExcelProperty(index = 4)
    private String deviceName;
    /**
     * 工作车间名称
     */
    @Schema(description = "工作车间名称")
    @ExcelProperty(index = 5)
    private String workShopName;


    /**
     * 额定工时
     */
    @Schema(description = "额定工时")
    @ExcelProperty(index = 6)
    private BigDecimal hoursFixed;

    /**
     * 加工工时
     */
    @Schema(description = "加工工时")
    @ExcelProperty(index = 7)
    private BigDecimal hoursWork;

    /**
     * 准备工时
     */
    @Schema(description = "准备工时")
    @ExcelProperty(index = 8)
    private BigDecimal hoursPrepare;


    @ExcelIgnore
    private String itemNo;


}
