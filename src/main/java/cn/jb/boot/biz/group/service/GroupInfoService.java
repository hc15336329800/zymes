package cn.jb.boot.biz.group.service;

import cn.jb.boot.biz.group.entity.GroupInfo;
import cn.jb.boot.biz.group.vo.request.GroupInfoCreateRequest;
import cn.jb.boot.biz.group.vo.request.GroupInfoPageRequest;
import cn.jb.boot.biz.group.vo.request.GroupInfoUpdateRequest;
import cn.jb.boot.biz.group.vo.response.GroupInfoInfoResponse;
import cn.jb.boot.biz.group.vo.response.GroupInfoPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.system.vo.response.DictListResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 工人分组 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-06 20:38:27
 */
public interface GroupInfoService extends IService<GroupInfo> {

    /**
     * 新增工人分组
     *
     * @param params 工人分组
     */
    void createInfo(GroupInfoCreateRequest params);

    /**
     * 查询详情
     *
     * @param id 主键
     * @return 对象
     */
    GroupInfoInfoResponse getInfoById(String id);

    /**
     * 修改工人分组
     *
     * @param params 工人分组
     */
    void updateInfo(GroupInfoUpdateRequest params);

    /**
     * 分页查询
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<GroupInfoPageResponse>> pageInfo(Paging page, GroupInfoPageRequest params);

    List<DictListResponse> selected();

}
