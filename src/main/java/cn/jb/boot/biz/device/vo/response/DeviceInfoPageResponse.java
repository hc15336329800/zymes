package cn.jb.boot.biz.device.vo.response;

import cn.jb.boot.framework.annotation.DictTrans;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 设备信息表 分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-31 04:46:16
 */
@Data
@Schema(name = "DeviceInfoPageResponse", description = "设备信息 分页返回参数")
public class DeviceInfoPageResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 设备编号
     */
    @Schema(description = "设备编号")
    private String deviceNo;

    /**
     * 品牌
     */
    @Schema(description = "品牌")
    private String brand;

    /**
     * 设备厂商
     */
    @Schema(description = "设备厂商")
    private String manufacturer;

    /**
     * 所属车间
     */
    @Schema(description = "所属车间")
    @DictTrans(type = DictType.WORK_SHOP)
    private String deptId;

    /**
     * 设备状态
     */
    @Schema(description = "设备状态")
    private String dataStatus;

    /**
     * 设备类型ID
     */
    @Schema(description = "设备类型名称")
    private String typeName;

    /**
     * 最后登陆时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdTime;
}
