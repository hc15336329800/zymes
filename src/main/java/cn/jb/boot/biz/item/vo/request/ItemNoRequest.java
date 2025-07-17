package cn.jb.boot.biz.item.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * mes中间件库存表表 新增请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 17:04:05
 */
@Data
@Schema(name = "ItemNoRequest")
public class ItemNoRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 中间件产品编码;产品编码-工序编码
     */
    @Schema(description = "产品编码")
    @NotEmpty(message = "产品编码不能为空")
    private String itemNo;


}
