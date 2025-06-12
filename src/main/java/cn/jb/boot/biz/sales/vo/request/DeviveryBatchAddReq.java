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
public class DeviveryBatchAddReq {

    @Valid
    @Schema(description = "批量新增发货单")
    private List<DeliveryBatchAddInfos> list;
}
