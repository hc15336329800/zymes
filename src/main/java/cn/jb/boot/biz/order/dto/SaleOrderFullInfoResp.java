package cn.jb.boot.biz.order.dto;

import cn.hutool.core.date.DateTime;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * 销售单和审批单结合体返回
 */
@Data
public class SaleOrderFullInfoResp {


//注意：以下为t_sale_order表字段
	private String orderNo; //销售单号
	private String custName; //客户名
	private String itemNo; //物料号
	private String itemName; //物料名称
	private BigDecimal needNum; //数量
	private String orderStatus; //是否已下单


	//注意：以下为t_sale_order_place 表字段
	private String id; //id
	private String applyNo; //申请人
	private String applyName; //申请人姓名
	private LocalDateTime applyTime; //申请时间
	private String placeStatus; //审批状态;00
	private String approvalMsg; //审批原因
	private LocalDateTime deliverTime;//承诺交期
	private String bizType;//业务类型

}
