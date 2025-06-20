package cn.jb.boot.biz.item.enums;



/**
 * 物料来源枚举
 */
public enum  EnumItemOrigin {
	SELF("0", "自制"),
	PURCHASE("1", "采购"),
	OUTSOURCING("2", "外协"),
	CUSTOMER_SUPPLIED("3", "客供"),
	OTHER("9", "其他");

	private final String code;
	private final String desc;

	EnumItemOrigin(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	/**
	 * 根据ERP原始数据转换
	 */
	public static EnumItemOrigin fromErpSource(String erpSource) {
		switch (erpSource) {
			case "0": return SELF;
			case "1": return PURCHASE;
			case "2": return OUTSOURCING;
			case "3": return CUSTOMER_SUPPLIED;
			default: return OTHER;
		}
	}
}
