package cn.jb.boot.biz.item.enums;


/**
 * 物料状态枚举
 */
public enum  EnumItemStatus {

	ACTIVE("01", "生效"),
	INACTIVE("00", "失效"),
	PENDING("02", "待审核"),
	OBSOLETE("03", "已淘汰");

	private final String code;
	private final String desc;

	EnumItemStatus(String code, String desc) {
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
