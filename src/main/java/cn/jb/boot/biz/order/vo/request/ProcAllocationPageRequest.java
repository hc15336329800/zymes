package cn.jb.boot.biz.order.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 工序分配表表 分页请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-05 19:00:38
 */
@Data
@Schema(name = "ProcAllocationPageRequest", description = "工序分配表 分页请求参数")
public class ProcAllocationPageRequest implements Serializable {

    private static final long serialVersionUID = 1L;


    @Schema(description = "明细ID")
    private List<String> dtlIds;

    @Schema(description = "车间")
    private String deptId;

    @Schema(description = "设备")
    private String deviceId;

    @Schema(description = "工序名称")
    private List<String> procedureNames;


    private String procStatus;


    @Schema(description = "分配状态 00:待分配,01:已分配")
    private String allocStatus;

    @Schema(description = "订单明细状态;01:就绪，02:排程中，   03：待生产 ，04：生产中，06：关闭，07：暂停，08：已完成   09：已排产")
    private String orderDtlStatus;

}
