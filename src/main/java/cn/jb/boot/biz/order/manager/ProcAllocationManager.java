package cn.jb.boot.biz.order.manager;


import cn.jb.boot.biz.order.model.ProcAllocationReport;
import cn.jb.boot.biz.work.entity.WorkReport;

import java.util.List;

public interface ProcAllocationManager {
    /**
     * 更新外协报工
     *
     * @param requests
     */
    void updateOuterReport(List<ProcAllocationReport> requests);

    void updateWorkReport(List<WorkReport> list);
}
