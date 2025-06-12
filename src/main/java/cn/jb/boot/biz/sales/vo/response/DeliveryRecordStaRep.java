package cn.jb.boot.biz.sales.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author user
 * @description
 * @date 2022年10月01日 22:23
 */
@Data
public class DeliveryRecordStaRep {

    @Schema(description = "当前月有多少天")
    private int days;


    @Schema(description = "每个客户的数据")
    private List<DeliveryRecordInfos> list;
}
