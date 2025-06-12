package cn.jb.boot.system.service;

import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.system.entity.DictData;
import cn.jb.boot.system.vo.response.DictListResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DictDataService extends IService<DictData> {
    List<DictData> allDict();

    List<DictListResponse> dictList(String type);

}
