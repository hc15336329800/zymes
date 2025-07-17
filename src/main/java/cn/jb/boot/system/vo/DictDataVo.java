package cn.jb.boot.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * 字典数据表表
 *
 * @Author: xyb
 * @Description:
 * @Date: 2022-11-14 下午 09:39
 **/
@Schema(name = "DictDataVo", description = "字典数据表")
@Data
public class DictDataVo {


    /**
     * 字典标签
     */
    @Schema(description = "字典标签")
    private String dictLabel;


    /**
     * 字典键值
     */
    @Schema(description = "字典键值")
    private String dictValue;

    /**
     * 字典类型
     */
    @Schema(description = "字典类型")
    private String dictType;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "值类型")
    private String type;


    private Integer dictSort;


}
