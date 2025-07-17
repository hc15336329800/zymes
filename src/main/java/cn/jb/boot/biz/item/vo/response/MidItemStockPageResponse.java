package cn.jb.boot.biz.item.vo.response;

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
 * mes中间件库存表表 分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-03 17:28:52
 */
@Data
@Schema(name = "MidItemStockPageResponse", description = "mes中间件库存表 分页返回参数")
public class MidItemStockPageResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private String id;

    /**
     * 工序Id
     */
    @Schema(description = "工序Id")
    private String procedureId;

    @Schema(description = "工序编码")
    private String procedureCode;
    @Schema(description = "工序名称")
    private String procedureName;
    @Schema(description = "工序名称")
    private Integer seqNo;

    @DictTrans(type = DictType.BOM_NO, name = "bomNo")
    private String itemNo;

    private BigDecimal itemCount;


}
