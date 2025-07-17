package cn.jb.boot.biz.production.model;

import lombok.Data;

import java.util.List;

@Data
public class OrderSchedule {
    private List<String> pids;
    private String uid;
}
