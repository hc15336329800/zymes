package cn.jb.boot.biz.item.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * bom用料依赖表 修改请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 17:04:05
 */
@Data
@Schema(name = "BomUsedUpdateRequest", description = "bom用料依赖 修改请求参数")
public class BomUsedUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    @NotBlank(message = "id不能为空")
    private String id;

    /**
     * 物料编码
     */
    @Schema(description = "物料编码")
    private String itemNo;

    /**
     * 用料编码
     */
    @Schema(description = "用料编码")
    private String useItemNo;

    /**
     * 用料数量
     */
    @Schema(description = "用料数量")
    private BigDecimal useItemCount;

    /**
     * 用料类型,0:物料，1：子件BOM 2：母件BOM
     */
    @Schema(description = "用料类型,0:物料，1：子件BOM 2：母件BOM")
    private Integer useItemType;

    /**
     * 用料长编码
     */
    @Schema(description = "用料长编码")
    private String itemNos;

    /**
     * bom编码
     */
    @Schema(description = "bom编码")
    private String bomNo;

}
