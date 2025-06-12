package cn.jb.boot.system.entity;

import cn.jb.boot.framework.com.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_data")
@Schema(name = "DictData", description = "字典数据表")
@Data
public class DictData extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @Schema(description = "字典编码id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    @Schema(description = "字典排序")
    @TableField("dict_sort")
    private Integer dictSort;
    @Schema(description = "字典标签")
    @TableField("dict_label")
    private String dictLabel;
    @Schema(description = "字典键值")
    @TableField("dict_value")
    private String dictValue;

    @Schema(description = "字典类型")
    @TableField("dict_type")
    private String dictType;

    @Schema(description = "是否默认;Y是 N否")
    @TableField("dict_default")
    private String dictDefault;
    @Schema(description = "状态;00正常 01停用")
    @TableField("data_status")
    private String dataStatus;


}