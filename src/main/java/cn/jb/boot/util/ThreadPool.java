package cn.jb.boot.util;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * **本类就是为了把常见的异步场景（定时、单线程、批量、通用异步）
 * **统一收口、易于配置和扩展，避免资源争抢和维护混乱。你直接用上面的静态方法就行，不要自己再造轮子！
 * @author xyb
 * @Description
 * @Date 2022/8/15 20:38
 */
public class ThreadPool {
    private static ScheduledExecutorService SERVICE = new ScheduledThreadPoolExecutor(4,
            new BasicThreadFactory.Builder().namingPattern("mes-pool-%d").daemon(true).build());

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(100000),
            new BasicThreadFactory.Builder().namingPattern("order-scheduled-%d")
                    .daemon(true).build(), new ThreadPoolExecutor.CallerRunsPolicy());
    public static ThreadPoolExecutor allocExecutor = new ThreadPoolExecutor(20, 100,
            0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(2000),
            new BasicThreadFactory.Builder().namingPattern("proc-alloc-%d")
                    .daemon(true).build(), new ThreadPoolExecutor.CallerRunsPolicy());
    public static ThreadPoolExecutor commonExecutor = new ThreadPoolExecutor(100, 500,
            0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(2000),
            new BasicThreadFactory.Builder().namingPattern("common-%d")
                    .daemon(true).build(), new ThreadPoolExecutor.CallerRunsPolicy());

    public static void execute(Runnable runnable) {

        SERVICE.execute(runnable);
    }

    /**
     * 工序分解，采用单线程
     *
     * @param runnable
     */
    public static void singleExecute(Runnable runnable) {
        threadPoolExecutor.execute(runnable);
    }

    public static void commonExecute(Runnable runnable) {
        commonExecutor.execute(runnable);
    }


}
