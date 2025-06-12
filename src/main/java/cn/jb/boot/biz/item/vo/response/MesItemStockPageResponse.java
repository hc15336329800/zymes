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
import java.util.Objects;

/**
 * 产品库存表表 分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 17:04:05
 */
@Data
@Schema(name = "MesItemStockPageResponse", description = "产品库存表 分页返回参数")
public class MesItemStockPageResponse implements Serializable {

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
     * 产品名称
     */
    @Schema(description = "产品名称")
    private String itemName;

    /**
     * 库存量
     */
    @Schema(description = "库存量")
    private BigDecimal itemCount;

    /**
     * 单位 eg:公斤，件
     */
    @Schema(description = "单位 eg:公斤，件")
    private String itemMeasure;

    /**
     * 来源：自制或采购
     */
    @Schema(description = "来源：自制或采购")
    @DictTrans(type = DictType.ITEM_ORIGIN)
    private String itemOrigin;

    /**
     * 型号规格
     */
    @Schema(description = "型号规格")
    private String itemModel;

    /**
     * BOM编码
     */
    @Schema(description = "BOM编码")
    private String bomNo;

    /**
     * 00:物料，01:bom
     */
    @Schema(description = "00:物料，01:bom")
    @DictTrans(type = DictType.ITEM_TYPE)
    private String itemType;

    /**
     * 库位
     */
    @Schema(description = "库位")
    @DictTrans(type = DictType.WARE_HOUSE)
    private String location;

    /**
     * 辅助量
     */
    @Schema(description = "辅助量")
    private BigDecimal itemCountAssist;

    /**
     * 辅助计量单位
     */
    @Schema(description = "辅助计量单位")
    private String itemMeasureAssist;

    /**
     * 净重
     */
    @Schema(description = "净重")
    private BigDecimal netWeight;

    /**
     * 最后登陆时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdTime;


    private BigDecimal bomCount;

    public BigDecimal getItemCount() {
        return itemCount.compareTo(BigDecimal.ZERO) < 0 ? bomCount : itemCount;
    }
}
