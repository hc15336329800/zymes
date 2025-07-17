package cn.jb.boot.biz.order.vo.response;

import cn.jb.boot.framework.annotation.DictTrans;
import cn.jb.boot.framework.com.DictType;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OuterDistInfoResponse {
    @Schema(description = "主键ID")
    private String id;
    @Schema(description = "工序名称")
    private String procedureName;
    @Schema(description = "产品编码")
    @DictTrans(type = DictType.BOM_NO, name = "bomNo")
    private String itemNo;
    @Schema(description = "工序总数")
    private BigDecimal totalCount;
    @Schema(description = "外协分配数量")
    private BigDecimal outerAllocCount;
    @Schema(description = "定额工时")
    private BigDecimal hoursFixed;
    @Schema(description = "外协生产数量")
    private BigDecimal outerProdCount;

    private BigDecimal outerPubCount;

    private BigDecimal waitPubCount;


}
