package cn.jb.boot.util;


import cn.jb.boot.framework.com.Constants;
import cn.jb.boot.framework.com.entity.TreeSelect;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.web.method.HandlerMethod;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * JavaBean验证 和JavaBean 有关的东西
 *
 * @Author: xyb
 * @Description:
 * @Date: 2022-11-14 下午 10:05
 **/
@SuppressWarnings("unchecked")
public class PojoUtil {

    private static final Map<String, BeanCopier> BEAN_COPIER_MAP = new ConcurrentHashMap<>();

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(PojoUtil.class);
    /**
     * 批次数量
     */
    private static final int BATCH_SIZE = 500;

    private PojoUtil() {
    }

    /**
     * 复制list
     *
     * @param resources 原始的数据list
     * @param clazz     目标class
     * @param <R>       原始数据class
     * @param <T>       目标class
     * @return 目标资源
     */
    public static <R, T> List<T> copyList(List<R> resources, Class<T> clazz) {
        return copyList(resources, clazz, null);
    }

    /**
     * 复制 list集合 可以单独处理某些属性
     *
     * @param resources 数据源
     * @param clazz     返回类型
     * @param consumer  处理函数
     * @param <R>       数据源类型
     * @param <T>       返回类型
     * @return 返回数据
     */
    public static <R, T> List<T> copyList(List<R> resources, Class<T> clazz, BiConsumer<R, T> consumer) {
        return copy(resources, null, clazz, consumer);
    }

    /**
     * 类型强转
     *
     * @param o   数据
     * @param <T> 类型
     * @return 值
     */
    public static <T> T cast(Object o) {
        if (o != null) {
            return (T) o;
        }
        return null;
    }

    /**
     * 逗号拼接的字符串转为 list 集合
     *
     * @param input 输入字符串
     * @return list集合
     */
    public static List<String> singleToList(String input) {
        if (StringUtils.isNotBlank(input)) {
            String[] arrays = StringUtils.split(input.trim(), Constants.COMMA);
            return arrayToList(arrays);
        }
        return new ArrayList<>();
    }

    /**
     * 数组转为集合
     *
     * @param ts  数组
     * @param <T> 集合类型
     * @return 集合
     */
    @SafeVarargs
    public static <T> ArrayList<T> arrayToList(T... ts) {
        if (ts == null) {
            throw new NullPointerException();
        }
        int arraySize = ts.length;
        int capacity = cast(5L + arraySize + (arraySize / 10));
        ArrayList<T> list = new ArrayList<>(capacity);
        Collections.addAll(list, ts);
        return list;
    }

    /**
     * list  转 list 使用lambda
     *
     * @param list   结合
     * @param mapper 处理转化过程
     * @param <T>    原始集合类型
     * @param <R>    新的集合类型
     * @return 新的集合
     */
    public static <T, R> List<R> toList(Collection<T> list, Function<? super T, ? extends R> mapper) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    /***
     * 流的方式处理集合
     * @param list 集合
     * @param filter 过滤
     * @param peek peek
     * @return 处理后的结果
     * @param <T> 类型
     */
    public static <T> List<T> toList(Collection<T> list, Predicate<T> filter, Consumer<T> peek) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        Stream<T> stream = list.stream();
        if (filter != null) {
            stream = stream.filter(filter);
        }
        if (peek != null) {
            stream = stream.peek(peek);
        }
        return stream.collect(Collectors.toList());
    }

    /**
     * 将集合对象转为map
     * key为对象里的一个属性，value为对象对象集合
     *
     * @param list      list集合
     * @param keyMapper 获取key的方法
     * @param <K>       map的key类型
     * @param <T>       value类型
     * @return map对象
     */
    public static <K, T> Map<K, List<T>> groupMap(List<T> list, Function<? super T, ? extends K> keyMapper) {
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<>(16);
        }
        return list.stream().collect(Collectors.groupingBy(keyMapper));
    }

    /**
     * 将list集合转为 map value为对应的key的集合
     *
     * @param list        数据源
     * @param keyMapper   key
     * @param valueMapper value
     * @param <K>         key
     * @param <T>         原始数据
     * @param <R>         返回结果
     * @return map
     */
    public static <K, T, R> Map<K, List<R>> groupMapList(List<T> list, Function<? super T, ? extends K> keyMapper, Function<T, R> valueMapper) {
        Map<K, List<R>> map = new HashMap<>(64);
        if (CollectionUtils.isNotEmpty(list)) {
            K key;
            List<R> result;
            for (T t : list) {
                key = keyMapper.apply(t);
                result = map.get(key);
                if (result == null) {
                    result = new ArrayList<>();
                }
                R r = valueMapper.apply(t);
                result.add(r);
                map.put(key, result);
            }
        }
        return map;

    }

    /**
     * 将集合对象转为map
     * key为对象里的一个属性，value为对象本身
     *
     * @param list      list集合
     * @param keyMapper 获取key的方法
     * @param <K>       map的key类型
     * @param <T>       value类型
     * @return map对象
     */
    public static <K, T> Map<K, T> toMap(List<T> list, Function<? super T, ? extends K> keyMapper) {
        return toMap(list, keyMapper, Function.identity());
    }

    /**
     * 将集合对象转为map
     * key 和value 均为对象里的一个属性
     *
     * @param list        list集合
     * @param keyMapper   获取key的方法
     * @param valueMapper 获取value的方法
     * @param <T>         list集合对象
     * @param <K>         map的key类型
     * @param <U>         map的value类型
     * @return map对象
     */
    public static <T, K, U> Map<K, U> toMap(List<T> list, Function<? super T, ? extends K> keyMapper,
                                            Function<? super T, ? extends U> valueMapper) {
        return toMap(list, keyMapper, valueMapper, (o, n) -> n);
    }

    /**
     * 对象转map 处理了 同key的问题
     * 同key 回覆盖
     *
     * @param list          list集合
     * @param keyMapper     获取key的方法
     * @param valueMapper   获取value的方法
     * @param mergeFunction 合并的方法
     * @param <T>           list集合对象
     * @param <K>           map的key类型
     * @param <U>           map的value类型
     * @return map对象
     */
    public static <T, K, U> Map<K, U> toMap(List<T> list, Function<? super T, ? extends K> keyMapper,
                                            Function<? super T, ? extends U> valueMapper, BinaryOperator<U> mergeFunction) {
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<>(64);
        }
        return list.stream().collect(Collectors.toMap(keyMapper, valueMapper, mergeFunction));
    }

    /**
     * 集合转为 数组
     *
     * @param list  集合
     * @param clazz 类
     * @param <T>   数组
     * @return 数组
     */
    public static <T> T[] toArray(List<T> list, Class<T> clazz) {
        if (list == null || clazz == null) {
            return null;
        }
        T[] array = (T[]) Array.newInstance(clazz, list.size());
        return list.toArray(array);
    }

    /**
     * 设置bean里面的list属性<p>
     * 先判断list对象是否存在，不存在的先new一个，再设置
     *
     * @param t   bean对象
     * @param r   设置的信息
     * @param get get
     * @param set set
     * @param <T> bean对象类型
     * @param <R> 设置的对象类型
     */
    public static <T, R> void setBeanList(T t, R r, Function<T, List<R>> get, BiConsumer<T, List<R>> set) {
        if (t == null || r == null) {
            return;
        }
        List<R> list = get.apply(t);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(r);
        set.accept(t, list);
    }

    /***
     * 反射赋值
     * @param bean bean对象
     * @param name bean的属性名称
     * @param value 对象值
     */
    public static void setProperty(final Object bean, String name, final Object value) {
        Class<?> clazz = bean.getClass();
        try {
            Field field = clazz.getDeclaredField(name);
            setValue(bean, field, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 反射取值
     * @param bean bean对象
     * @param name bean的属性名称
     * @return Object
     */
    public static Object getProperty(final Object bean, String name) {
        Class<?> clazz = bean.getClass();
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反射取值
     *
     * @param bean  对象
     * @param field 属性
     * @return 获取到的值
     */
    public static Object getProperty(final Object bean, Field field) {
        try {
            field.setAccessible(true);
            return field.get(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 数值强转
     *
     * @param value 数
     * @return int对象
     */
    private static int cast(long value) {
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (value < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) value;
    }

    /**
     * 利用cglib的BeanCopier 拷贝对象
     *
     * @param r     源
     * @param clazz 目标
     */
    public static <R, T> T copyBean(R r, Class<T> clazz) {
        if (r == null || clazz == null) {
            return null;
        }
        T t = null;
        try {
            t = newInstance(clazz);
            copyBean(r, t);
        } catch (Exception e) {
            logger.error("复制异常，{}", e.getMessage());
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 利用cglib的BeanCopier 拷贝对象
     *
     * @param r 源
     * @param t 目标
     */
    public static <R, T> void copyBean(R r, T t) {
        copy(Collections.singletonList(r), t, (Class<T>) t.getClass(), null);
    }

    /**
     * 复制对象公共方法
     *
     * @param resources 原始数据
     * @param target    目标对象
     * @param clazz     目标对象类型
     * @param consumer  单独处理类型的函数
     * @param <R>       原始类型
     * @param <T>       目标类型
     * @return 复制后的数据集
     */
    private static <R, T> List<T> copy(List<R> resources, T target, Class<T> clazz, BiConsumer<R, T> consumer) {
        List<T> list = new ArrayList<>();
        try {
            T t;
            BeanCopier copier = beanCopier(resources.get(0).getClass(), clazz);
            for (R r : resources) {
                if (target == null) {
                    t = newInstance(clazz);
                } else {
                    t = target;
                }
                copier.copy(r, t, null);
                if (consumer != null) {
                    consumer.accept(r, t);
                }
                list.add(t);
            }
        } catch (Exception e) {
            logger.error("复制异常，{}", e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * map转bean
     *
     * @param source 数据源
     * @param clazz  类型
     * @param <T>    bean的类型
     * @return bean对象
     * @throws Exception 异常
     */
    public static <T> T map2Bean(Map<String, String[]> source, Class<T> clazz) throws Exception {
        if (source == null || clazz == null) {
            return null;
        }
        T t = newInstance(clazz);
        BeanInfo info = Introspector.getBeanInfo(clazz);
        PropertyDescriptor[] pds = info.getPropertyDescriptors();
        String key;
        Object[] ps = new Object[1];
        for (PropertyDescriptor pd : pds) {
            key = pd.getName();
            if (source.containsKey(key)) {
                ps[0] = source.get(key);
                pd.getWriteMethod().invoke(t, ps);
            }
        }
        return t;
    }

    /**
     * 获取到 BeanCopier
     *
     * @param r   源对象
     * @param t   目标对象
     * @param <R> 源类型
     * @param <T> 目标类型
     * @return 对应的BeanCopier
     */
    private static <R, T> BeanCopier beanCopier(Class<R> r, Class<T> t) {
        String beanKey = r.toString() + t.toString();
        BeanCopier copier;
        if (BEAN_COPIER_MAP.containsKey(beanKey)) {
            copier = BEAN_COPIER_MAP.get(beanKey);
        } else {
            copier = BeanCopier.create(r, t, false);
            BEAN_COPIER_MAP.put(beanKey, copier);
        }
        return copier;
    }

    /**
     * 深拷贝
     *
     * @param resources 集合
     * @param <T>       bean类型
     * @return 拷贝后的对象
     */
    public static <T extends Serializable> List<T> deepCopy(List<T> resources) {
        try (ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bytesOut);
             ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytesOut.toByteArray());
             ObjectInputStream in = new ObjectInputStream(bytesIn)) {
            out.writeObject(resources);
            return cast(in.readObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反射设置值
     *
     * @param o     bean对象
     * @param field field
     * @param v     值
     * @throws IllegalAccessException 异常
     */
    public static void setValue(Object o, Field field, Object v) throws IllegalAccessException {
        // 判断一下是否有值 有值的不做处理，没有值的 利用反射注入id
        if (field != null && o != null) {
            field.setAccessible(true);
            field.set(o, v);
        }
    }

    /**
     * 批量分批次新增 或者修改数据
     * 先把数据按照 500 分成多批次
     * 再从数据库根据联合主键查询数据，转成联合主键为key 对象为value的map
     * 再把集合遍历 分为更新的集合 和 修改的集合
     * 再分别批次更新或者修改
     *
     * @param list     原始数据
     * @param function 查询数据库的方法 通过list 参数返回一个map对象
     * @param isUpdFun 判断是否为新增的数据 如果是修改需要再函数里设置主键
     * @param service  service对象
     * @param <T>      实体类型
     * @param <S>      service
     */
    public static <T, S extends ServiceImpl<?, T>> Map<String, T> batchAddOrUpdate(List<T> list, Function<List<T>, Map<String, T>> function, BiFunction<T, Map<String, T>, Boolean> isUpdFun, S service) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        Map<String, T> map = new HashMap<>(64);
        List<Map<String, T>> partitions = batchList(list, function);
        if (CollectionUtils.isNotEmpty(partitions)) {
            partitions.forEach(map::putAll);
        }
        List<T> insertList = new ArrayList<>();
        List<T> updateList = new ArrayList<>();
        list.forEach(l -> {
            boolean isUpdate = isUpdFun.apply(l, map);
            if (isUpdate) {
                updateList.add(l);
            } else {
                insertList.add(l);
            }
        });
        if (CollectionUtils.isNotEmpty(insertList)) {
            service.saveBatch(insertList);
        }
        if (CollectionUtils.isNotEmpty(updateList)) {
            service.updateBatchById(updateList);
        }
        return map;
    }

    /**
     * 把数据分批次 处理 默认500
     * 把指定的数据安装批次大小去执行 比
     *
     * @param params   参数
     * @param consumer 处理
     * @param <T>      对象类型
     * @return 小批次的集合
     */
    public static <T> List<List<T>> executeBatch(Collection<T> params, Consumer<List<T>> consumer) {
        return executeBatch(params, BATCH_SIZE, c -> c, consumer);
    }

    /**
     * 把数据分批次 处理 默认500
     * 比如把一个大的集合按照指定大小转为 集合的集合
     * 批量查询数据库
     *
     * @param params   参数
     * @param function 处理过程
     * @param <T>      对象类型
     * @return 小批次的集合
     */
    public static <T, R> List<R> batchList(Collection<T> params, Function<List<T>, R> function) {
        return executeBatch(params, BATCH_SIZE, function, null);
    }

    /**
     * 把数据分批次 处理
     *
     * @param params    参数
     * @param batchSize 批次数量
     * @param function  处理过程
     * @param consumer  最后执行
     * @param <T>       对象类型
     * @return 小批次的集合
     */
    public static <T, R> List<R> executeBatch(Collection<T> params, int batchSize, Function<List<T>, R> function, Consumer<R> consumer) {
        List<T> subs = new ArrayList<>();
        List<R> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(params)) {
            int size = params.size();
            int idxLimit = Math.min(batchSize, size);
            int i = 1;
            for (T t : params) {
                subs.add(t);
                if (i == idxLimit) {
                    R r = function.apply(subs);
                    list.add(r);
                    if (consumer != null) {
                        consumer.accept(r);
                    }
                    subs = new ArrayList<>();
                    idxLimit = Math.min(idxLimit + batchSize, size);
                }
                i++;
            }
        }
        return list;
    }


    /**
     * 根据类型初始化对象
     *
     * @param clazz 对象类型
     * @param <T>   对象类型
     * @return 对象
     * @throws NoSuchMethodException     异常
     * @throws IllegalAccessException    异常
     * @throws InvocationTargetException 异常
     * @throws InstantiationException    异常
     */
    public static <T> T newInstance(Class<T> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return clazz.getDeclaredConstructor().newInstance();
    }

    /**
     * 转树形结构
     *
     * @param resources 原始集合
     * @param fun       转化过程
     * @param root      原始数据root节点的值
     * @param <T>       对象类型
     * @return 树形对象
     */
    public static <T> List<TreeSelect> toTree(List<T> resources, Function<T, TreeSelect> fun, String root) {
        if (CollectionUtils.isEmpty(resources)) {
            return null;
        }
        List<TreeSelect> list = toList(resources, fun);
        return toTree(list, root);
    }

    /**
     * 递归 转为树结构
     *
     * @param resources 原始集合
     * @param root      原始数据root节点的值
     * @param <T>       对象类型
     * @return 树形对象
     */
    public static <T extends TreeSelect> List<T> toTree(List<T> resources, String root) {
        // 递归循环查找
        List<T> tree = new ArrayList<>();
        boolean isRoot;
        for (T t : resources) {
            isRoot = StringUtils.isAllBlank(root, t.getParId()) || (StringUtils.isNotBlank(root) && root.equals(t.getParId()));
            if (isRoot) {
                tree.add(t);
            }
        }
        children(tree, resources);
        return tree;
    }

    /**
     * 递归获取树形结构
     *
     * @param tree    返回的树
     * @param sources 原始数据
     */
    private static <T extends TreeSelect> void children(List<T> tree, List<T> sources) {
        for (T sub : tree) {
            List<T> list = new ArrayList<>();
            for (T s : sources) {
                if (sub.getId().equals(s.getParId())) {
                    list.add(s);
                }
            }
            if (CollectionUtils.isNotEmpty(list)) {
                List<T> collect = list.stream().sorted(Comparator.comparing(TreeSelect::getSeqNo)).collect(Collectors.toList());
                sub.setChildren(collect);
                children(list, sources);
            }
        }
    }


    /**
     * 目的：从对象及其父类 反射获取对象的方法 <br/>
     * 可以获取多参数方法
     *
     * @param o          对象
     * @param methodName 对象方法
     * @param classTypes 对象参数类型
     * @return 方法对象
     */
    public static Method getMethod(Object o, String methodName, Class<?>... classTypes) {
        Method method = null;
        Class<?> clazz = o.getClass();
        for (Class<?> superClass = clazz; superClass != null; superClass = superClass.getSuperclass()) {
            try {
                if (ArrayUtils.isEmpty(classTypes)) {
                    method = superClass.getDeclaredMethod(methodName);
                } else {
                    method = superClass.getDeclaredMethod(methodName, classTypes);
                }
                break;
            } catch (NoSuchMethodException | SecurityException ignored) {
            }
        }
        return method;
    }

    /**
     * 反射得到方法
     *
     * @param clazz      类
     * @param methodName 方法名称
     * @return 方法对象
     */
    public static Method getMethod(Class<?> clazz, String methodName) {
        Method method = null;
        try {
            method = clazz.getMethod(methodName);
        } catch (NoSuchMethodException ignored) {
        }
        return method;
    }


    /**
     * 从HandlerMethod 获取到注解
     *
     * @param method HandlerMethod
     * @param clazz  注解类型
     * @param <T>    注解类型
     * @return 注解对象
     */
    public static <T extends Annotation> T getAnnotation(HandlerMethod method, Class<T> clazz) {
        T t = method.getBeanType().getAnnotation(clazz);
        if (t == null) {
            t = method.getMethod().getAnnotation(clazz);
        }
        return t;
    }
}
