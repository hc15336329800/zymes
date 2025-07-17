package cn.jb.boot.biz.group.service;

import cn.jb.boot.biz.group.entity.GroupDtl;
import cn.jb.boot.biz.group.vo.request.GroupDtlCreateRequest;
import cn.jb.boot.biz.group.vo.request.GroupDtlPageRequest;
import cn.jb.boot.biz.group.vo.request.GroupDtlUpdateRequest;
import cn.jb.boot.biz.group.vo.response.GroupDtlInfoResponse;
import cn.jb.boot.biz.group.vo.response.GroupDtlPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 分组明细 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-06 20:38:27
 */
public interface GroupDtlService extends IService<GroupDtl> {

    /**
     * 新增分组明细
     *
     * @param params 分组明细
     */
    void createInfo(GroupDtlCreateRequest params);

    /**
     * 查询详情
     *
     * @param id 主键
     * @return 对象
     */
    GroupDtlInfoResponse getInfoById(String id);

    /**
     * 修改分组明细
     *
     * @param params 分组明细
     */
    void updateInfo(GroupDtlUpdateRequest params);

    /**
     * 分页查询
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<GroupDtlPageResponse>> pageInfo(Paging page, GroupDtlPageRequest params);
}
