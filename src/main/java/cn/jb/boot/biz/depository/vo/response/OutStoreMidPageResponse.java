package cn.jb.boot.biz.depository.vo.response;

import cn.jb.boot.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 中间件使用表表 分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-12 15:08:36
 */
@Data
@Schema(name = "OutStoreMidPageResponse", description = "中间件使用表 分页返回参数")
public class OutStoreMidPageResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

    /**
     * 中间件表ID
     */
    @Schema(description = "中间件表ID")
    private String midId;

    /**
     * 件数
     */
    @Schema(description = "件数")
    private BigDecimal itemCount;

    /**
     * 明细Id
     */
    @Schema(description = "明细Id")
    private String orderDtlId;

    /**
     * 最后登陆时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdTime;
}
