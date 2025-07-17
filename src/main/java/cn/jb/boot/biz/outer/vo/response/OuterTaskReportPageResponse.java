package cn.jb.boot.biz.outer.vo.response;

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
 * 外协任务报工表 分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-10 10:44:22
 */
@Data
@Schema(name = "OuterTaskReportPageResponse", description = "外协任务报工 分页返回参数")
public class OuterTaskReportPageResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

    /**
     * 任务ID
     */
    @Schema(description = "任务ID")
    private String taskId;
    @Schema(description = "产品编码")
    @DictTrans(type = DictType.BOM_NO, name = "bomNo")
    private String itemNo;

    private BigDecimal outerCount;

    private String procedureName;

    /**
     * 正品数量
     */
    @Schema(description = "正品数量")
    private BigDecimal realCount;
    @DictTrans(type = DictType.USER_INFO, name = "userName")
    private String userId;

    /**
     * 次品数量
     */
    @Schema(description = "次品数量")
    private BigDecimal deffCount;

    /**
     * 审核状态;00:待审核
     */
    @Schema(description = "审核状态;00:待审核")
    @DictTrans(type = DictType.REP_CHECK_STATUS)
    private String reviewStatus;

    /**
     * 审核账号
     */
    @Schema(description = "审核账号")
    @DictTrans(type = DictType.USER_INFO)
    private String reviewUserId;

    /**
     * 审核描述
     */
    @Schema(description = "审核描述")
    private String reviewDesc;

    /**
     * 审核时间
     */
    @Schema(description = "审核时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime reviewTime;

    /**
     * 最后登陆时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdTime;
}
