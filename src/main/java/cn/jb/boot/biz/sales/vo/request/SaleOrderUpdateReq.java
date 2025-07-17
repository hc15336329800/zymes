package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author user
 * @description
 * @date 2022年10月01日 15:08
 */
@Data
public class SaleOrderUpdateReq {

    @NotNull(message = "销售单id不能为空")
    @Schema(description = "销售单id")
    private String id;

    /**
     * 需求量
     */
    @NotNull(message = "需求数量不能为空")
    @Schema(description = "需求数量")
    private BigDecimal needNum;
}
