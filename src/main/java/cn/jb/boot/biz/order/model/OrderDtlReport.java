package cn.jb.boot.biz.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDtlReport {


    private String orderDtlId;

    private BigDecimal count;

    private String procedureCode;


}
