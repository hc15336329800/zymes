package cn.jb.boot.biz.item.entity;

import cn.jb.boot.framework.com.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_laser_report_summary")
@Schema(name = "LaserReportSummary", description = "激光设备报工汇总表")
public class LaserReportSummary extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Schema(description = "主键ID")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@Schema(description = "订单号")
	@TableField("order_no")
	private String orderNo;

	@Schema(description = "设备ID")
	@TableField("device_id")
	private Long deviceId;

	@Schema(description = "设备名称")
	@TableField("device_name")
	private String deviceName;

	@Schema(description = "母件BOM编号")
	@TableField("mother_bom_no")
	private String motherBomNo;

	@Schema(description = "子件BOM编号")
	@TableField("child_bom_no")
	private String childBomNo;

	@Schema(description = "子件物料")
	@TableField("use_item_no")
	private String useItemNo;

	@Schema(description = "报工次数")
	@TableField("report_count")
	private Integer reportCount;

	@Schema(description = "完成数量")
	@TableField("finished_qty")
	private BigDecimal finishedQty;

	@Schema(description = "最近报工时间")
	@TableField("last_report_time")
	private LocalDateTime lastReportTime;
}
