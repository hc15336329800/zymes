package cn.jb.boot.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@Slf4j
public class AssistUtil {

    /**
     * 获取辅助用料
     * @param model
     * @param count
     * @return
     */
    public static BigDecimal getAssistCount(String model, BigDecimal count) {
        try {
            if (StringUtils.isNotEmpty(model) && model.contains("*")) {
                String[] split = model.split("\\*");
                //钢铁密度 7.85kg/m3
                double density = 7.85;
                double v1 = Double.parseDouble(split[0]) / 1000 * Double.parseDouble(split[1]) / 1000 * Double.parseDouble(split[2]) * density;
                double v = count.doubleValue() / v1;
                return BigDecimal.valueOf(Math.ceil(v));
            }
        } catch (Exception e) {
            log.error("转换数据异常 e", e);
        }

        return count;
    }




}
