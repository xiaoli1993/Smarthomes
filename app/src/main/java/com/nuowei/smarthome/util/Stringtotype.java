package com.nuowei.smarthome.util;/**
 * Created by hp on 2016/7/8.
 */

/**
 * 作者：肖力
 * 邮箱：554674787@qq.com
 * 时间：2016/7/8 08:19
 */
public class Stringtotype {

    //change the string type to the int type
    public static int stringToInt(String intstr) {
        try {
            Integer integer;
            integer = Integer.valueOf(intstr);
            return integer.intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    //change int type to the string type
    public static String intToString(int value) {
        Integer integer = new Integer(value);
        return integer.toString();
    }

    //change the string type to the float type
    public static float stringToFloat(String floatstr) {
        try {
            Float floatee;
            floatee = Float.valueOf(floatstr);
            return floatee.floatValue();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 字符串转换成十六进制字符串
     *
     * @param str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String str2HexStr(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    //change the float type to the string type
    public static String floatToString(float value) {
        Float floatee = new Float(value);
        return floatee.toString();
    }

    //change the string type to the sqlDate type
    public static java.sql.Date stringToDate(String dateStr) {
        return java.sql.Date.valueOf(dateStr);
    }

    //change the sqlDate type to the string type
    public static String dateToString(java.sql.Date datee) {
        return datee.toString();
    }

}
