package cn.jb.boot.biz.order.vo.response;

import cn.jb.boot.framework.annotation.DictTrans;
import cn.jb.boot.framework.com.DictType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 工序分配表表 详情返回信息
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-05 19:00:38
 */
@Data
@Schema(name = "ProcAllocationInfoResponse", description = "工序分配表 详情返回信息")
public class ProcAllocationInfoResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

    /**
     * 生产单Id
     */
    @Schema(description = "生产单Id")
    private String orderDtlId;

    /**
     * 生产单号
     */
    @Schema(description = "生产单号")
    private String orderNo;

    /**
     * 产品编码
     */
    @Schema(description = "产品编码")
    @DictTrans(type = DictType.BOM_NO, name = "bomNo")
    private String itemNo;

    /**
     * 工序ID
     */
    @Schema(description = "工序名称")
    private String procedureName;


}
