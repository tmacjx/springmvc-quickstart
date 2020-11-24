package com.bokecc.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 计算两个时间的间隔
 */
public class TimeInterval {

    /**
     * 线程安全型 用于时间格式化
     **/
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 默认的时区
     **/
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");

    private final static int DAY_MILLIS = 86400000;

    /** 计算日期之间间隔天数 **/
    public static int interval(Date preDate, Date nextDate){

        double day = (nextDate.getTime() - preDate.getTime())/DAY_MILLIS;

        return (int)day;
    }

    /** 计算日期之间间隔天数 **/
    public static int interval(String preDate, String nextDate){

        LocalDate pre = LocalDate.parse(preDate, DATE_FORMATTER);

        long preLong = pre.atStartOfDay().atZone(ZONE_ID).toInstant().toEpochMilli();

        LocalDate next = LocalDate.parse(nextDate, DATE_FORMATTER);

        long nextLong = next.atStartOfDay().atZone(ZONE_ID).toInstant().toEpochMilli();

        return (int)((nextLong - preLong)/DAY_MILLIS);
    }

    public static String getTime(int hours){

        LocalDateTime dateTime = LocalDateTime.now(ZONE_ID);

        return TimeFormatter.getTime(dateTime.plus(hours, ChronoUnit.HOURS));
    }
}
