package cn.jb.boot.framework.config;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author YX
 * @Description swagger 配置
 * 请求地址 http://127.0.0.1:8400/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
 * @Date 2021/8/18 0:05
 */
@Configuration
@Profile({"local", "dev"})
public class Swagger3 {

    private String securitySchemeName = "Authorization OAuth2";

    @Bean
    public OperationCustomizer customize() {
        return (o, h) -> o.addParametersItem(
                new Parameter()
                        .in(ParameterIn.HEADER.toString())
                        .required(false)
                        .description("用户id")
                        .name("UserId"));
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Swagger API")
                        .description("swagger api 信息")
                        .version("1.0.0")
                        .contact(new Contact().name("Jiubankeji").email("xie.y.bin@163.com").url("https://www.jiubankeji.cn"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org/")))
                .externalDocs(new ExternalDocumentation()
                        .description("Swagger Api Documentation")
                        .url("https://www.jiubankeji.cn"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .description("OAuth2 token flow.")
                        )

                );
    }

}
