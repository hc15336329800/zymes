package cn.jb.boot.biz.sales.service;

import javax.servlet.http.HttpServletResponse;

public interface ExportDeliveryService {
    void export(String ids, HttpServletResponse response);

    void exportMain(String id, HttpServletResponse response);
}
