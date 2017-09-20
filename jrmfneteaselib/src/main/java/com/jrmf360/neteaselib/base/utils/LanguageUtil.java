package com.jrmf360.neteaselib.base.utils;

import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * @创建人 honglin
 * @创建时间 2017/6/22 下午6:35
 * @类描述 一句话描述 你的UI
 */
public class LanguageUtil {

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) public static void setLocale(Activity context) {
		String setLan = SPManager.getInstance().getString(context, "setLan", "zh");
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		Configuration config = resources.getConfiguration();
		config.locale = new Locale(setLan);
		resources.updateConfiguration(config, dm);
		if ("ar".equals(setLan)) {
			context.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
			context.getWindow().getDecorView().setTextDirection(View.TEXT_DIRECTION_RTL);
		}
	}



	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static void setRtl(View view, Activity activity){
		String setLan = SPManager.getInstance().getString(activity, "setLan", "zh");
		if ("ar".equals(setLan)) {
			view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
			view.setTextDirection(View.TEXT_DIRECTION_RTL);
		}
	}

	public static String getLan(Activity context) {
		String setLan = SPManager.getInstance().getString(context, "setLan", "zh");
		return setLan;
	}

	/**
	 * 判断当前语言设置是否是阿拉伯语
	 * @param context
	 * @return
	 */
	public static boolean isAr(Context context){
		return "ar".equals(SPManager.getInstance().getString(context, "setLan", "zh"));
	}
}











