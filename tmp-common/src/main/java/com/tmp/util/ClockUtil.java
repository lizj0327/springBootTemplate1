package com.tmp.util;

/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期提供者，使用它而不是直接取得系统时间，方便测试。
 *
 * java.time.ClockUtil
 */
public interface ClockUtil {

    ClockUtil DEFAULT = new DefaultClockUtil();

    /**
     * 当前时间
     *
     * @return
     */
    LocalDateTime getCurrentDate();

    /**
     * 当前时间戳
     *
     * @return
     */
    long getCurrentTimeInMillis();

    String DEFAULT_TIME_ZONE = "GMT+08:00";
    String LOCAL_DATE_TIME_FORMATTER_PATTERN = "yyyy-MM-dd HH:mm:ss";
    String LOCAL_DATE_TIME_NO_SECOND_FORMATTER_PATTERN = "yyyy-MM-dd HH:mm";
    String LOCAL_DATE_FORMATTER_PATTERN = "yyyy-MM-dd";
    String LOCAL_TIME_FORMATTER_PATTERN = "HH:mm:ss";
    String LOCAL_HOUR_FORMATTER_PATTERN = "HH:mm";
    DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMATTER_PATTERN);
    DateTimeFormatter LOCAL_DATE_TIME_NO_SECOND_FORMATTER = DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_NO_SECOND_FORMATTER_PATTERN);
    DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern(LOCAL_DATE_FORMATTER_PATTERN);
    DateTimeFormatter LOCAL_TIME_FORMATTER = DateTimeFormatter.ofPattern(LOCAL_TIME_FORMATTER_PATTERN);
    DateTimeFormatter LOCAL_HOUR_FORMATTER = DateTimeFormatter.ofPattern(LOCAL_HOUR_FORMATTER_PATTERN);

    /**
     * 默认时间提供者，返回当前的时间，线程安全。
     */
    class DefaultClockUtil implements ClockUtil {

        @Override
        public LocalDateTime getCurrentDate() {

            return LocalDateTime.now();
        }

        @Override
        public long getCurrentTimeInMillis() {
            return System.currentTimeMillis();
        }
    }



}
