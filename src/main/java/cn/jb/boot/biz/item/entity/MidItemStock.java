package cn.jb.boot.biz.item.entity;

import cn.jb.boot.framework.com.entity.BaseEntity;
import cn.jb.boot.util.StringUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * mes中间件库存表表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-03 17:28:52
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_mid_item_stock")
@Schema(name = "MidItemStock", description = "mes中间件库存表")
public class MidItemStock extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 中间件产品编码;产品编码-工序编码
     */
    @Schema(description = "中间件产品编码;产品编码-工序编码")
    @TableField("item_no")
    private String itemNo;


    /**
     * 工序编码
     */
    @Schema(description = "工序编码")
    @TableField("procedure_code")
    private String procedureCode;


    /**
     * 工序名称
     */
    @Schema(description = "工序名称")
    @TableField("procedure_name")
    private String procedureName;


    /**
     * 工序序号
     */
    @Schema(description = "工序序号")
    @TableField("seq_no")
    private Integer seqNo;


    /**
     * 期初数量;期初数量
     */
    @Schema(description = "期初数量;期初数量")
    @TableField("initial_count")
    private BigDecimal initialCount;


    /**
     * 报工数量
     */
    @Schema(description = "报工数量")
    @TableField("report_count")
    private BigDecimal reportCount;


    /**
     * 报工使用量
     */
    @Schema(description = "报工使用量")
    @TableField("report_used")
    private BigDecimal reportUsed;


    /**
     * 出库使用量
     */
    @Schema(description = "出库使用量")
    @TableField("out_store_used")
    private BigDecimal outStoreUsed;


    /**
     * 报工剩余数量;订单关闭，剩余量
     */
    @Schema(description = "报工剩余数量;订单关闭，剩余量")
    @TableField("report_less_count")
    private BigDecimal reportLessCount;


    /**
     * 分配使用量
     */
    @Schema(description = "分配使用量")
    @TableField("alloc_used")
    private BigDecimal allocUsed;


    /**
     * 最后一道工序;00:非，01:最后一道工序
     */
    @Schema(description = "最后一道工序;00:非，01:最后一道工序")
    @TableField("last_flag")
    private String lastFlag;


    @TableField(exist = false)
    private String itemProc;

    public String getItemProc() {
        return StringUtil.getItemProcKey(itemNo, procedureCode);
    }

}
