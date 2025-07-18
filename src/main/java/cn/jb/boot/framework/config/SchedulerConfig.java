package cn.jb.boot.framework.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;



//设置定时器多线程
@Configuration
public class SchedulerConfig implements SchedulingConfigurer {
	@Override
	public void configureTasks(ScheduledTaskRegistrar registrar) {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(4);               // 根据并发需求调整
		scheduler.setThreadNamePrefix("scheduler-");
		scheduler.initialize();
		registrar.setTaskScheduler(scheduler);
	}
}
