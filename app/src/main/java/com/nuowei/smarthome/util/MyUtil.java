package com.nuowei.smarthome.util;
/**
 * Copyright ©深圳市海曼科技有限公司
 */

import android.content.Context;

import java.util.Locale;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/2/21 08:53
 * @Description :
 */
public class MyUtil {
    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }
}
