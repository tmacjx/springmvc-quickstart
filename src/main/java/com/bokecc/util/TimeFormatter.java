package com.bokecc.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 时间格式化（jdk1.8）
 */
public class TimeFormatter {

    /**
     * 线程安全型 用于时间格式化 日期
     **/
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 线程安全型 用于时间格式化 日期+时间
     **/
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 默认的时区
     **/
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");

    /**
     * 返回格式化后的日期+时间 指定格式化的风格
     **/
    public static String getTime(TimeStyle style) {

        DateTimeFormatter dateTimeFormatter = null;

        switch (style) {

            case TIME_HORIZONTAL_LINE:

                dateTimeFormatter = DateTimeFormatter.ofPattern(style.getNode());

                return dateTimeFormatter.format(LocalDateTime.now(ZONE_ID));

            case TIME_POINT:

                dateTimeFormatter = DateTimeFormatter.ofPattern(style.getNode());

                return dateTimeFormatter.format(LocalDateTime.now(ZONE_ID));

            case TIME_NOMAL_LINE_MILLI:

                dateTimeFormatter = DateTimeFormatter.ofPattern(style.getNode());

                return dateTimeFormatter.format(LocalDateTime.now(ZONE_ID));

            default:

                return null;
        }
    }

    /**
     * 返回格式化后的日期+时间 (固定格式效率较高)
     **/
    public static String getTime() {

        return TIME_FORMATTER.format(LocalDateTime.now(ZONE_ID));
    }

    /**
     * 返回格式化后的日期  指定格式化的风格
     **/
    public static String getDate(TimeStyle style) {

        DateTimeFormatter dateTimeFormatter = null;

        switch (style) {

            case HORIZONTAL_LINE:

                dateTimeFormatter = DateTimeFormatter.ofPattern(style.getNode());

                return dateTimeFormatter.format(LocalDateTime.now(ZONE_ID));

            case POINT:

                dateTimeFormatter = DateTimeFormatter.ofPattern(style.getNode());

                return dateTimeFormatter.format(LocalDateTime.now(ZONE_ID));

            case NOMAL_LINE:

                dateTimeFormatter = DateTimeFormatter.ofPattern(style.getNode());

                return dateTimeFormatter.format(LocalDateTime.now(ZONE_ID));

            case TIME_HORIZONTAL_LINE:

                dateTimeFormatter = DateTimeFormatter.ofPattern(style.getNode());

                return dateTimeFormatter.format(LocalDateTime.now(ZONE_ID));

            default:

                return null;

        }
    }

    /**
     * 返回格式化后的日期 (固定格式效率较高)
     **/
    public static String getDate() {

        return DATE_FORMATTER.format(LocalDate.now(ZONE_ID));
    }

    /**
     * 返回指定时间的格式化字符串 (固定格式效率较高)
     **/
    public static String getTime(Date date) {

        Instant instant = date.toInstant();

        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZONE_ID);

        return TIME_FORMATTER.format(localDateTime);
    }

    public static String getTime(LocalDateTime localDateTime) {

        return TIME_FORMATTER.format(localDateTime);
    }

    /**
     * 返回指定时间的格式化字符串 指定格式化的风格
     **/
    public static String getTime(Date date, TimeStyle style) {

        DateTimeFormatter dateTimeFormatter = null;

        Instant instant = date.toInstant();

        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZONE_ID);

        switch (style) {

            case TIME_HORIZONTAL_LINE:

                dateTimeFormatter = DateTimeFormatter.ofPattern(style.getNode());

                return dateTimeFormatter.format(localDateTime);

            case TIME_POINT:

                dateTimeFormatter = DateTimeFormatter.ofPattern(style.getNode());

                return dateTimeFormatter.format(localDateTime);

            default:

                return null;
        }
    }

    /**
     * 返回指定时间的格式化字符串 (固定格式效率较高)
     **/
    public static String getDate(Date date) {

        Instant instant = date.toInstant();

        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZONE_ID);

        return DATE_FORMATTER.format(localDateTime);
    }

    /**
     * 返回指定时间的格式化字符串 指定格式化的风格
     **/
    public static String getDate(Date date, TimeStyle style) {

        Instant instant = date.toInstant();

        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZONE_ID);

        DateTimeFormatter dateTimeFormatter = null;

        switch (style) {

            case HORIZONTAL_LINE:

                dateTimeFormatter = DateTimeFormatter.ofPattern(style.getNode());

                return dateTimeFormatter.format(localDateTime);

            case POINT:

                dateTimeFormatter = DateTimeFormatter.ofPattern(style.getNode());

                return dateTimeFormatter.format(localDateTime);

            default:

                return null;

        }
    }
}