package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

/**
 * @author user
 * @description
 * @date 2022年10月01日 15:08
 */
@Data
public class SaleOrderBatchAddReq {

    @Valid
    @Schema(description = "批量新增销售单")
    private List<SaleOrderBatchAddInfos> list;
}
