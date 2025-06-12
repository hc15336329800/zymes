package cn.jb.boot.biz.work.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;


//在 userId、beginTime、endTime 上加 @NotBlank/@NotNull 注解，工序名称保持非必须即可：
@Data
public class WorkerReportDetailPageRequest implements Serializable {
	@NotBlank(message = "工人ID不能为空")
	private String userId;

	@NotBlank(message = "开始时间不能为空")
	private String beginTime;

	@NotBlank(message = "结束时间不能为空")
	private String endTime;

	// 工序名称可以不加校验
	private List<String> procedureNames;
	// ... 其他字段 ...
}
