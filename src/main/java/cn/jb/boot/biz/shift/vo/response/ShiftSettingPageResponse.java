package cn.jb.boot.biz.shift.vo.response;

import cn.jb.boot.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 班次设定表表 分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-07 14:26:37
 */
@Data
@Schema(name = "ShiftSettingPageResponse", description = "班次设定表 分页返回参数")
public class ShiftSettingPageResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

    /**
     * 班次类型
     */
    @Schema(description = "班次类型")
    private String shiftType;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private LocalTime startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private LocalTime endTime;

    /**
     * 周几;1:周一，2:周二，3:周三，4:周四，5:周五，6:周六，7周天
     */
    @Schema(description = "周几;1:周一，2:周二，3:周三，4:周四，5:周五，6:周六，7周天")
    private Integer weekDay;

    /**
     * 最后登陆时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdTime;
}
