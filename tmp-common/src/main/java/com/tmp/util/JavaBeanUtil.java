package com.tmp.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Enums;
import com.google.common.base.Optional;

/**
 * javaBean的基本构成字符串转换方法
 * 
 * @author Wesley
 * 
 */
public class JavaBeanUtil {

    private static final char SEPARATOR = '_';

    /**
     * 将驼峰样式的属性样式字符串转成带下滑线全小写的字符串<br>
     * (例:branchNo -> branch_no)<br>
     * 
     * @param inputString
     * @return
     */
    public static String toUnderlineString(String inputString) {
        if (inputString == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < inputString.length(); i++) {
            char c = inputString.charAt(i);

            boolean nextUpperCase = true;

            if (i < (inputString.length() - 1)) {
                nextUpperCase = Character.isUpperCase(inputString.charAt(i + 1));
            }

            if ((i >= 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    if (i > 0) {
                        sb.append(SEPARATOR);
                    }
                }
                upperCase = true;
            } else {
                upperCase = false;
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * 将属性字符串转成驼峰字段<br>
     * (例:branch_no -> branchNo )<br>
     * 
     * @param inputString
     *            输入字符串
     * @return
     */
    public static String toCamelCaseString(String inputString) {
        return toCamelCaseString(inputString, false);
    }

    /**
     * 将驼峰字段转成属性字符串<br>
     * (例:branch_no -> branchNo )<br>
     * 
     * @param inputString
     *            输入字符串
     * @param firstCharacterUppercase
     *            是否首字母大写
     * @return
     */
    public static String toCamelCaseString(String inputString, boolean firstCharacterUppercase) {
        if (inputString == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean nextUpperCase = false;
        for (int i = 0; i < inputString.length(); i++) {
            char c = inputString.charAt(i);

            switch (c) {
            case '_':
            case '-':
            case '@':
            case '$':
            case '#':
            case ' ':
            case '/':
            case '&':
                if (sb.length() > 0) {
                    nextUpperCase = true;
                }
                break;

            default:
                if (nextUpperCase) {
                    sb.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
                break;
            }
        }

        if (firstCharacterUppercase) {
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        }

        return sb.toString();
    }

    /**
     * 得到标准字段名称<br>
     * 
     * @param inputString
     *            输入字符串
     * @return
     */
    public static String getValidPropertyName(String inputString) {

        String answer;
        if (inputString == null) {
            answer = null;
        } else if (inputString.length() < 2) {
            answer = inputString.toLowerCase(Locale.US);
        } else {
            if (Character.isUpperCase(inputString.charAt(0)) && !Character.isUpperCase(inputString.charAt(1))) {
                answer = inputString.substring(0, 1).toLowerCase(Locale.US) + inputString.substring(1);
            } else {
                answer = inputString;
            }
        }
        return answer;
    }

    /**
     * 将属性转换成标准set方法名字符串<br>
     * 
     * @param property
     * @return
     */
    public static String getSetterMethodName(String property) {
        StringBuilder sb = new StringBuilder();

        sb.append(property);
        if (Character.isLowerCase(sb.charAt(0))) {
            if ((sb.length() == 1) || !Character.isUpperCase(sb.charAt(1))) {
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            }
        }
        sb.insert(0, "set");
        return sb.toString();
    }

    /**
     * 将属性转换成标准get方法名字符串<br>
     * 
     * @param property
     * @return
     */
    public static String getGetterMethodName(String property) {
        StringBuilder sb = new StringBuilder();

        sb.append(property);
        if (Character.isLowerCase(sb.charAt(0))) {
            if ((sb.length() == 1) || !Character.isUpperCase(sb.charAt(1))) {
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            }
        }
        sb.insert(0, "get");
        return sb.toString();
    }

    // public static void main(String[] args) {
    // System.out.println(JavaBeanUtil.toUnderlineString("ISOCertifiedStaff"));
    // System.out.println(JavaBeanUtil.getValidPropertyName("CertifiedStaff_name"));
    // System.out.println(JavaBeanUtil.getSetterMethodName("userID"));
    // System.out.println(JavaBeanUtil.getGetterMethodName("userID"));
    // System.out.println(JavaBeanUtil.toCamelCaseString("iso_certified_staff", true));
    // System.out.println(JavaBeanUtil.getValidPropertyName("AAertified_staff"));
    // System.out.println(JavaBeanUtil.toCamelCaseString("site_Id"));
    // }


    /**
     * 对象为指定类型的javabean，特别处理字符串转其他类型
     *
     * @param obj 对象值
     * @param entityClazz 实体类型
     * @return
     */
    public static <T> Optional<T> convertObjectFromObject(Object obj, final Class<T> entityClazz) {

        if (obj == null ) {
            return null;
        }

        if(obj instanceof String){
            return convertObjectFromString(obj.toString(), entityClazz);
        }

        return Optional.of((T) obj);
    }



    /**
     * 字符串转换为指定类型的javabean
     * 
     * @param str 字符串类型的值
     * @param entityClazz 实体类型
     * @return
     */
    public static <T> Optional<T> convertObjectFromString(String str, final Class<T> entityClazz) {

        if (StringUtils.isBlank(str) || (entityClazz == String.class)) {
            return Optional.of((T) str);
        }

        // 枚举类型
        if (entityClazz.isEnum()) {

            Optional optional = Enums.getIfPresent((Class<Enum>) entityClazz.asSubclass(Enum.class), str);
            return optional.isPresent() ? Optional.of((T) optional.get()) : (Optional<T>) Optional.absent();
            // return (T) EnumUtils.getEnum(entityClazz.asSubclass(Enum.class), str);
        }

        // 判断类型
        if (entityClazz == Boolean.class) {
            Boolean value = false;
            if (StringUtils.equalsIgnoreCase(str, "true") || StringUtils.equalsIgnoreCase(str, "1")) {
                value = true;
            }
            return Optional.of((T) value);
        }

        if (entityClazz == LocalDate.class) {
            return Optional.of((T) LocalDate.parse(str, ClockUtil.LOCAL_DATE_FORMATTER));
        } else if (entityClazz == LocalDateTime.class) {
            return Optional.of((T) LocalDateTime.parse(str, ClockUtil.LOCAL_DATE_TIME_FORMATTER));
        } else if (entityClazz == LocalTime.class) {
            return Optional.of((T) LocalTime.parse(str, ClockUtil.LOCAL_TIME_FORMATTER));
        } else if (entityClazz == Integer.class) {
            return Optional.of((T) Integer.valueOf(str));
        } else if (entityClazz == Long.class) {
            return Optional.of((T) Long.valueOf(str));
        } else if (entityClazz == Float.class) {
            return Optional.of((T) Float.valueOf(str));
        }

        return Optional.of((T) str);
    }

}
