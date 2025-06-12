package cn.jb.boot.biz.item.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 不良品库存表 新增请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-03 22:01:35
 */
@Data
@Schema(name = "DefectiveStockCreateRequest", description = "不良品库存 新增请求参数")
public class DefectiveStockCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 产品编码
     */
    @Schema(description = "产品编码")
    private String itemNo;

    /**
     * 数量
     */
    @Schema(description = "数量")
    private BigDecimal itemCount;

    /**
     * 出入库类型
     */
    @Schema(description = "出入库类型")
    private String stockType;

    private String remark;

}
