package cn.jb.boot.biz.sales.vo.response;

import cn.jb.boot.framework.annotation.DictTrans;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @author user
 * @description
 * @date 2022年10月01日 15:08
 */
@Data
public class SaleOrderPageRep {

    /**
     * 销售单号
     */
    @Schema(description = "订单号主键")
    private String id;
    /**
     * 销售单号
     */
    @Schema(description = "订单号")
    private String orderNo;

    /**
     * 客户名称
     */
    @Schema(description = "客户名称")
    private String custName;

    /**
     * bom编号
     * @DictTrans(type = DictType.BOM_NO, name = "bomNo")   todo: 修改
     */
    @Schema(description = "产品编码")
    private String itemNo;

    @Schema(description = "产品名称")
    private String itemName;

    @Schema(description = "图纸号")
    private String bomNo;



    /**
     * 需求量
     */
    @Schema(description = "需求量")
    private BigDecimal needNum;

    /**
     * 已审核的下单数量
     */
    @Schema(description = "已审核的下单数量")
    private BigDecimal approvedOrderedNum;

    /**
     * 已下单数量
     */
    @Schema(description = "已下单数量")
    private BigDecimal orderedNum;

    /**
     * 已下单数量
     */
    @Schema(description = "待下单数量")
    private BigDecimal waitOrderedNum;
    /**
     * 待生产数量
     */
    @Schema(description = "待生产数量")
    private BigDecimal waitProducedNumNum;

    /**
     * 需求量
     */
    @Schema(description = "已生产数量")
    private BigDecimal producedNum;

    @Schema(description = "是否已下单 00未下单 01 已下单 02下单完成")
    private String orderStatus;


    @Schema(description = "工序名称")
    private String procedureName;

    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private String createdTime;


}
