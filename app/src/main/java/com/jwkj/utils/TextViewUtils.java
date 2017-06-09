package com.jwkj.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.widget.TextView;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nuowei.smarthome.R;

public class TextViewUtils {
	static String emailRegex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
	static String FONTPATH="fonts";

	/*
	 * 验证邮箱
	 */
	public static boolean isEmail(String str) {
		Pattern pattern = Pattern.compile(emailRegex,Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	public static void setSpannableText(TextView view,String title,String text,int style){
		Context mContext=view.getContext();
		SpannableString styledText =new SpannableString(title+"\n"+text);
		Log.e("dxsText","title-->"+title.length()+"text-->"+text.length()+"styledText-->"+styledText.length());
		TextAppearanceSpan spanTitle=new TextAppearanceSpan(mContext,style);
		styledText.setSpan(spanTitle, 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		styledText.setSpan(new TextAppearanceSpan(mContext, R.style.spanstyletext), title.length(), styledText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		view.setText(styledText, TextView.BufferType.SPANNABLE);
	}
	
	public static Typeface setTypeFont(TextView tv,String fontName){
		Typeface newTypeFace=Typeface.createFromAsset(tv.getContext().getAssets(), FONTPATH+"/" + fontName);
		return newTypeFace!=null?newTypeFace:tv.getTypeface();
	}
}
