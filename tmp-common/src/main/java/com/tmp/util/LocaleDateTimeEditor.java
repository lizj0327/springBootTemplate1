package com.tmp.util;


import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;

/**
 * LocaleDateTime、LocaleDate、LocalTime的工具类，格式化为字符串，字符串转为对象
 * 
 */
public class LocaleDateTimeEditor extends PropertyEditorSupport {

    private final Class<? extends TemporalAccessor> classz;

    private final DateTimeFormatter formatter;

    private final boolean allowEmpty;

    public LocaleDateTimeEditor(Class<? extends TemporalAccessor> classz, String dateFormat, boolean allowEmpty) {

        this.classz = classz;
        this.formatter = DateTimeFormatter.ofPattern(dateFormat);
        formatter.withResolverStyle(ResolverStyle.STRICT);
        this.allowEmpty = allowEmpty;
    }

    @Override
    public String getAsText() {
        TemporalAccessor value = (TemporalAccessor) getValue();

        if (value == null) {
            return "";
        }

        return formatter.format(value);
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {

        if (allowEmpty && StringUtils.isBlank(text)) {
            setValue(null);
        } else {
            setValue(formatter.parse(text));
        }
    }

    @Override
    public void setValue(Object value) {

        if (value instanceof TemporalAccessor) {

            if (classz.equals(LocalDateTime.class)) {

                super.setValue(LocalDateTime.from((TemporalAccessor) value));
            } else if (classz.equals(LocalDate.class)) {

                super.setValue(LocalDate.from((TemporalAccessor) value));
            } else if (classz.equals(LocalTime.class)) {

                super.setValue(LocalTime.from((TemporalAccessor) value));
            }

        } else {
            super.setValue(value);
        }
    }

}