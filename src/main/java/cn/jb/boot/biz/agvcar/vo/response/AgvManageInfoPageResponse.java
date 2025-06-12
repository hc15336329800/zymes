package cn.jb.boot.biz.agvcar.vo.response;

import cn.jb.boot.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* 叉车返回参数
* @Description
* @Copyright Copyright (c) 2024
* @author shihy
* @since 2024-04-08 20:04:08
*/
@Data
@Schema(name = "AgvManageInfoPageResponse", description = "点检信息 分页返回参数")
public class AgvManageInfoPageResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /** 主键 */
    @Schema(description = "主键")
    private String id;

    /** 叉车名称 */
    @Schema(description = "叉车名称")
    private String agvName;

    /** 起始命令字 */
    @Schema(description = "起始命令字")
    private String beginCmd;

    /** 起始点 */
    @Schema(description = "起始点")
    private String begin ;

    /** 结束命令字 */
    @Schema(description = "结束命令字")
    private String endCmd;

    /** 结束点 */
    @Schema(description = "结束点")
    private String end ;

    /** 搬运ID */
    @Schema(description = "搬运ID")
    private String CarryID;

    /** 任务插入时间 */
    @Schema(description = "任务插入时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime datetime;
}
