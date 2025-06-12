package cn.jb.boot.biz.outer.vo.response;

import cn.hutool.core.lang.Dict;
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
 * 外协任务表 分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-10 10:44:22
 */
@Data
@Schema(name = "OuterTaskPageResponse", description = "外协任务 分页返回参数")
public class OuterTaskPageResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private String id;

    /**
     * 外协账号
     */
    @Schema(description = "外协账号")
    @DictTrans(type = DictType.USER_INFO, name = "userName")
    private String userId;

    /**
     * 工序分配id
     */
    @Schema(description = "工序分配id")
    private String allocId;

    @DictTrans(type = DictType.BOM_NO, name = "bomNo")
    private String itemNo;

    @Schema(description = "工序名称")
    private String procedureName;

    /**
     * 外协数量
     */
    @Schema(description = "外协数量")
    private BigDecimal outerCount;

    /**
     * 待验收数量
     */
    @Schema(description = "待验收数量")
    private BigDecimal waitRealCount;

    /**
     * 正品数量
     */
    @Schema(description = "正品数量")
    private BigDecimal realCount;

    /**
     * 待验收次品数量
     */
    @Schema(description = "待验收次品数量")
    private BigDecimal waitDeffCount;

    /**
     * 次品数量
     */
    @Schema(description = "次品数量")
    private BigDecimal deffCount;

    /**
     * 最后登陆时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdTime;

    @DictTrans(type = DictType.ACCEPT_STATUS)
    private String acceptStatus;
}
