package cn.jb.boot.biz.depository.vo.response;

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
 * 出库单表表 分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-12 15:08:36
 */
@Data
@Schema(name = "OutStoreOrderPageResponse", description = "出库单表 分页返回参数")
public class OutStoreOrderPageResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;


    /**
     * 产品编码
     */
    @Schema(description = "产品编码")
    @DictTrans(type = DictType.BOM_NO, name = "bomNo")
    private String itemNo;

    /**
     * 部门Id
     */
    @Schema(description = "部门Id")
    @DictTrans(type = DictType.WORK_SHOP, name = "deptName")
    private String deptId;

    /**
     * 出库状态;出库状态 00：待出库，01：已出库
     */
    @Schema(description = "出库状态;出库状态 00：待出库，01：已出库")
    @DictTrans(type = DictType.OUT_STATUS)
    private String outStatus;

    /**
     * 领料时间
     */
    @Schema(description = "领料时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime outTime;

    /**
     * 领料人
     */
    @Schema(description = "领料人")
    @DictTrans(type = DictType.USER_INFO, name = "userName")
    private String outUser;

    /**
     * 下达数量
     */
    @Schema(description = "下达数量")
    private BigDecimal assignCount;

    /**
     * 用料编码
     */
    @Schema(description = "用料编码")
    private String useItemNo;

    private String useItemName;

    /**
     * 预估数量
     */
    @Schema(description = "预估数量")
    private BigDecimal planCount;

    /**
     * 实际数量
     */
    @Schema(description = "实际数量")
    private BigDecimal realCount;

    /**
     * 审核状态
     */
    @Schema(description = "审核状态")
    @DictTrans(type = DictType.APPROVAL_STATUS)
    private String reviewStatus;

    /**
     * 审核人
     */
    @Schema(description = "审核人")
    @DictTrans(type = DictType.USER_INFO, name = "reviewName")
    private String reviewBy;

    /**
     * 最后登陆时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdTime;

    private BigDecimal itemCount;

}
