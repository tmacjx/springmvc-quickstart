package com.bokecc.util;

/**
 * 格式化风格
 */

public enum TimeStyle {

    /**  **/
    HORIZONTAL_LINE("yyyy-MM-dd"),

    POINT("yyyy.MM.dd"),

    TIME_HORIZONTAL_LINE("yyyy-MM-dd HH:mm:ss"),

    TIME_POINT("yyyy.MM.dd HH:mm:ss"),

    NOMAL_LINE("yyyyMMdd"),

    TIME_NOMAL_LINE("yyyyMMddHHmmss"),

    TIME_NOMAL_LINE_MILLI("yyyyMMddHHmmssSSS");

    private String node;

    private TimeStyle(String node){

        this.node = node;
    }

    public String getNode() {
        return node;
    }
}
