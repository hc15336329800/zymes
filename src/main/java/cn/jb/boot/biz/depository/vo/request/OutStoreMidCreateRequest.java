package cn.jb.boot.biz.depository.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 中间件使用表表 新增请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-12 15:08:36
 */
@Data
@Schema(name = "OutStoreMidCreateRequest", description = "中间件使用表 新增请求参数")
public class OutStoreMidCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 中间件表ID
     */
    @Schema(description = "中间件表ID")
    private String midId;

    /**
     * 件数
     */
    @Schema(description = "件数")
    private BigDecimal itemCount;

    /**
     * 明细Id
     */
    @Schema(description = "明细Id")
    private String orderDtlId;

}
