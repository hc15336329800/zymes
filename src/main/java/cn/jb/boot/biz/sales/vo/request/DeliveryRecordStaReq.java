package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author user
 * @description
 * @date 2022年10月01日 22:23
 */
@Data
public class DeliveryRecordStaReq {

    @NotBlank(message = "统计年月不能为空")
    @Schema(description = "年月 yyyy-MM")
    private String date;
}
