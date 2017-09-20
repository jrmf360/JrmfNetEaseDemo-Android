package com.jrmf360.neteaselib.rp.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.utils.KeyboardUtil;
import com.jrmf360.neteaselib.base.utils.ScreenUtil;
import com.jrmf360.neteaselib.rp.ui.AcfActivity;

/**
 * Created by Administrator on 2016/2/22.
 */
public class LimitDialog extends Dialog implements View.OnClickListener {

	private Context		context;

	private Activity activity;

	private View		rootView;

	private Button		cancle, confirm;

	private String		money;

	private TextView	tv_text;

	public LimitDialog(Context context, String money) {
		super(context, R.style.jrmf_rp_commondialog);
		this.context = context;
		this.money = money;
		rootView = LayoutInflater.from(context).inflate(R.layout.jrmf_rp_limit_dialog, null);
		setContentView(rootView);
		initDialog();
	}

	public LimitDialog(Activity activity, String money) {
		super(activity, R.style.jrmf_rp_commondialog);
		this.money = money;
		this.activity = activity;
		rootView = LayoutInflater.from(context).inflate(R.layout.jrmf_rp_limit_dialog, null);
		setContentView(rootView);
		initDialog();
	}

	private void initDialog() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.width = ScreenUtil.dip2px(context, 320);
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		getWindow().setGravity(Gravity.CENTER);
		getWindow().setAttributes(lp);
		setCancelable(false);
		setCanceledOnTouchOutside(false);
		tv_text = (TextView) rootView.findViewById(R.id.tv_text);
		tv_text.setText(String.format(context.getString(R.string.jrmf_rp_bind_card),money));
		cancle = (Button) rootView.findViewById(R.id.cancle);
		confirm = (Button) rootView.findViewById(R.id.confirm);
		confirm.setOnClickListener(this);
		cancle.setOnClickListener(this);
	}

	@Override public void onClick(View v) {
		if (v == cancle) {
			dismiss();
			new Handler().postDelayed(new Runnable() {

				@Override public void run() {
					KeyboardUtil.showKeyboard(activity);
				}
			}, 200);

		} else if (v == confirm) {
			//跳转到绑卡页面
			AcfActivity.intent(activity);
			dismiss();
		} else {
		}
	}
}
