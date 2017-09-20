package com.jrmf360.neteaselib.base.fragment;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.utils.ScreenUtil;

/**
 * @创建人 honglin
 * @创建时间 16/8/28 下午5:50
 * @类描述 显示加载的进度框
 */
public class LoadingDialogFragment extends DialogFragment {

	TextView						tvMessage;

	ImageView						ivSuccess;

	ImageView						ivFailure;

	ImageView						ivProgressSpinner;

	AnimationDrawable				adProgressSpinner;

	private static LoadingDialogListener	mListener;

	public static LoadingDialogFragment getInstance() {
		mListener = null;
		return new LoadingDialogFragment();
	}

	public static LoadingDialogFragment getInstance(LoadingDialogListener listener) {
		mListener = listener;
		return new LoadingDialogFragment();
	}

	@Override public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.jrmf_b_dialog_progress, null);
		tvMessage = (TextView) view.findViewById(R.id.textview_message);
		ivSuccess = (ImageView) view.findViewById(R.id.imageview_success);
		ivFailure = (ImageView) view.findViewById(R.id.imageview_failure);
		ivProgressSpinner = (ImageView) view.findViewById(R.id.imageview_progress_spinner);

		Dialog dialog = new Dialog(this.getActivity(), R.style.Jrmf_b_DialogTheme);
		dialog.setCanceledOnTouchOutside(false);
		ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ScreenUtil.dp2px(getActivity(), 100), ScreenUtil.dp2px(getActivity(), 100));
		dialog.addContentView(view, layoutParams);
		return dialog;
	}

	@Override public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData(savedInstanceState);
	}

	protected void initData(Bundle bundle) {
		ivProgressSpinner.setImageResource(R.drawable.jrmf_b_round_spinner_fade);
		adProgressSpinner = (AnimationDrawable) ivProgressSpinner.getDrawable();
		adProgressSpinner.start();
	}

	public void setMessage(String message) {
		if (tvMessage != null) {
			tvMessage.setText(message);
		}
	}

	public void setMessage(int message) {
		if (tvMessage != null) {
			tvMessage.setText(getResources().getString(message));
		}
	}

	@Override public void dismissAllowingStateLoss() {
		super.dismissAllowingStateLoss();
		reset();
	}

	public DialogFragment showAllowingStateLoss(FragmentManager fragmentManager) {
		FragmentTransaction ft = fragmentManager.beginTransaction();
		ft.add(this, this.getClass().getName());
		ft.commitAllowingStateLoss();
		return this;
	}

	protected void reset() {
		if (adProgressSpinner != null) {
			adProgressSpinner.stop();
			adProgressSpinner = null;
		}
		if (ivProgressSpinner != null) {
			ivProgressSpinner.setVisibility(View.VISIBLE);
		}
		if (ivFailure != null) {
			ivFailure.setVisibility(View.GONE);
		}
		if (ivSuccess != null) {
			ivSuccess.setVisibility(View.GONE);
		}
		if (tvMessage != null) {
			tvMessage.setText("正在加载 ...");
		}
	}

	@Override public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		if (mListener != null) {
			mListener.onCancel();
		}
	}

	public void dismissWithSuccess(String message) {
		showSuccessImage();
		if (message != null) {
			if (tvMessage != null) {
				tvMessage.setText(message);
			}
		} else {
			if (tvMessage != null) {
				tvMessage.setText("");
			}
		}

		dismissHUD();
	}

	protected void showSuccessImage() {
		if (ivProgressSpinner != null) {
			ivProgressSpinner.setVisibility(View.GONE);
		}
		if (ivSuccess != null) {
			ivSuccess.setVisibility(View.VISIBLE);
		}
	}

	protected void dismissHUD() {
		AsyncTask<String, Integer, Long> task = new AsyncTask<String, Integer, Long>() {

			@Override protected Long doInBackground(String... params) {
				SystemClock.sleep(500);
				return null;
			}

			@Override protected void onPostExecute(Long result) {
				super.onPostExecute(result);
				if (isAdded()) {
					reset();
				} else {
					if (isAdded()) {
						reset();
					}
				}
			}
		};
		task.execute();
	}

	public interface LoadingDialogListener {

		void onCancel();
	}
}
