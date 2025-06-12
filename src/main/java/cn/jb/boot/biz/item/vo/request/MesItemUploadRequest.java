package cn.jb.boot.biz.item.vo.request;

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
@Schema(name = "MesItemUploadRequest", description = "产品导入")
public class MesItemUploadRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 物料编码
     */
    @ExcelProperty(index = 0)
    private String itemNo;
    /**
     * 用料编码
     */
    @ExcelProperty(index = 1)
    private String itemName;
    /**
     * 型号规格
     */
    @ExcelProperty(index = 2)
    private String itemModel;
    /**
     * BOM编码
     */
    @ExcelProperty(index = 3)
    private String bomNo;
    /**
     * 单位 eg:公斤，件
     */
    @Schema(description = "单位 eg:公斤，件")
    @ExcelProperty(index = 4)
    private String itemMeasure;
    /**
     * 库存量
     */
    @ExcelProperty(index = 5)
    private BigDecimal itemCount;
    /**
     * 来源：自制或采购
     */
    @ExcelProperty(index = 6)
    private String itemOrigin;
    /**
     * 00:物料，01:bom
     */
    @ExcelProperty(index = 7)
    private String itemType;
    /**
     * 辅助量
     */
    @ExcelProperty(index = 8)
    private BigDecimal itemCountAssist;
    /**
     * 辅助计量单位
     */
    @Schema(description = "辅助计量单位")
    @ExcelProperty(index = 9)
    private String itemMeasureAssist;
    /**
     * 净重
     */
    @ExcelProperty(index = 10)
    private BigDecimal netWeight;
    /**
     * 库位
     */
    @ExcelProperty(index = 11)
    private String location;
}
