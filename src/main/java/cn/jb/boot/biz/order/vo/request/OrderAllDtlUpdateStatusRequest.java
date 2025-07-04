package cn.jb.boot.biz.order.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 订单明细表表 修改请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-05 19:00:38
 */
@Data
@Schema(name = "OrderDtlUpdateStatusRequest", description = "订单明细表 修改请求参数")
public class OrderAllDtlUpdateStatusRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private List<String> ids;

    /**
     * 订单明细状态;01:就绪，02:排程中
     */
    @Schema(description = "订单明细状态;01:就绪，02:排程中")
    private String orderDtlStatus;


}
