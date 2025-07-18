package cn.jb.boot.biz.tabularAnalysis.entity;


import cn.jb.boot.framework.com.entity.BaseEntity;
import cn.jb.boot.framework.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
//@EqualsAndHashCode(callSuper = true)
@TableName("part")
@Schema(name = "Part", description = "")
public class Part extends BaseEntity {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /** 用户ID */
    @Excel(name = "用户序号", type = Excel.Type.EXPORT, cellType = Excel.ColumnType.NUMERIC, prompt = "用户编号")
    private String userId;
    @Excel(name = "序号")
    private String excelId;
    @Excel(name = "零件名称")
    private String partName;
    @Excel(name = "零件尺寸(mm*mm)")
    private String partSize;
    @Excel(name = "数量")
    private String number;
    @Excel(name = "切割长度(m)")
    private String cuttingLength;
    @Excel(name = "打标长度(m)")
    private String markingLength;
    @Excel(name = "蒸发去膜长度(m)")
    private String evaporationFilmLength;
    private String figureNumber;  // 图号
    private String name;  // 图号

}
