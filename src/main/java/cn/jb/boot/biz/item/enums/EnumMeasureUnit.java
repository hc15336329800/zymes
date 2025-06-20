package cn.jb.boot.biz.item.enums;


import java.math.BigDecimal;

/**
 * 计量单位枚举（常用单位）
 */
public enum EnumMeasureUnit {
	PIECE("PCS", "件"),
	KILOGRAM("KG", "千克"),
	METER("M", "米"),
	LITER("L", "升"),
	BOX("BOX", "箱"),
	SET("SET", "套");

	private final String code;
	private final String desc;

	EnumMeasureUnit(String code, String desc) {
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
	 * 获取单位对应的标准转换系数（用于单位换算）
	 */
	public BigDecimal getConversionFactor() {
		switch (this) {
			case KILOGRAM: return new BigDecimal("1");
			case METER: return new BigDecimal("1");
			default: return BigDecimal.ONE;
		}
	}
}
