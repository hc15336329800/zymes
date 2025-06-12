package cn.jb.boot.biz.sales.vo.response;

import cn.jb.boot.framework.annotation.DictTrans;
import cn.jb.boot.framework.com.DictType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BomStoreResp {
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;
    /**
     * 产品编码
     */
    @Schema(description = "产品编码")
    @DictTrans(type = DictType.BOM_NO, name = "bomNo")
    private String itemNo;

    /**
     * 产品名称
     */
    @Schema(description = "产品名称")
    private String itemName;


    /**
     * 出入库类型;01:出库，02:入库
     */
    @Schema(description = "出入库类型;00:出库，01:入库")
    @DictTrans(type = DictType.STOCK_TYPE)
    private String bizType;

    /**
     * 状态;00:待确认，01:已确认
     */
    @Schema(description = "状态;00:待确认，01:已确认")
    @DictTrans(type = DictType.CONFIRM_STATUS)
    private String storeStatus;

    /**
     * 申请数量
     */
    @Schema(description = "申请数量")
    private BigDecimal itemCount;


    /**
     * 确认时间
     */
    @Schema(description = "确认时间")
    private LocalDateTime confirmTime;
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;


    /**
     * mes库存数量
     */
    @Schema(description = "mes库存数量")
    private BigDecimal mesItemCount;

    private String autoFlag;


}
