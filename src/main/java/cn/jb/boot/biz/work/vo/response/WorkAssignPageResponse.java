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
 * 工单下达表表 分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-09 14:06:38
 */
@Data
@Schema(name = "WorkAssignPageResponse", description = "工单下达表 分页返回参数")
public class WorkAssignPageResponse implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 工序名称
     */
    @Schema(description = "工序名称")
    private String procedureName;

    @Schema(description = "分配数量")
    private BigDecimal planTotalCount;


    /**
     * 下达数量
     */
    @Schema(description = "下达数量")
    private BigDecimal assignCount;

    @DictTrans(type = DictType.DEVICE, name = "deviceName")
    private String deviceId;

    @DictTrans(type = DictType.WORK_SHOP, name = "deptName")
    private String deptId;

    @Schema(description = "下达人")
    @DictTrans(type = DictType.USER_INFO, name = "createdByName")
    private String createdBy;

    /**
     * 最后登陆时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdTime;
}
