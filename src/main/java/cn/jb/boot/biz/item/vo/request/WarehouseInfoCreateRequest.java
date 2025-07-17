package cn.jb.boot.biz.item.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 库位信息表 新增请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-02 00:44:38
 */
@Data
@Schema(name = "WarehouseInfoCreateRequest", description = "库位信息 新增请求参数")
public class WarehouseInfoCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 库位名称
     */
    @Schema(description = "库位名称")
    private String name;

    private String remark;

}
