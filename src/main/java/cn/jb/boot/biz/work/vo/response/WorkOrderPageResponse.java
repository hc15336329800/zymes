package cn.jb.boot.biz.work.vo.response;

import cn.jb.boot.framework.annotation.DictTrans;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.util.ArithUtil;
import cn.jb.boot.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 工单表表 分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-07 21:52:39
 */
@Data
@Schema(name = "WorkOrderPageResponse", description = "工单表 分页返回参数")
public class WorkOrderPageResponse implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 班组  新增     */
    @Schema(description = "班组")
    private String groupId;


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
     * 订单号
     */
    @Schema(description = "订单号")
    private String orderNo ;

    /**
     * 下达状态
     */
    @Schema(description = "下达状态")
    private String state ;


    /**
     * 主键Id
     */
    @Schema(description = "主键Id")
    private String id;


    /**
     * 工单号
     */
    @Schema(description = "工单号")
    private String workOrderNo;


    /**
     * 工序名称
     */
    @Schema(description = "工序名称")
    private String procedureName;

    /**
     * 计划生产数量
     */
    @Schema(description = "计划生产数量")
    private BigDecimal planTotalCount;

    /**
     * 已下达数量
     */
    @Schema(description = "已下达数量")
    private BigDecimal assignCount;

    /**
     * 正品数量
     */
    @Schema(description = "正品数量")
    private BigDecimal realCount;

    /**
     * 待审核正品数量
     */
    @Schema(description = "待审核正品数量")
    private BigDecimal toReviewRealCount;

    /**
     * 次品数量
     */
    @Schema(description = "次品数量")
    private BigDecimal deffCount;

    /**
     * 待审核次品数量
     */
    @Schema(description = "待审核次品数量")
    private BigDecimal toReviewDeffCount;

    /**
     * 设备Id
     */
    @Schema(description = "设备Id")
    @DictTrans(type = DictType.DEVICE, name = "deviceName")
    private String deviceId;

    /**
     * 部门名称
     */
    @Schema(description = "部门名称")
    @DictTrans(type = DictType.WORK_SHOP, name = "deptName")
    private String deptId;

    /**
     * 定额工时
     */
    @Schema(description = "定额工时")
    private BigDecimal hoursFixed;

    /**
     * 班次
     */
    @Schema(description = "班次")
    private String shiftType;

    /**
     * 工序状态
     */
    @Schema(description = "工序状态")
    private String procStatus;

    /**
     * 数据状态
     */
    @Schema(description = "数据状态")
    private String dataStatus;

    /**
     * 最后登陆时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime updatedTime;


    private BigDecimal waitAssignCount;

    private BigDecimal waitReportCount;

    public BigDecimal getWaitAssignCount() {
        return ArithUtil.sub(planTotalCount, assignCount);
    }

    public BigDecimal getWaitReportCount() {
        BigDecimal sub = ArithUtil.sub(assignCount, (ArithUtil.add(realCount, toReviewRealCount, toReviewDeffCount, deffCount)));
        return sub.compareTo(BigDecimal.ZERO) > 0 ? sub : BigDecimal.ZERO;
    }
}
