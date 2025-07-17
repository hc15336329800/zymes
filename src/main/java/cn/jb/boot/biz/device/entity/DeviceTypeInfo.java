package cn.jb.boot.biz.device.entity;

import cn.jb.boot.framework.com.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备类型信息表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-31 03:22:39
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_device_type_info")
@Schema(name = "DeviceTypeInfo", description = "设备类型信息")
public class DeviceTypeInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 设备类型名称
     */
    @Schema(description = "设备类型名称")
    @TableField("name")
    private String name;

    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

}
