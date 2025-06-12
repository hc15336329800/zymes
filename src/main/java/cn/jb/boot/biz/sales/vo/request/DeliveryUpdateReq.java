package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 发货表表
 *
 * @author X
 * @Description
 * @Copyright Copyright (c) 2022
 * @since 2022-10-01 12:29:38
 */
@Data
public class DeliveryUpdateReq {

    @NotNull(message = "销售单id不能为空")
    @Schema(description = "销售单id")
    private String id;

    /**
     * 接受客户
     */
    @NotBlank(message = "接收客户不能为空")
    @Schema(description = "接收客户")
    private String custName;

    /**
     * 需求量
     */
    @NotNull(message = "需求数量不能为空")
    @Schema(description = "需求数量")
    private BigDecimal needNum;

}
