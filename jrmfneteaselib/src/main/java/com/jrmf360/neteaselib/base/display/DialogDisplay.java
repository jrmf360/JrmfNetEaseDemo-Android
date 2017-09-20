package com.jrmf360.neteaselib.base.display;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;

import com.jrmf360.neteaselib.base.fragment.LARDialogFragment;
import com.jrmf360.neteaselib.base.fragment.LoadingDialogFragment;
import com.jrmf360.neteaselib.base.utils.ThreadUtil;

/**
 * 负责dialog的显示
 * 
 * @author honglin
 */
public class DialogDisplay {
	private static DialogDisplay instance = null;
	private boolean touchOutside = true;

	private DialogDisplay() {
	}

	public static DialogDisplay getInstance() {
		synchronized (DialogDisplay.class) {
			if (instance == null) {
				instance = new DialogDisplay();
			}
		}

		return instance;
	}
	
	/**
	 * 显示dialog
	 * @param title
	 * @param left
	 * @param right
	 * @param listener
	 */
	public LARDialogFragment dialogLeftAndRight(int fromkey, String title, String left, String right, LARDialogFragment.InputPwdErrorListener listener){
		LARDialogFragment errorDialogFragment = LARDialogFragment.newInstance(fromkey,title, left, right,touchOutside);
		errorDialogFragment.setListener(listener);
		return errorDialogFragment;
	}

	public DialogDisplay setCanceledOnTouchOutside(boolean touchOutside){
		if (instance == null)getInstance();
		this.touchOutside = touchOutside;
		return instance;
	}
	
	/**
	 * 默认支付密码错误弹框
	 * @param context
	 * @param listener
	 * @return
	 */
	public LARDialogFragment dialogLeftAndRight(Context context, LARDialogFragment.InputPwdErrorListener listener){
		LARDialogFragment errorDialogFragment = new LARDialogFragment();
		errorDialogFragment.setListener(listener);
		return errorDialogFragment;
	}


	/**
	 * 显示加载框
	 * @param activity
	 * @param msg
     */
	public void dialogLoading(final Activity activity, final String msg) {
		ThreadUtil.getInstance().runMainThread(new Runnable() {

			@Override public void run() {
				if (activity != null) {
					FragmentManager fragmentManager = activity.getFragmentManager();
					if (fragmentManager != null) {
						LoadingDialogFragment loadingDialogFragment = (LoadingDialogFragment) fragmentManager.findFragmentByTag(LoadingDialogFragment.class.getName());
						if (loadingDialogFragment == null) {
							loadingDialogFragment = LoadingDialogFragment.getInstance();
							loadingDialogFragment.showAllowingStateLoss(fragmentManager);
							fragmentManager.executePendingTransactions();
						}
						loadingDialogFragment.setMessage(msg);
					}
				}
			}
		});
	}
	/**
	 * 显示加载框
	 * @param activity
	 * @param msg
     */
	public void dialogLoading(final Activity activity, final String msg, final LoadingDialogFragment.LoadingDialogListener listener) {
		ThreadUtil.getInstance().runMainThread(new Runnable() {

			@Override public void run() {
				if (activity != null) {
					FragmentManager fragmentManager = activity.getFragmentManager();
					if (fragmentManager != null) {
						LoadingDialogFragment loadingDialogFragment = (LoadingDialogFragment) fragmentManager.findFragmentByTag(LoadingDialogFragment.class.getName());
						if (loadingDialogFragment == null) {
							loadingDialogFragment = LoadingDialogFragment.getInstance(listener);
							loadingDialogFragment.showAllowingStateLoss(fragmentManager);
							fragmentManager.executePendingTransactions();
						}
						loadingDialogFragment.setMessage(msg);
					}
				}
			}
		});
	}

	public void dialogCloseLoading(final Activity activity) {
		ThreadUtil.getInstance().runMainThread(new Runnable() {

			@Override public void run() {
				if (activity != null) {
					FragmentManager fragmentManager = activity.getFragmentManager();
					if (fragmentManager != null) {
						LoadingDialogFragment loadingDialogFragment = (LoadingDialogFragment) fragmentManager.findFragmentByTag(LoadingDialogFragment.class.getName());
						if (loadingDialogFragment != null) {
							loadingDialogFragment.dismissAllowingStateLoss();
						}
					}
				}
			}
		});

	}
}






