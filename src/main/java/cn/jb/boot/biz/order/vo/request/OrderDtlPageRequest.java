package cn.jb.boot.biz.order.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 订单明细表表 分页请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-05 19:00:38
 */
@Data
@Schema(name = "OrderDtlPageRequest", description = "订单明细表 分页请求参数")
public class OrderDtlPageRequest implements Serializable {

    @Schema(description = "物料名称（模糊查询）")
    private String itemName;

    private static final long serialVersionUID = 1L;

    @Schema(description = "开始时间 yyyy-MM-dd")
    private String beginTime;

    @Schema(description = "结束时间 yyyy-MM-dd")
    private String endTime;


    private String parentItemNo;

    private List<String> childItemNos;


    private String orderStatus;


//    新增分页字段  原生
    private Integer pageNum;
    private Integer pageSize;
    private String orderNo;
    private String orderDtlStatus;
}
