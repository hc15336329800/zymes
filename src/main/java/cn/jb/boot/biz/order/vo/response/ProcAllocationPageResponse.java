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
 * 工序分配表表 分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-05 19:00:38
 */
@Data
@Schema(name = "ProcAllocationPageResponse", description = "工序分配表 分页返回参数")
public class ProcAllocationPageResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

    /**
     * 订单明细Id
     */
    @Schema(description = "订单明细Id")
    private String orderDtlId;

    /**
     * 订单号
     */
    @Schema(description = "订单号")
    private String orderNo;

    /**
     * 产品编码
     */
    @Schema(description = "产品编码")
    @DictTrans(type = DictType.BOM_NO, name = "bomNo")
    private String itemNo;

    /**
     * 工序编码
     */
    @Schema(description = "工序编码")
    private String procedureCode;

    /**
     * 工序名称
     */
    @Schema(description = "工序名称")
    private String procedureName;

    /**
     * 工序总数
     */
    @Schema(description = "工序总数")
    private BigDecimal totalCount;

    /**
     * 外协分配数量
     */
    @Schema(description = "外协分配数量")
    private BigDecimal outerAllocCount;

    /**
     * 工厂分配数量
     */
    @Schema(description = "工厂分配数量")
    private BigDecimal workerAllocCount;

    /**
     * 设备编码
     */
    @Schema(description = "设备编码")
    @DictTrans(type = DictType.DEVICE, name = "deviceName")
    private String deviceId;

    /**
     * 定额工时
     */
    @Schema(description = "定额工时")
    private BigDecimal hoursFixed;

    /**
     * 状态;00:暂停
     */
    @Schema(description = "状态;00:暂停")
    @DictTrans(type = DictType.PROC_STATUS)
    private String procStatus;

    /**
     * 工作车间名称
     */
    @Schema(description = "工作车间名称")
    @DictTrans(type = DictType.WORK_SHOP, name = "deptName")
    private String deptId;

    /**
     * 已生产数量
     */
    @Schema(description = "已生产数量")
    private BigDecimal prodCount;

    /**
     * 分配模式
     */
    @Schema(description = "分配模式")
    private String allocModel;

    /**
     * 原始总数
     */
    @Schema(description = "原始总数")
    private BigDecimal orgiTotalCount;

    /**
     * 中间件使用件数
     */
    @Schema(description = "中间件使用件数")
    private BigDecimal midCount;

    /**
     * 工序序号
     */
    @Schema(description = "工序序号")
    private Integer seqNo;

    /**
     * 外协生产数量
     */
    @Schema(description = "外协生产数量")
    private BigDecimal outerProdCount;

    /**
     * 外协发布数量
     */
    @Schema(description = "外协发布数量")
    private BigDecimal outerPubCount;

    private String itemName;

    /**
     * 最后登陆时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdTime;
    @Schema(description = "待分配数量")
    private BigDecimal waitAllocCount;
}
