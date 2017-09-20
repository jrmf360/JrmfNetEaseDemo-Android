package com.jrmf360.neteaselib.base.utils;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 吐司显示的工具类-通过全局的context来显示
 */
public class ToastUtil {

	private static Toast	toast;

	/**
	 * 显示吐司
	 *
	 * @param context
	 * @param message
	 */
	public static void showToast(final Context context, final String message) {
		if (context == null){
			return;
		}
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
				View view =  toast.getView();
				TextView textView = (TextView) view.findViewById(android.R.id.message);
				textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
				toast.show();
			}
		});
	}

	/**
	 * 显示长时间的吐司
	 * @param context
	 * @param message
     */
	public static void showLongToast(final Context context, final String message) {
		if (context == null){
			return;
		}
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
				View view =  toast.getView();
				TextView textView = (TextView) view.findViewById(android.R.id.message);
				textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
				toast.show();
			}
		});
	}

	/**
	 * 连续显示吐司
	 *
	 * @param context
	 * @param message
	 */
	public static void showNoWaitToast(final Context context, final String message) {
		if (context == null){
			return;
		}
		new Handler(Looper.getMainLooper()).post(new Runnable() {

			@Override public void run() {
				if (toast == null) {
					toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
					View view = toast.getView();
					TextView textView = (TextView) view.findViewById(android.R.id.message);
					textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
					toast.show();
				}else{
					toast.setText(message);
					toast.show();
				}
			}
		});

	}
}
