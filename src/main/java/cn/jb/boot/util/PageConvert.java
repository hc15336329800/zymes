package cn.jb.boot.util;

import cn.jb.boot.framework.com.response.BaseResponse;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * 分页转化
 *
 * @author user
 * @description 分页转化
 * @date 2022年09月28日 11:36
 */
public class PageConvert {

    /**
     * 将entity的分页对象转为 vo
     *
     * @return vo分页
     * @Param 分页数据
     **/
    public static <T, R> BaseResponse<List<T>> convert(BaseResponse<List<R>> sources, Class<T> clazz, BiConsumer<T, R> consumer) {
        List<R> list = sources.getData();
        BaseResponse<List<T>> response = new BaseResponse<>();
        List<T> datas = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(e -> {
                T t = PojoUtil.copyBean(e, clazz);
                if (consumer != null) {
                    consumer.accept(t, e);
                }
                datas.add(t);
            });
        }
        response.setData(datas);
        response.setPage(sources.getPage());
        return response;
    }

    /**
     * 将entity的分页对象转为 vo
     *
     * @return vo分页
     * @Param 分页数据
     **/
    public static <T, R> BaseResponse<List<T>> convert(BaseResponse<List<R>> sources, Class<T> clazz) {
        return convert(sources, clazz, null);
    }

}
