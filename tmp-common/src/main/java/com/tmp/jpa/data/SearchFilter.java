package com.tmp.jpa.data;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

public class SearchFilter {

    /**
     * 查询操作类型
     */
    public enum Operator {
        /**
         * 等于
         */
        EQ,

        /**
         * 模糊查询
         */
        LIKE,

        /**
         * 大于
         */
        GT,

        /**
         * 小于
         */
        LT,

        /**
         * 大于等于
         */
        GTE,

        /**
         * 小于登录
         */
        LTE,

        /**
         * 包含
         */
        IN,

        /**
         * 为NULL
         */
        ISNULL,

        /**
         * 不为NULL
         */
        ISNOTNULL,
    }

    public String fieldName;
    public Object value;
    public Operator operator;

    /**
     * @param fieldName 实体字典名称
     * @param operator  操作类型
     * @param value     值
     */
    public SearchFilter(String fieldName, Operator operator, Object value) {
        this.fieldName = fieldName;
        this.value = value;
        this.operator = operator;
    }

    /**
     * searchParams中key的格式为OPERATOR_FIELDNAME。如：EQ_id
     */
    public static Map<String, SearchFilter> parse(Map<String, Object> searchParams) {
        Map<String, SearchFilter> filters = Maps.newHashMap();

        for (Entry<String, Object> entry : searchParams.entrySet()) {

            String key = entry.getKey();
            Object value = entry.getValue();

            // 拆分operator与filedAttribute
            String[] names = StringUtils.split(key, "_");
            if (names.length != 2) {
                throw new IllegalArgumentException(key + " is not a valid search filter name");
            }
            Operator operator = Operator.valueOf(names[0]);
            String filedName = names[1];

            if (operator != Operator.ISNULL && operator != Operator.ISNOTNULL) {
                // 如果不是isnull、isnotnull则过滤掉null、空字符。要保留空格字符
                if (Objects.isNull(value) || StringUtils.isEmpty(String.valueOf(value))) {
                    continue;
                }
            }

            // 创建searchFilter
            SearchFilter filter = new SearchFilter(filedName, operator, value);
            filters.put(key, filter);
        }

        return filters;
    }
}
