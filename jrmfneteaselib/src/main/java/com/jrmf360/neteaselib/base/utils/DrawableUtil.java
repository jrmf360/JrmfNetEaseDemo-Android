package com.jrmf360.neteaselib.base.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;

/**
 * 在代码中生成drawable
 * 
 * @author honglin
 *
 */
public class DrawableUtil {

	/**
	 * @param color
	 *            颜色
	 * @param radius
	 *            圆角
	 * @return
	 */
	public static Drawable generateDrawable(Context context, String color, int radius) {
		GradientDrawable gd = new GradientDrawable();
		if (StringUtil.isEmpty(color)) {
			gd.setColor(Color.WHITE);
		} else {
			if (color.startsWith("#")) {
				gd.setColor(Color.parseColor(color));
			} else {
				gd.setColor(Color.parseColor("#" + color));
			}
		}
		gd.setCornerRadius(ScreenUtil.dp2px(context, radius));
		return gd;
	}

	/**
	 * 设置右边的drawable
	 *
	 * @param textView
	 */
	public static void setRightDrawable(Context context, TextView textView, int id, boolean isShow) {
		Drawable drawable = context.getResources().getDrawable(id);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		if (isShow){
			textView.setCompoundDrawables(null, null, drawable, null);
		}else{
			textView.setCompoundDrawables(null, null, null, null);
		}
	}

	public static void setDrawableRightWithIntrinsicBounds(Drawable drawable, TextView textView) {
		textView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
	}
}
