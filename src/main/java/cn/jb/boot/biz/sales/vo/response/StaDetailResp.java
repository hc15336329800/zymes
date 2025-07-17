package cn.jb.boot.biz.sales.vo.response;

import cn.hutool.core.lang.Dict;
import cn.jb.boot.framework.annotation.DictTrans;
import cn.jb.boot.framework.com.DictType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StaDetailResp {

    @Schema(description = "业务类型 00:出库，01 入库")
    private String bizType;
    @Schema(description = "数量")
    private BigDecimal itemCount;
    @Schema(description = "出库时间")
    private LocalDateTime confirmTime;
    @Schema(description = "产品编码")
    @DictTrans(type = DictType.BOM_NO, name = "bomNo")
    private String itemNo;
    @Schema(description = "单位")
    private String itemMeasure;


}
