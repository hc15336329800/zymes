package cn.jb.boot.biz.production.vo.resp;

import cn.jb.boot.framework.annotation.DictTrans;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 生产任务单表 分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-04 17:38:37
 */
@Data
@Schema(name = "ProductionOrderPageResponse", description = "生产任务单 分页返回参数")
public class ProductionOrderPageResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

    /**
     * 销售单号
     */
    @Schema(description = "销售单号")
    private String salesOrderNo;


    /**
     * 产品号
     */
    @Schema(description = "产品号")
//    @DictTrans(type = DictType.BOM_NO, name = "bomNo")
    private String itemNo;



    /**
     * bom号
     */
    private String bomNo;



    /**
     * 数量
     */
    @Schema(description = "数量")
    private BigDecimal itemCount;


    @Schema(description = "订单明细状态;01:就绪，02:已排程;03:待生产;04:生产中,06:关闭,07:暂停")
    @DictTrans(type = DictType.PRODUCTION_STATUS)
    private String status;

    /**
     * 优先级  "紧急" ="02" "加急" ="03" "正常" ="01" "延后""04"
     */
    @Schema(description = "优先级  \"紧急\" =\"02\" \"加急\" =\"03\" \"正常\" =\"01\" \"延后\"\"04\" ")
//    @DictTrans(type = DictType.ORDER_TYPE)
    private String bizType;


    /**
     * 业务类型;01:销售单;02:加急单03:追加计划，04:月度单  新
     */
    @Schema(description = "业务类型;01:销售单;02:加急单03:追加计划，04:月度单")
//    @DictTrans(type = DictType.ORDER_TYPE)
    private String orderType;

    /**
     * 产品名称  新
     */
    @Schema(description = "产品名称")
    private String itemName ;


    /**
     * 承诺交期
     */
    @Schema(description = "承诺交期")
    private LocalDateTime deliverTime;

    @Schema(description = "审核Id")
    private String placeId;

    @Schema(description = "工序编码")
    private String procedureCode;
    @Schema(description = "工序名称")
    private String procedureName;
    @Schema(description = "是否外协")
    private String outerState;

    /** 新增：订单创建时间 */
    @Schema(description = "订单创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdTime;
}
