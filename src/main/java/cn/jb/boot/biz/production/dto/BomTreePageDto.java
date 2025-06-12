package cn.jb.boot.biz.production.dto;

import lombok.Data;

@Data
public class BomTreePageDto {
	private String bomNo;

	private Integer pageNum = 1;
	private Integer pageSize = 10;
}
