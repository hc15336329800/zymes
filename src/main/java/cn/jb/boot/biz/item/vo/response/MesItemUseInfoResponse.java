package cn.jb.boot.biz.item.vo.response;

import cn.jb.boot.framework.annotation.DictTrans;
import cn.jb.boot.framework.com.DictType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 产品用料表表 详情返回信息
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 17:04:05
 */
@Data
@Schema(name = "MesItemUseInfoResponse", description = "产品用料表 详情返回信息")
public class MesItemUseInfoResponse implements Serializable {

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
     * 用料编码
     */
    @Schema(description = "用料编码")
    private String useItemNo;

    /**
     * 用料量
     */
    @Schema(description = "用料量")
    private BigDecimal useItemCount;

    /**
     * 用料单位 eg:公斤，件
     */
    @Schema(description = "用料单位 eg:公斤，件")
    private String useItemMeasure;

    /**
     * 固定用量
     */
    @Schema(description = "固定用量")
    private BigDecimal fixedUse;

    /**
     * 变动用量
     */
    @Schema(description = "变动用量")
    private BigDecimal variUse;

    /**
     * 辅助固定用量
     */
    @Schema(description = "辅助固定用量")
    private BigDecimal fixedUseAssist;

    /**
     * 辅助变动用量
     */
    @Schema(description = "辅助变动用量")
    private BigDecimal variUseAssist;

    /**
     * 辅助计量单位
     */
    @Schema(description = "辅助计量单位")
    private String itemMeasureAssist;

    /**
     * 用料类型,00:物料，01：BOM
     */
    @Schema(description = "用料类型,00:物料，01：BOM")
    private String useItemType;

    private String itemName;

    private String useItemName;

    private String bomNo;

    private String useBomNo;

}
