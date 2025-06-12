package cn.jb.boot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring Boot启动类
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2022
 * @Date 2022年4月31日 17:44:45
 */
@MapperScan("cn.jb.**.mapper")
@EnableTransactionManagement
@EnableAsync
@SpringBootApplication(scanBasePackages = {"cn.jb"}, exclude = SecurityAutoConfiguration.class)
public class MesApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {

        System.out.println("✅ main 开始运行...");
        SpringApplication.run(MesApplication.class, args);
        System.out.println("✅ Spring Boot 启动完成！");

    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MesApplication.class);
    }

}
