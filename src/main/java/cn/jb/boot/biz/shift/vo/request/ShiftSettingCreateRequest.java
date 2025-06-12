package cn.jb.boot.biz.shift.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.List;

/**
 * 班次设定表表 新增请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-07 14:26:37
 */
@Data
@Schema(name = "ShiftSettingCreateRequest", description = "班次设定表 新增请求参数")
public class ShiftSettingCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ShiftSettingReq> list;

    @Data
    public static class ShiftSettingReq {
        @Schema(description = "班次类型，01:白班，02：夜班")
        @NotBlank(message = "班次类型不能为空")
        private String shiftType;
        @Schema(description = "开始时间")
        @NotBlank(message = "开始时间不能为空")
        private LocalTime startTime;
        @Schema(description = "结束时间")
        @NotBlank(message = "结束时间不能为空")
        private LocalTime endTime;
        @Schema(description = "1:周一，2:周二，3:周三，4:周四，5:周五，6:周六，7周天")
        @NotNull(message = "周几不能为空")
        private Integer weekDay;
    }

}
