package cn.jb.boot.biz.tabularAnalysis.vo.response;


import cn.jb.boot.framework.com.entity.BaseEntity;
import cn.jb.boot.framework.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
//@EqualsAndHashCode(callSuper = true)
@Schema(name = "Part", description = "")
public class PartResponse implements Serializable {
    private static final long serialVersionUID = 1L;


    @Schema(description = "主键")
    private String id;

    /** 用户ID */
    private String userId;
    private String excelId;
    private String partName;
    private String partSize;
    private String number;
    private String cuttingLength;
    private String markingLength;
    private String evaporationFilmLength;
    private String figureNumber;  // 图号
    private String name;  // 图号

}
