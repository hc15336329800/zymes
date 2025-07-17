package cn.jb.boot.biz.order.vo.response;

import cn.jb.boot.framework.annotation.DictTrans;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单明细表分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-05 19:00:38
 */
@Data
@Schema(name = "OrderDtlPageResponse", description = "订单明细表分页返回参数")
public class OrderDtlPageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /* 树形结构相关字段 */
    /** 子节点：同一个 orderNo 下的所有明细 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<OrderDtlPageResponse> children;

    /* 基本信息 */
    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "生产单号")
    private String orderNo;

    @Schema(description = "顶层BOM号")
    private String topBomNo;

    @Schema(description = "BOM号")
    private String bomNo;

    /* 产品信息 */
    @Schema(description = "物料/bom名称")
    private String itemName;

    @Schema(description = "产品编码")
     private String itemNo;

    @Schema(description = "产品数量")
    private BigDecimal itemCount;

    @Schema(description = "已生产数量")
    private BigDecimal productionCount;

    /* 状态信息 */
    @Schema(description = "订单明细状态;01:就绪，02:排程中")
    @DictTrans(type = DictType.ORDER_STATUS, name = "orderDtlStatusDesc")
    private String orderDtlStatus;

//    @Schema(description = "订单明细状态描述")  删掉不然会重复
//    private String orderDtlStatusDesc;

    /* 时间信息 */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime updatedTime;
}
