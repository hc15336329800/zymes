package cn.jb.boot.biz.order.vo.response;

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
 * 订单明细表表 分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-05 19:00:38
 */
@Data
@Schema(name = "OrderDtlPageResponse", description = "订单明细表 分页返回参数")
public class OrderDtlPageResponse implements Serializable {

    /**
     * 物料/bom 名称  新
     */
    private String itemName;

    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private String id;

    /**
     * 生产单号
     */
    @Schema(description = "生产单号")
    private String orderNo;

    /**
     * 产品编码
     */
    @Schema(description = "产品编码")
    @DictTrans(type = DictType.BOM_NO, name = "bomNo")
    private String itemNo;

    /**
     * 产品数量
     */
    @Schema(description = "产品数量")
    private BigDecimal itemCount;

    /**
     * 已生产数量
     */
    @Schema(description = "已生产数量")
    private BigDecimal productionCount;

    /**
     * 订单明细状态;01:就绪，02:排程中
     */
    @Schema(description = "订单明细状态;01:就绪，02:排程中")
    @DictTrans(type = DictType.ORDER_STATUS)
    private String orderDtlStatus;


    /**
     * 最后登陆时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdTime;

    /**
     * 最后更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime updatedTime;
}
