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
 * 工序分配记录表 分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-08 18:34:33
 */
@Data
@Schema(name = "WorkOrderRecordPageResponse", description = "工序分配记录 分页返回参数")
public class WorkOrderRecordPageResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

    /**
     * 工单表记录
     */
    @Schema(description = "工单表记录")
    private String workOrderNo;

    /**
     * 分配数量
     */
    @Schema(description = "分配数量")
    private BigDecimal itemCount;

    /**
     * 最后登陆时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdTime;

    @DictTrans(type = DictType.WORK_SHOP, name = "deptName")
    private String deptId;

    @DictTrans(type = DictType.DEVICE, name = "deviceName")
    private String deviceId;

    @DictTrans(type = DictType.USER_INFO, name = "createdByName")
    private String createdBy;
    @DictTrans(type = DictType.SHIFT_TYPE)
    private String shiftType;
    @DictTrans(type = DictType.BOM_NO, name = "bomNo")
    private String itemNo;
    @Schema(description = "次品数量")
    private BigDecimal deffCount;
    @Schema(description = "正品数量")
    private BigDecimal realCount;
    @Schema(description = "工序名称")
    private String procedureName;

    /** 分配工人姓名 */
    @Schema(description = "工人姓名")
    private String workerName;
}
