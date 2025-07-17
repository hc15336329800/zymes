package cn.jb.boot.framework.config;

import cn.jb.boot.framework.interceptor.AuthInterceptor;
import cn.jb.boot.framework.value.AppProperties;
import cn.jb.boot.framework.value.WhiteListProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 路径拦截器配置
 *
 * @author xyb
 * @Description
 * @Date 2022/7/2 15:01
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AppProperties app;

    @Autowired
    private AuthInterceptor authInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WhiteListProperties whiteList = app.getWhiteList();
        registry.addInterceptor(authInterceptor).addPathPatterns(whiteList.getApiBlackList()).excludePathPatterns(whiteList.getApiPermitAll());
    }
}
