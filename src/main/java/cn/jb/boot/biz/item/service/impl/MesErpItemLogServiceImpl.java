package cn.jb.boot.biz.item.service.impl;

import cn.jb.boot.biz.item.entity.MesErpItemLog;
import cn.jb.boot.biz.item.mapper.MesErpItemLogMapper;
import cn.jb.boot.biz.item.service.MesErpItemLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * mes与ERP物料出入库流水表 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 16:55:58
 */
@Service
public class MesErpItemLogServiceImpl extends ServiceImpl<MesErpItemLogMapper, MesErpItemLog> implements MesErpItemLogService {

    @Resource
    private MesErpItemLogMapper mapper;

}
