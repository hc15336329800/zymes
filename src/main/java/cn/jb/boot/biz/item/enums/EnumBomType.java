package cn.jb.boot.biz.item.enums;


/**
 * BOM类型枚举
 */
public enum EnumBomType {
	PRODUCTION("10", "生产BOM"),
	ENGINEERING("20", "工程BOM"),
	COSTING("30", "成本BOM"),
	PLANNING("40", "计划BOM");

	private final String code;
	private final String desc;

	EnumBomType(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}
}
