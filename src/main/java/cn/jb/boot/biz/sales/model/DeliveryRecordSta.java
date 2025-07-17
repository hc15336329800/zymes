package cn.jb.boot.biz.sales.model;

import lombok.Data;

import java.util.List;

/**
 * @author user
 * @description
 * @date 2022年10月01日 23:23
 */
@Data
public class DeliveryRecordSta {
    private String itemNo;
    private List<DeliveryRecordStaSusb> list;
}
