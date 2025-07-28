package cn.jb.boot.biz.item.entity;

import cn.jb.boot.framework.com.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 订单进度汇总表实体 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order_progress_summary")
public class OrderProgressSummary extends BaseEntity {

	@TableId(type = IdType.AUTO)
	private String id;

	@TableField("order_no")
	private String orderNo;

	@TableField("item_name")
	private String itemName;

	@TableField("cust_name")
	private String custName;

//	@TableField("created_time")
//	private LocalDateTime createdTime;

	@TableField("need_num")
	private BigDecimal needNum;

	@TableField("bom_no")
	private String bomNo;

	@TableField("total_hours")
	private BigDecimal totalHours;

	@TableField("done_hours")
	private BigDecimal doneHours;

	@TableField("progress_percent")
	private BigDecimal progressPercent;
//
//	@TableField("update_time")
//	private LocalDateTime updateTime;
}
