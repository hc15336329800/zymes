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
 * 工人报工明细表 分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-11 15:32:29
 */
@Data
@Schema(name = "WorkerReportDtlPageResponse", description = "工人报工明细 分页返回参数")
public class WorkerReportDtlPageResponse implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @DictTrans(type = DictType.USER_INFO, name = "userName")
    private String userId;

    @Schema(description = "日期")
    private String createDate;

    @Schema(description = "件数")
    private BigDecimal userCount;

    /**
     * 分组Id
     */
    @Schema(description = "工资")
    private BigDecimal wages;

    /**
     * 分组账户ID
     */
    @Schema(description = "单价")
    private BigDecimal hoursFixed;

    @Schema(description = "工序名称")
    private String procedureName;

    @DictTrans(type = DictType.BOM_NO, name = "bomNo")
    private String itemNo;


    /**
     * 最后登陆时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdTime;


    private String workOrderNo;

    private String orderNo;


}
