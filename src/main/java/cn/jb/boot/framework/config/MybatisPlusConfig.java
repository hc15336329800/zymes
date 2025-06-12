package cn.jb.boot.framework.config;

import cn.jb.boot.util.SnowFlake;
import cn.jb.boot.util.UserUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;

/**
 * @author YX
 * @Description mybatis plus 插件
 * @Date 2021/8/18 0:05
 */
@Configuration
@EnableTransactionManagement
public class MybatisPlusConfig {
    private String creator = "createdBy";
    private String createTime = "createdTime";
    private String updater = "updatedBy";
    private String updateTime = "updatedTime";

    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 防止全部更新和删除
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        return interceptor;
    }

    @Bean
    public IdentifierGenerator idGen() {
        return e -> SnowFlake.geneId();
    }

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject mo) {
                this.strictInsertFill(mo, creator, () -> uid(), String.class);
                this.strictInsertFill(mo, updater, () -> uid(), String.class);
                this.strictInsertFill(mo, createTime, LocalDateTime::now, LocalDateTime.class);
                this.strictInsertFill(mo, updateTime, LocalDateTime::now, LocalDateTime.class);
            }

            @Override
            public void updateFill(MetaObject mo) {
                this.strictInsertFill(mo, updater, () -> uid(), String.class);
//                this.strictUpdateFill(mo, updateTime, LocalDateTime::now, LocalDateTime.class);
                //强制更新时间
                this.setFieldValByName(updateTime, LocalDateTime.now(), mo);
            }
        };
    }

    private String uid() {
        try {
            return UserUtil.uid();
        } catch (Exception e) {
        }
        return null;
    }
}
