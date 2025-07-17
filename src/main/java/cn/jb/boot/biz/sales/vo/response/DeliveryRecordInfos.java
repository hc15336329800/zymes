package cn.jb.boot.biz.sales.vo.response;

import cn.jb.boot.framework.annotation.DictTrans;
import cn.jb.boot.framework.com.DictType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author user
 * @description
 * @date 2022年10月01日 22:23
 */
@Data
public class DeliveryRecordInfos {

    /**
     * 客户名称
     */
    @Schema(description = "客户名称")
    @DictTrans(type = DictType.BOM_NO, name = "bomNo")
    private String itemNo;

    @Schema(description = "从1号到月末最后一天 数量集合")
    private List<BigDecimal> counts;
}
