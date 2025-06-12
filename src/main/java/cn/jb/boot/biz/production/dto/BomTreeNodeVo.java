package cn.jb.boot.biz.production.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class BomTreeNodeVo {
	private String id;
	private String itemNo;
	private String useItemNo;
	private String parentCode;
	private BigDecimal useItemCount;
	private String itemName;  // 物料名称
	private String bomNo;     // BOM 编码

	private List<BomTreeNodeVo> children = new ArrayList<>();
}
