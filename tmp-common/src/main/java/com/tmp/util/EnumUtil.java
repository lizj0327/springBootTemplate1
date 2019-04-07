package com.tmp.util;

import java.util.EnumSet;

/**
 * 枚举的工具类
 */
public class EnumUtil {

    /**
     * 判断是否属于某个枚举类的枚举
     *
     * @param name        枚举名称
     * @param elementType 枚举的类型
     * @return
     */
    public static <E extends Enum<E>> boolean isInclude(String name, Class<E> elementType) {

        boolean include = false;
        for (Enum<E> e : EnumSet.allOf(elementType)) {
            if (e.name().equals(name)) {
                include = true;
                break;
            }
        }
        return include;
    }

}
