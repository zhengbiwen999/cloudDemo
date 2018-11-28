package com.zbw.cache;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    private static Pattern PATTERN_P1 = Pattern.compile("[\t|\r|\n]");

    private static Pattern PATTERN_P2 = Pattern.compile("[\"|\\\\]");

    /**
     * 去除制定开始的字符(同C#的trimStart)
     * @param source
     * @param characters
     * @return
     */
    public static String trimStart(String source, Character characters) {
        return source.replaceAll("^[" + characters + "]+", "");
    }
    public static String trimEnd(String source, Character characters) {
        return source.replaceAll("[" + characters + "]+$", "");
    }

    /**
     * 左填充
     *
     * @param str
     * @param length
     * @param ch
     * @return
     */
    public static String leftPad(String str, int length, char ch) {
        if (str.length() >= length) {
            return str;
        }
        char[] chs = new char[length];
        Arrays.fill(chs, ch);
        char[] src = str.toCharArray();
        System.arraycopy(src, 0, chs, length - src.length, src.length);
        return new String(chs);
    }

    /**
     * 右填充
     *
     * @param str
     * @param length
     * @param ch
     * @return
     */
    public static String rightPad(String str, int length, char ch) {
        if (str.length() >= length) {
            return str;
        }
        char[] chs = new char[length];
        Arrays.fill(chs, ch);
        char[] src = str.toCharArray();
        System.arraycopy(src, 0, chs, 0, src.length);
        return new String(chs);
    }

    public static String getValue(String str) {
        if (StringUtils.isBlank(str)) {
            return "";
        }
        return str;
    }

    public static String getValue(Object str) {
        if (str == null) {
            return "";
        }

        if (StringUtils.isBlank(str.toString())) {
            return "";
        }
        return str.toString();
    }

    public static boolean isBlank(String str) {
        return StringUtils.isBlank(str);
    }

    public static boolean isNotBlank(String str) {
        return !StringUtils.isBlank(str);
    }

    public static boolean isBlank(Object str) {
        return StringUtils.isBlank(getValue(str));
    }

    public static String addSlashes(String src) {
        if (src == null) {
            return "";
        }
        return src.replace("'", "\\'").replace("\"", "\\\"");
    }

    public static String urlencode(String src) {
        if (src == null) {
            return "";
        }
        try {
            return java.net.URLEncoder.encode(src, "utf-8");
        } catch (UnsupportedEncodingException e) {
        }
        return src;
    }

    public static String urldecode(String src) {
        if (src == null) {
            return "";
        }
        try {
            return java.net.URLDecoder.decode(src, "utf-8");
        } catch (UnsupportedEncodingException e) {
        }
        return src;
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (!StringUtils.isEmpty(str)) {
            //final Pattern p = Pattern.compile("[\t|\r|\n]");
            Matcher m = PATTERN_P1.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static String replaceQuota(String str) {
        String dest = "";
        if (!org.springframework.util.StringUtils.isEmpty(str)) {
            //final Pattern p = Pattern.compile("[\"|\\\\]");
            Matcher m = PATTERN_P2.matcher(str);
            dest = m.replaceAll("'").replace("''", "'");
        }
        return dest;
    }



}
