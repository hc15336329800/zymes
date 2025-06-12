package cn.jb.boot.biz.item.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * mes中间件库存表表 分页请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-03 17:28:52
 */
@Data
@Schema(name = "MidItemStockPageRequest", description = "mes中间件库存表 分页请求参数")
public class MidItemStockPageRequest implements Serializable {

    private static final long serialVersionUID = 1L;


    @Schema(description = "物料名称")
    private String itemNo;

    @Schema(description = "工序名称")
    private String procedureName;


}
