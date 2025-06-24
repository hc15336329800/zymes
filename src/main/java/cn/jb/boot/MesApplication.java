package cn.jb.boot;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
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
@Slf4j   //implements CommandLineRunner
public class MesApplication extends SpringBootServletInitializer implements CommandLineRunner  {

    public static void main(String[] args) {

        // 启动Spring Boot应用
//        System.out.println("✅ main 开始运行...");
//        SpringApplication.run(MesApplication.class, args);
//        System.out.println("✅ Spring Boot 启动完成！");

        System.out.println("✅ main 开始运行...");
        try {
            SpringApplication.run(MesApplication.class, args);
            System.out.println("✅ Spring Boot 启动完成！");
        } catch (Throwable e) {
            System.err.println("❌ 启动失败：");
            e.printStackTrace(); // 关键！
        }

    }



    // CommandLineRunner 会在 Spring Boot 启动完成后执行
    @Override
    public void run(String... args) throws Exception {
//        String url = "jdbc:oracle:thin:@127.0.0.1:1521:orcl";  错误写法
        String url = "jdbc:oracle:thin:@//127.0.0.1:1521/ORCL";
        String username = "system";
        String password = "sys";
        String tableName = "JSP.JSPBOM";  // 要检查的表名


//        String url = "jdbc:oracle:thin:@//127.0.0.1:1521/XEPDB1";
//        String username = "jsp";
//        String password = "root123456";
//        String tableName = "JSPBOM";  // 要检查的表名


        log.info("=== Oracle数据库连接测试开始 ===");
        System.out.println("=== Oracle数据库连接测试开始 ===");

        log.info("连接参数:");
        log.info("URL: " + url);
        log.info("用户名: " + username);

        // 调用数据库连接测试方法
        testOracleConnection(url, username, password, tableName);
    }

    // 测试连接并查询表记录数
    private static void testOracleConnection(String url, String username, String password, String tableName) {
        log.info("尝试加载JDBC驱动...");
        try {
            // 加载Oracle JDBC驱动
            Class.forName("oracle.jdbc.driver.OracleDriver");
            log.info("✅ JDBC驱动加载成功");
            System.out.println("=== ✅ JDBC驱动加载成功 ===");
        } catch (ClassNotFoundException e) {
            log.error("❌ JDBC驱动加载失败", e);
            System.out.println("=== ❌ JDBC驱动加载失败 ===");
            return;
        }

        // 尝试建立连接并查询数据
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(url, username, password)) {
            log.info("✅ 成功连接到Oracle数据库");
            System.out.println("=== ✅ 成功连接到Oracle数据库 ===");

            // 进行简单查询：测试能否访问数据
            try (java.sql.Statement stmt = conn.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery("SELECT 1 FROM DUAL")) {
                if (rs.next()) {
                    log.info("✅ 成功查询数据，返回结果: " + rs.getObject(1));
                    System.out.println("✅ 成功查询数据，返回结果: " + rs.getObject(1));
                } else {
                    log.warn("⚠️ 查询无结果返回");
                    System.out.println("️ 查询无结果返回");
                }
            } catch (java.sql.SQLException e) {
                log.error("❌ 数据查询失败", e);
                System.out.println("️ ❌ 数据查询失败"+ e);
            }

            // 查询符合条件的记录数
            getTableRecordCount(conn, tableName);

        } catch (java.sql.SQLException e) {
            log.error("❌ 无法连接到数据库", e);
            System.out.println("️ ❌ 无法连接到数据库"+ e);
        }
    }



    // 获取表记录数（符合 BYTSTATUS = 1 条件）
    private static void getTableRecordCount(java.sql.Connection conn, String tableName) {
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE BYTSTATUS = 1";
        try (java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int recordCount = rs.getInt(1);
                log.info("✅ 表 " + tableName + " 的符合条件记录数为: " + recordCount);
                System.out.println("✅ 表 " + tableName + " 的符合条件记录数为: " + recordCount);
            }
        } catch (java.sql.SQLException e) {
            log.error("❌ 获取表记录数失败", e);
            System.out.println("❌ 获取表记录数失败" + e);
        }
    }











    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MesApplication.class);
    }

}
