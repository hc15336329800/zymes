package cn.jb.boot.biz.sales.vo.response;

import cn.hutool.core.lang.Dict;
import cn.jb.boot.framework.annotation.DictTrans;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 发货表表
 *
 * @author X
 * @Description
 * @Copyright Copyright (c) 2022
 * @since 2022-10-01 12:29:38
 */
@Data
public class PlaceOrderPageRep {


    /**
     * 图纸号
     */
    @Schema(description = "图纸号")
    private String id;
    /**
     * 图纸号
     */
    @Schema(description = "图纸号")
    @DictTrans(type = DictType.BOM_NO, name = "bomNo")
    private String itemNo;

    /**
     * 订单号
     */
    @Schema(description = "订单号")
    private String orderNo;

    /**
     * 下单数量
     */
    @Schema(description = "下单数量")
    private BigDecimal orderedNum;

    /**
     * 承诺交期
     */
    @Schema(description = "承诺交期")
    @JsonFormat(pattern = DateUtil.DATE_PATTERN)
    private LocalDateTime deliverTime;

    /**
     * 业务类型
     */
    @Schema(description = "业务类型")
    @DictTrans(type = DictType.ORDER_TYPE)
    private String bizType;

    /**
     * 申请人名称
     */
    @Schema(description = "申请人名称")
    private String applyName;

    /**
     * 申请时间
     */
    @Schema(description = "申请时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime applyTime;

    /**
     * 发货单状态
     */
    @Schema(description = "审批状态;00 待审批 01 审批通过  02审批不通过")
    @DictTrans(type = DictType.APPROVAL_STATUS)
    private String placeStatus;

    /**
     * 审批原因
     */
    @Schema(description = "拒绝原因")
    private String approvalMsg;

    @Schema(description = "工序名称")
    private String procedureName;


    private String custName;

    private String needNum;


}
