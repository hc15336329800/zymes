package cn.jb.boot.biz.item.vo.response;

import cn.jb.boot.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * mes与ERP物料出入库流水表表 分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 17:04:05
 */
@Data
@Schema(name = "MesErpItemLogPageResponse", description = "mes与ERP物料出入库流水表 分页返回参数")
public class MesErpItemLogPageResponse implements Serializable {

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
    private String itemNo;

    /**
     * 图纸号
     */
    @Schema(description = "图纸号")
    private String bomNo;

    /**
     * 全链路唯一ID
     */
    @Schema(description = "全链路唯一ID")
    private Integer uniId;

    /**
     * mes变动量
     */
    @Schema(description = "mes变动量")
    private BigDecimal mesCount;

    /**
     * 总量
     */
    @Schema(description = "总量")
    private BigDecimal totalCount;

    /**
     * 物料业务类型;01:MES入库，02：MES_出库
     */
    @Schema(description = "物料业务类型;01:MES入库，02：MES_出库")
    private String bizType;

    /**
     * 最后登陆时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdTime;
}
