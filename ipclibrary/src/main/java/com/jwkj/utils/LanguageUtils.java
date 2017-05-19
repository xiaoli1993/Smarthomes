package com.jwkj.utils;

import android.content.Context;

import java.util.Locale;
/*
 * create by wzy  2016/9/21
 */
public class LanguageUtils {

	public static boolean isZh(Context context) {
		Locale locale = context.getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		if (language.endsWith("zh"))
			return true;
		else
			return false;
	}
	
	
	public static boolean isLanguage(String lang,Context context) {
		Locale locale = context.getResources().getConfiguration().locale;
		String country = locale.getCountry();
		if (country.equals(lang))
			return true;
		else
			return false;
	}

	public static String getCountry(Context context) {
		Locale locale = context.getResources().getConfiguration().locale;
//		String language = locale.getCountry();
		return locale.getCountry();
	}
}
