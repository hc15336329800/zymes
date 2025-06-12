package cn.jb.boot.biz.item.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 库位信息表 详情返回信息
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-02 00:44:38
 */
@Data
@Schema(name = "WarehouseInfoInfoResponse", description = "库位信息 详情返回信息")
public class WarehouseInfoInfoResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

    /**
     * 库位名称
     */
    @Schema(description = "库位名称")
    private String name;
    private String remark;

}
