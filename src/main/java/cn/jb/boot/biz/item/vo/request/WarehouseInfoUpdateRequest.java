package cn.jb.boot.biz.item.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 库位信息表 修改请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-02 00:44:38
 */
@Data
@Schema(name = "WarehouseInfoUpdateRequest", description = "库位信息 修改请求参数")
public class WarehouseInfoUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    @NotBlank(message = "id不能为空")
    private String id;

    /**
     * 库位名称
     */
    @Schema(description = "库位名称")
    private String name;

    private String remark;


}
