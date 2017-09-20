package com.jrmf360.neteaselib.base.manager;

import android.app.Activity;
import android.app.PendingIntent;

import com.jrmf360.neteaselib.base.utils.LogUtil;

import java.lang.reflect.Method;
import java.util.Stack;

public class CusActivityManager {

	private static Stack<Activity>		activityStack;

	private static CusActivityManager	instance;

	private PendingIntent				restartIntent;

	private CusActivityManager() {
		activityStack = new Stack<Activity>();
	}

	/**
	 * 单一实例
	 */
	public static CusActivityManager getInstance() {
		if (instance == null) {
			instance = new CusActivityManager();
		}
		return instance;
	}

	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity) {
		LogUtil.i("addActivity", activity);
		activityStack.add(activity);
	}

	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 */
	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public void finishCurrActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			LogUtil.i("finishActivity", activity);
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 找到指定的activity－如果有相同的activity － 取出来最后打开的哪个activity
	 * 
	 * @param calzz
	 * @return
	 */
	public <T> T findActivity(Class<T> calzz) {
		Activity a = null;
		for (int i = activityStack.size() - 1; i >= 0; i--) {
			if (activityStack.get(i).getClass().equals(calzz)) {
				a = activityStack.get(i);
				break;
			}
		}
		return (T) a;
	}

	/**
	 * 结束指定类名的Activity - 如果有相同的activity，结束最后打开的哪个activity
	 */
	public void finishActivity(Class<?> cls) {
		for (int i = activityStack.size() - 1; i >= 0; i--) {
			Activity activity = activityStack.get(i);
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
				break;
			}
		}
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		// 关闭我们自己的页面
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
		// 关闭支付宝的页面
//		finishAlipayActivity();

	}

	/**
	 * 关闭支付宝的页面
	 */
	private void finishAlipayActivity() {
		try {
			// 付款
			Class payActivity = Class.forName("com.alipay.sdk.app.H5PayActivity");
			Method finish = payActivity.getDeclaredMethod("finish");
			finish.setAccessible(true);
			finish.invoke(payActivity);
		} catch (Exception e) {
			LogUtil.e("H5PayActivity关闭失败");
		}

		// 认证
		try {
			// 付款
			Class payActivity = Class.forName("com.alipay.sdk.auth.AuthActivity");
			Method finish = payActivity.getDeclaredMethod("finish");
			finish.setAccessible(true);
			finish.invoke(payActivity);
		} catch (Exception e) {
			LogUtil.e("AuthActivity关闭失败");
		}
	}

	/**
	 * 退出应用程序
	 */
	public void exitApp() {
		try {
			finishAllActivity();
			System.exit(0);
			android.os.Process.killProcess(android.os.Process.myPid());
		} catch (Exception e) {
		}
	}
}
