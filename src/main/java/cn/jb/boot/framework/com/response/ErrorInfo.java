package cn.jb.boot.framework.com.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author YX
 * @Description json错误返回格式
 * @Date 2021/8/18 0:00
 */
@Data
public class ErrorInfo {

    @Schema(description = "状态码")
    private String code;

    @Schema(description = "错误消息")
    private String message;

    @Schema(description = "url")
    private String url;
}