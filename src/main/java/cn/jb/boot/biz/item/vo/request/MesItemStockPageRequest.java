package cn.jb.boot.biz.item.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 产品库存表表 分页请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 17:04:05
 */
@Data
@Schema(name = "MesItemStockPageRequest", description = "产品库存表 分页请求参数")
public class MesItemStockPageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "产品编码")
    private String itemNo;
    @Schema(description = "产品名称")
    private String itemName;
    @Schema(description = "来源，采购 或者自制 传中文名称")
    private String itemOrigin;
    @Schema(description = "库位")
    private String location;

    @Schema(description = "图纸号")
    private String bomNo;

    @Schema(description = "类型 00:物料，01:BOM")
    @NotEmpty(message = "类型不能为空")
    private String itemType;
    @Schema(description = "开始时间 yyyy-MM-dd")
    private String beginTime;

    @Schema(description = "结束时间 yyyy-MM-dd")
    private String endTime;
}
