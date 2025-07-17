package cn.jb.boot.biz.item.dto;

import java.math.BigDecimal;

// 扁平树: 用于接收递归 CTE 查询结果   (一次查询 + 内存组装)
public class UseItemTreeRow {
	private String itemNo;       // 根节点或父节点编码
	private String useItemNo;    // 本节点编码（子项）
	private String parentCode;   // 上级编码
	private Long usedId;
	private BigDecimal fixedUsed;
	private String bomNo;
	private String itemType;
	private String itemName;     // 从 mes_item_stock 拿到的物料名称

	// Getters and Setters
	public String getItemNo() {
		return itemNo;
	}

	public void setItemNo(String itemNo) {
		this.itemNo = itemNo;
	}

	public String getUseItemNo() {
		return useItemNo;
	}

	public void setUseItemNo(String useItemNo) {
		this.useItemNo = useItemNo;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public Long getUsedId() {
		return usedId;
	}

	public void setUsedId(Long usedId) {
		this.usedId = usedId;
	}

	public BigDecimal getFixedUsed() {
		return fixedUsed;
	}

	public void setFixedUsed(BigDecimal fixedUsed) {
		this.fixedUsed = fixedUsed;
	}

	public String getBomNo() {
		return bomNo;
	}

	public void setBomNo(String bomNo) {
		this.bomNo = bomNo;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	// Optional: toString() method for debugging/logging
	@Override
	public String toString() {
		return "UseItemTreeRow{" +
				"itemNo='" + itemNo + '\'' +
				", useItemNo='" + useItemNo + '\'' +
				", parentCode='" + parentCode + '\'' +
				", usedId=" + usedId +
				", fixedUsed=" + fixedUsed +
				", bomNo='" + bomNo + '\'' +
				", itemType='" + itemType + '\'' +
				", itemName='" + itemName + '\'' +
				'}';
	}
}
