package cn.jb.boot.biz.order.vo.response;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 返回提示信息
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-05 19:00:38
 */
@Data
@Schema(name = "WarningResponse", description = "订单明细表 分页返回参数")
public class WarningResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @Schema(description = "data")
    private String data;
}
