package cn.jb.boot.biz.item.vo.request;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * bom用料依赖表 新增请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 17:04:05
 */
@Data
@Schema(name = "MesItemUsedUploadRequest", description = "产品用料导入")
public class MesItemUsedUploadRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 物料编码
     */
    @ExcelProperty(index = 0)
    private String bomNo;


    @ExcelProperty(index = 1)
    private String level;
    /**
     * 用料编码
     */
    @ExcelProperty(index = 2)
    private String useItemNo;
    /**
     * 型号规格
     */
    @ExcelProperty(index = 3)
    private String useItemName;

    /**
     * 固定用量
     */
    @ExcelProperty(index = 4)
    private BigDecimal fixedUse;
    /**
     * 变动用量
     */
    @ExcelProperty(index = 5)
    private BigDecimal variUse;
    /**
     * 辅助计量单位
     */
    @ExcelProperty(index = 6)
    private String itemMeasureAssist;

    /**
     * 辅助固定用量
     */
    @ExcelProperty(index = 7)
    private BigDecimal fixedUseAssist;

    /**
     * 辅助变动用量
     */
    @ExcelProperty(index = 8)
    private BigDecimal variUseAssist;

    /**
     * 产品编码
     */
    @ExcelIgnore
    private String itemNo;

    /**
     * 产品编码
     */
    @ExcelIgnore
    private String useItemType;

    @ExcelIgnore
    private BigDecimal useItemCount;
    @ExcelIgnore
    private String useItemMeasure;


}
