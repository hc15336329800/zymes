package cn.jb.boot.framework.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @FileName：SpringContextUtil
 * @Author：xyb
 * @Date：2020/8/29
 * @Description：
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext context;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.context = applicationContext;
    }

    /**
     * 获得spring上下文
     *
     * @return ApplicationContext spring上下文
     */
    public static ApplicationContext getApplicationContext() {
        return context;
    }

    /***
     * 根据一个bean的id获取配置文件中相应的bean
     */
    public static <T> T getBean(String beanId) throws BeansException {
        if (context.containsBean(beanId)) {
            return (T) context.getBean(beanId);
        }
        return null;
    }

    /***
     * 根据一个bean的类型获取配置文件中相应的bean
     */
    public static <T> T getBeanByClass(Class<T> calzz) throws BeansException {
        return context.getBean(calzz);
    }

    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     */
    public static boolean containsBean(String name) {
        return context.containsBean(name);
    }


    public static <T> Map<String, T> beansOfType(Class<T> calzz) {
        return context.getBeansOfType(calzz);
    }
}
