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
 * 不良品库存表 分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-03 22:01:35
 */
@Data
@Schema(name = "DefectiveStockPageResponse", description = "不良品库存 分页返回参数")
public class DefectiveStockPageResponse implements Serializable {

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

    private String bomNo;

    private String itemName;

    /**
     * 数量
     */
    @Schema(description = "数量")
    private BigDecimal itemCount;

    /**
     * 出入库类型
     */
    @Schema(description = "出入库类型")
    @DictTrans(type = DictType.STOCK_TYPE)
    private String stockType;


    private String remark;

    /**
     * 最后登陆时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdTime;
}
