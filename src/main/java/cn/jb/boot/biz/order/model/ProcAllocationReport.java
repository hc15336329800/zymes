package cn.jb.boot.biz.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcAllocationReport {


    private String pid;

    private BigDecimal count;


}
