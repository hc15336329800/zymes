package cn.jb.boot.biz.sales.vo.response;

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
public class DeliveryPageRep {

    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;
    /**
     * 图纸号
     */
    @Schema(description = "图纸号")
    private String bomNo;

    /**
     * 申请数量
     */
    @Schema(description = "申请数量")
    private BigDecimal applyCount;
    /**
     * 接受客户
     */
    @Schema(description = "接受客户")
    private String custName;

    /**
     * 申请人
     */
    @Schema(description = "申请人")
    private String applyUser;

    /**
     * 申请日期
     */
    @Schema(description = "申请日期")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime applyTime;

    @Schema(description = "发送人")
    private String sendAcctName;

    /**
     * 发货日期
     */
    @Schema(description = "发货日期")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime sendTime;

    /**
     * 实际发货数量
     */
    @Schema(description = "实际发货数量")
    private BigDecimal sendCount;

    /**
     * 发货单状态
     */
    @Schema(description = "发货单状态")
    private String deliveryStatus;
    @Schema(description = "库存数量")
    private BigDecimal itemCount;

    @Schema(description = "货车")
    private String truck;
}
