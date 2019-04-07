package com.tmp.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式的工具类
 *
 */
public class RegExUtils {

    /**
     * 功能描述：判断是否为整数
     *
     * @param str
     *            传入的字符串
     * @return 是整数返回true,否则返回false
     */
    public static boolean isInteger(String str) {
        return str.matches("^[+-]?\\d+$");
    }

    /**
     * 判断是否为浮点数，包括double和float
     *
     * @param str
     *            传入的字符串
     * @return 是浮点数返回true,否则返回false
     */
    public static boolean isDouble(String str) {
        return str.matches("^[+-]?\\d+(\\.\\d+$)?");
    }

    /**
     * 字母数字下划线,并以字母开头
     */
    public static boolean isSafe(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        return str.matches("[\\w]{4,}");
    }

    /**
     * 功能描述：判断输入的字符串是否符合Email样式.
     *
     * @param email
     *            传入的字符串
     * @return 是Email样式返回true,否则返回false
     */
    public static boolean isEmail(String email) {
        if (email == null || email.length() < 1 || email.length() > 256) {
            return false;
        }
        return email.matches("^[-+.\\w]+@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
    }

    /**
     * 功能描述：判断输入的字符串是否为纯汉字
     *
     * @param str
     *            传入的字符窜
     * @return 如果是纯汉字返回true,否则返回false
     */
    public static boolean isChinese(String str) {
        return str.matches("^[\u0391-\uFFE5]+$");
    }

    /**
     * 功能描述：判断是不是合法的手机号码
     *
     * @param pn
     *            手机号
     * @return boolean
     */
    public static boolean isPhoneNumber(String pn) {
        try {
            return pn.matches("^1[\\d]{10}$");

        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * 是否是手机号
     * 
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {

        if (StringUtils.isBlank(mobiles)) {
            return false;
        }

        Pattern p = Pattern.compile("^((1[0-9][0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /***
     * 获取ImageUrl地址
     * 
     * @param html
     * @return
     */
    public static List<String> getImgUrl(String html) {

        String IMGURL_REG = "<img.*src=(.*?)[^>]*?>";
        Matcher matcher = Pattern.compile(IMGURL_REG).matcher(html);
        List<String> listImgUrl = new ArrayList<String>();
        while (matcher.find()) {
            listImgUrl.add(matcher.group());
        }
        return listImgUrl;
    }

    /***
     * 获取ImageSrc地址
     * 
     * @param listImgUrl
     * @return
     */
    public static List<String> getImageSrc(List<String> listImgUrl) {

        String IMGSRC_REG = "http:\"?(.*?)(\"|>|\\s+)";
        List<String> listImgSrc = new ArrayList<String>();
        for (String image : listImgUrl) {
            Matcher matcher = Pattern.compile(IMGSRC_REG).matcher(image);
            while (matcher.find()) {
                listImgSrc.add(matcher.group().substring(0, matcher.group().length() - 1));
            }
        }
        return listImgSrc;
    }

    /***
     * 获取ImageSrc地址
     * 
     * @param html
     * @return
     */
    public static List<String> getImageSrc(String html) {

        return getImageSrc(getImgUrl(html));
    }
}
