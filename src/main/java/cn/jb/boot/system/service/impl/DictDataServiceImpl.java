package cn.jb.boot.system.service.impl;

import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.system.entity.DictData;
import cn.jb.boot.system.mapper.DictDataMapper;
import cn.jb.boot.system.service.DictDataService;
import cn.jb.boot.system.vo.DictDataVo;
import cn.jb.boot.system.vo.response.DictListResponse;
import cn.jb.boot.util.DictUtil;
import cn.jb.boot.util.PojoUtil;
import cn.jb.boot.util.SnowFlake;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@DS("db1")
public class DictDataServiceImpl extends ServiceImpl<DictDataMapper, DictData> implements DictDataService {
    @Override
    public List<DictData> allDict() {
        return this.list(new LambdaQueryWrapper<DictData>().orderByAsc(DictData::getDictType));
    }

    @Override
    public List<DictListResponse> dictList(String type) {
        List<DictDataVo> list = DictUtil.getDictCache(DictType.byCode(type));
        if (CollectionUtils.isNotEmpty(list)) {
            list = list.stream().sorted(Comparator.comparing(DictDataVo::getDictSort)).collect(Collectors.toList());
        }
        return PojoUtil.toList(list, (d) -> new DictListResponse(d.getDictValue(), d.getDictLabel()));
    }


}
