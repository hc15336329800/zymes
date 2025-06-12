package cn.jb.boot.framework.com.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author YX
 * @Description 请求响应信息基类
 * @Date 2021/8/18 0:00
 */
@Data
public class BaseResponse<T> {
    /**
     * 交易状态
     * 00-成功
     * 01-失败
     */
    @JsonProperty("tx_code")
    @Schema(description = "交易状态 00-成功 01-失败")
    private String txStatus = "00";

    /**
     * 分页信息
     */
    @JsonProperty("page")
    @Schema(description = "分页信息")
    private PagingResponse page;

    /**
     * 错误信息描述
     */
    @JsonProperty("error_info")
    @Schema(description = "错误信息描述")
    private ErrorInfo errorInfo;

    @JsonProperty("data")
    @Schema(description = "业务数据")
    private T data;
}
