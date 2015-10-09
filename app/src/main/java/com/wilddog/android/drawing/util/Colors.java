package com.wilddog.android.drawing.util;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 颜色转换
 * Created by jale on 15/10/8.
 */
public class Colors {

    private final static Map<String, Integer> colors = new HashMap<>();

    private static String current = null;

    static {
        colors.put("fff", Color.rgb(255, 255, 255));
        colors.put("000", Color.rgb(0, 0, 0));
        colors.put("f00", Color.rgb(255, 0, 0));
        colors.put("0f0", Color.rgb(0, 255, 0));
        colors.put("00f", Color.rgb(0, 0, 255));
        colors.put("88f", Color.rgb(136, 136, 255));
        colors.put("f8d", Color.rgb(255, 136, 221));
        colors.put("f88", Color.rgb(255, 136, 136));
        colors.put("f05", Color.rgb(255, 0, 85));
        colors.put("f80", Color.rgb(255, 136, 0));
        colors.put("0f8", Color.rgb(0, 255, 136));
        colors.put("cf0", Color.rgb(204, 255, 0));
        colors.put("08f", Color.rgb(0, 136, 255));
        colors.put("408", Color.rgb(68, 0, 136));
        colors.put("ff8", Color.rgb(255, 255, 136));
        colors.put("8ff", Color.rgb(136, 255, 255));
    }


    public static int use(String key) {
        current = key;
        return colors.get(key);
    }

    public static String current() {
        return current == null ? "000" : current;
    }

    public static Set<Map.Entry<String, Integer>> all() {
        return colors.entrySet();
    }
}
