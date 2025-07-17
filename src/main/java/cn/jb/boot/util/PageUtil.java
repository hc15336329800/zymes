package cn.jb.boot.util;

import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.com.response.PagingResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * @author YX
 * @Description 封装分页信息
 * @Date 2021/8/18 0:08
 */
@FunctionalInterface
public interface PageUtil<T, R> {

    /**
     * 封装分页对象
     *
     * @param info
     * @param r
     * @return 查询集合
     */
    default BaseResponse<List<T>> page(Paging info, R r) {
        BaseResponse<List<T>> response = new BaseResponse<>();
        if (info != null) {
            int pageSize = info.getPageSize();
            Page<T> p = new Page<>(info.getPageNum(), pageSize);
            IPage<T> pageData = datas(p, r);
            PagingResponse page = new PagingResponse();
            page.setPageNum(pageData.getCurrent());
            page.setPages(pageData.getPages());
            page.setPageSize(pageSize);
            page.setTotalNum(pageData.getTotal());
            response.setPage(page);
            response.setData(pageData.getRecords());
        }
        return response;
    }

    /**
     * 获取数据集合
     *
     * @return
     */

    IPage<T> datas(Page<T> page, R r);
}
