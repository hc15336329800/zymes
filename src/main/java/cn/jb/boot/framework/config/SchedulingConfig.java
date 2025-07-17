package cn.jb.boot.framework.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.SchedulingConfiguration;

/**
 * 定时任务控制
 */
@Configuration
@ConditionalOnExpression(value = "${enable.scheduling}")
@Import(SchedulingConfiguration.class)
public class SchedulingConfig {

}
