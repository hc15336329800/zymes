package cn.jb.boot.biz.depository.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 出库单表表 分页请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-12 15:08:36
 */
@Data
@Schema(name = "OutStoreOrderPageRequest", description = "出库单表 分页请求参数")
public class OutStoreOrderPageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "开始时间 yyyy-MM-dd")
    private String beginTime;

    @Schema(description = "结束时间 yyyy-MM-dd")
    private String endTime;

    private String itemNo;

    private String outStatus;
    private String reviewStatus;
    @Schema(description = "部门ID")
    private String deptId;

    @Schema(description = "用料编码")
    private String useItemNo;

}
