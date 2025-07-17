package cn.jb.boot.biz.sales.vo.response;

import cn.jb.boot.framework.annotation.DictTrans;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 发货表
 *
 * @author X
 * @Description
 * @Copyright Copyright (c) 2022
 * @since 2022-10-01 12:29:38
 */
@Data
public class DeliveryRecordPageRep {


    @Schema(description = "发货人")
    private String sendAcctName;

    /**
     * 发货日期
     */
    @Schema(description = "发货日期")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime sendTime;

    /**
     * 图纸号
     */
    @Schema(description = "图纸号")
    @DictTrans(type = DictType.BOM_NO, name = "bomNo")
    private String itemNo;

    /**
     * 接受客户
     */
    @Schema(description = "接收客户")
    private String custName;


    /**
     * 发货数量
     */
    @Schema(description = "发货数量")
    private BigDecimal sendCount;


}
