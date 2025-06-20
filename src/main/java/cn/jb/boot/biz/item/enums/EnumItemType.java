package cn.jb.boot.biz.item.enums;

/**
 * 物料类型枚举
 */
public enum EnumItemType {
	SELF_MADE("00", "自制件"),
	PURCHASE("01", "采购件"),
	SEMI_FINISHED("02", "半成品"),
	RAW_MATERIAL("03", "原材料"),
	FINISHED_PRODUCT("04", "产成品"),
	AUXILIARY_MATERIAL("05", "辅助材料");

	private final String code;
	private final String desc;

	EnumItemType(String code, String desc) {
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
	 * 根据编码获取枚举
	 */
	public static EnumItemType getByCode(String code) {
		for (EnumItemType value : values()) {
			if (value.code.equals(code)) {
				return value;
			}
		}
		return null;
	}

	/**
	 * 判断是否为采购件
	 */
	public static boolean isPurchase(String code) {
		return PURCHASE.code.equals(code);
	}

	/**
	 * 判断是否为自制件
	 */
	public static boolean isSelfMade(String code) {
		return SELF_MADE.code.equals(code);
	}
}
