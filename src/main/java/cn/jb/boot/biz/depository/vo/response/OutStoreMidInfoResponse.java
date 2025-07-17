package cn.jb.boot.biz.depository.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 中间件使用表表 详情返回信息
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-12 15:08:36
 */
@Data
@Schema(name = "OutStoreMidInfoResponse", description = "中间件使用表 详情返回信息")
public class OutStoreMidInfoResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

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
