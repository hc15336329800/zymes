package cn.jb.boot.biz.work.vo.response;

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
 * 工单报工表表 分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-09 14:06:38
 */
@Data
@Schema(name = "WorkReportPageResponse", description = "工单报工表 分页返回参数")
public class WorkReportPageResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;
    /**
     * 工单号
     */
    @Schema(description = "工单号")
    private String workOrderNo;

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
     * 工序名称
     */
    @Schema(description = "工序名称")
    private String procedureName;

    @Schema(description = "分配数量")
    private BigDecimal planTotalCount;
    /**
     * 正品数量
     */
    @Schema(description = "正品数量")
    private BigDecimal realCount;

    /**
     * 次品数量
     */
    @Schema(description = "次品数量")
    private BigDecimal deffCount;

    @DictTrans(type = DictType.DEVICE, name = "deviceName")
    private String deviceId;

    @DictTrans(type = DictType.WORK_SHOP, name = "deptName")
    private String deptId;

    @Schema(description = "审核状态")
    @DictTrans(type = DictType.REPORT_STATUS)
    private String status;


    /**
     * 质检员时间
     */
    @Schema(description = "质检员时间")
    private LocalDateTime quaTime;

    /**
     * 质检员工号
     */
    @Schema(description = "质检员工号")
    private String quaUser;

    /**
     * 审核时间
     */
    @Schema(description = "审核时间")
    private LocalDateTime verifyTime;

    /**
     * 审核员工号
     */
    @Schema(description = "审核员工号")
    private String verifyUser;

    /**
     * 最后登陆时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdTime;


    @Schema(description = "分组名称")
    private String groupName;
    @Schema(description = "分组类型")
    @DictTrans(type = DictType.REPORT_TYPE)
    private String reportType;
    @DictTrans(type = DictType.USER_INFO, name = "groupUserName")
    private String groupUid;


    private String remark;

}
