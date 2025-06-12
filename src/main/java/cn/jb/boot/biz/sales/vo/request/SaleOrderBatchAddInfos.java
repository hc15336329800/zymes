package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * @author user
 * @description
 * @date 2022年10月01日 15:08
 */
@Data
public class SaleOrderBatchAddInfos {

    /**
     * 客户名称
     */
//    @NotBlank(message = "客户名称不能为空")
    @Schema(description = "客户名称")
    private String custName;

    /**
     * bom编号
     */
//    @NotBlank(message = "产品编码不能为空")
    private String itemNo;


    /**
     * 需求量
     */
    @Schema(description = "需求数量")
    private BigDecimal needNum;

    @Schema(description = "工序编码")
    private String procedureId;


}
