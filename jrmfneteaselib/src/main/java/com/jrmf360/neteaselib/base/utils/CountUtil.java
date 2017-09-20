package com.jrmf360.neteaselib.base.utils;

import android.os.CountDownTimer;
import android.widget.TextView;

/**
 * 计时器
 * 
 * @author honglin
 *
 */
public class CountUtil extends CountDownTimer {

	private TextView	view;

	private int			key;

	public CountUtil(long millisInFuture, long countDownInterval, TextView view, int key) {
		super(millisInFuture, countDownInterval);
		this.key = key;
		this.view = view;
	}

	@Override public void onTick(long millisUntilFinished) {
		if (key == 1) {
			// 发送验证码
			view.setText(millisUntilFinished / 1000 + "秒后重新获取");
		} else {
			view.setText((new StringBuilder().append(millisUntilFinished / 1000).append("秒")));
		}
		view.setClickable(false);
	}

	@Override public void onFinish() {
		if (key == 1) {
			view.setText("获取验证码");

		} else {
			view.setText("重新发送");
		}
		view.setClickable(true);
	}
}
