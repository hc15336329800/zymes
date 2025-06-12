package cn.jb.boot.util;

import java.util.HashMap;
import java.util.Map;

public class AutoDeviceCache {
    private static Map<String, String> map = new HashMap<String, String>() {
        {
            put("192.168.3.40", "4号数控冲");
            put("192.168.3.43", "7号数控冲");
            put("192.168.3.44", "北校平机");
            put("192.168.3.41", "5号数控冲");
            put("192.168.3.42", "6号数控冲");
            put("192.168.3.45", "10号数控冲");
            put("192.168.3.46", "11号数控冲");
            put("192.168.3.47", "1600冲压机");
            put("192.168.3.48", "3000冲压机");
            put("192.168.3.49", "400冲压机");
            put("192.168.3.60", "2号数控冲");
            put("192.168.3.61", "1号剪板机");
            put("192.168.3.62", "2号剪板机");
            put("192.168.3.63", "4号剪板机");
            put("192.168.3.64", "老4米剪板机");
        }
    };


    public static Map<String, String> getMap() {
        return map;
    }
}
