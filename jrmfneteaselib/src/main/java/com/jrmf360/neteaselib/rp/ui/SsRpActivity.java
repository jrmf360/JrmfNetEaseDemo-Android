package com.jrmf360.neteaselib.rp.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.utils.KeyboardUtil;
import com.jrmf360.neteaselib.base.utils.NoDoubleClickUtils;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.utils.ToastUtil;
import com.jrmf360.neteaselib.base.wrapper.BaseTextWatcher;
import com.jrmf360.neteaselib.rp.constants.ConstantUtil;
import com.jrmf360.neteaselib.rp.http.RpHttpManager;
import com.jrmf360.neteaselib.rp.http.model.RedEnvelopeModel;
import com.jrmf360.neteaselib.rp.widget.ActionBarView;

import java.math.BigDecimal;

public class SsRpActivity extends BaseSendActivity {

	private TextView		pop_message;

	private LinearLayout	ll_amount_layout;

	private TextView		tv_amount;

	private Button			btn_putin;

	private String			receiveUserid;

	@Override public int getLayoutId() {
		return R.layout.jrmf_rp_activity_ss;
	}

	@Override public void initView() {
		actionBarView = (ActionBarView) findViewById(R.id.actionbar);
		pop_message = (TextView) findViewById(R.id.pop_message);
		pop_message.setVisibility(View.INVISIBLE);
		ll_amount_layout = (LinearLayout) findViewById(R.id.ll_amount_layout);
		et_amount = (EditText) findViewById(R.id.et_amount);
		et_peak_message = (EditText) findViewById(R.id.et_message);
		tv_amount = (TextView) findViewById(R.id.tv_amount);
		btn_putin = (Button) findViewById(R.id.btn_putin);

		// 弹出软键盘
		KeyboardUtil.popInputMethod(et_amount);
	}

	@Override public void initListener() {
		actionBarView.getIvBack().setOnClickListener(this);
		btn_putin.setOnClickListener(this);
		et_amount.addTextChangedListener(new BaseTextWatcher() {

			@Override public void afterTextChanged(Editable s) {
				super.afterTextChanged(s);
				checkAmount();
			}
		});

		// 设置过滤器
		et_amount.setFilters(new InputFilter[] { new InputMoney() });
	}

	@Override protected void initData(Bundle bundle) {
		summary = getString(R.string.jrmf_rp_bribery_message);
		String userName = "";
		String userIcon = "";
		if (bundle != null) {
			receiveUserid = bundle.getString(ConstantUtil.R_TargetId);
			userName = bundle.getString(ConstantUtil.JRMF_USER_NAME);
			userIcon = bundle.getString(ConstantUtil.JRMF_USER_ICON);
		}
		pop_message.getBackground().mutate().setAlpha(80);// 0~255透明度值
		tv_amount.setText("0.00");
		setClickable(btn_putin, false);
		saveUserId();
		getRpInfo(userName,userIcon);
	}


	@Override public void onClick(int id) {
		if (id == R.id.btn_putin) {
			if (!NoDoubleClickUtils.isDoubleClick()) {
				requestInfo();
			}
		} else if (id == R.id.iv_back) {
			KeyboardUtil.hideKeyboard(this);
			finish();
		}
	}

	private void checkAmount() {
		String str = et_amount.getText().toString();
		if (!StringUtil.isEmpty(str)) {
			if (!str.startsWith(".")) {
				BigDecimal bd = new BigDecimal(str);
				float amount = bd.floatValue();
				if (amount == 0) {
					closeTips();
					ll_amount_layout.setBackgroundResource(R.drawable.jrmf_rp_bg_white_round);
				} else if (amount < 0.01f) {
					// showTips("单个红包金额不可低于0.01元");
					showTips(getString(R.string.jrmf_rp_last_money));
					ll_amount_layout.setBackgroundResource(R.drawable.jrmf_rp_bg_white_round_stroke);
					setClickable(btn_putin, false);
				} else if (amount > StringUtil.string2double(maxLimitMoney)) {
					showTips(String.format(getString(R.string.jrmf_rp_single_most_money), maxLimitMoney));
					ll_amount_layout.setBackgroundResource(R.drawable.jrmf_rp_bg_white_round_stroke);
					setClickable(btn_putin, false);
				} else {
					closeTips();
					ll_amount_layout.setBackgroundResource(R.drawable.jrmf_rp_bg_white_round);
					setClickable(btn_putin, true);
				}
				if (amount > 0) {
					bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP); // 设置精度，以及舍入规则
					tv_amount.setText(bd.toString());
				} else {
					tv_amount.setText("0.00");
				}
			} else {
				showTips(getString(R.string.jrmf_rp_money_error));
				ll_amount_layout.setBackgroundResource(R.drawable.jrmf_rp_bg_white_round_stroke);
				setClickable(btn_putin, false);
			}
		} else {
			closeTips();
			ll_amount_layout.setBackgroundResource(R.drawable.jrmf_rp_bg_white_round);
			tv_amount.setText("0.00");
		}

	}

	private void requestInfo() {
		KeyboardUtil.hideKeyboard(this);
		DialogDisplay.getInstance().dialogLoading(context, getString(R.string.jrmf_rp_loading), this);
		String summary = et_peak_message.getText().toString().trim();
		envelopeMessage = (StringUtil.isNotEmpty(summary)) ? summary : et_peak_message.getHint().toString().trim();
		RpHttpManager.sendSinglePeak(context, userid, thirdToken, tv_amount.getText().toString(), envelopeMessage, receiveUserid, envelopeName, new OkHttpModelCallBack<RedEnvelopeModel>() {

			@Override public void onSuccess(RedEnvelopeModel redEnvelopeModel) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				if (SsRpActivity.this.isFinishing()) {
					return;
				}
				if (redEnvelopeModel.isSuccess()) {
					if (redEnvelopeModel.hasPwd == 1){
						jumpPayTypeActivity(redEnvelopeModel, et_amount.getText().toString().trim(),
								ConstantUtil.ENVELOPES_TYPE_SINGLE, "1", 1 == redEnvelopeModel.hasPwd);
					}else{
						showBindCardTipDialog(DIALOG_SET_PWD);
					}
				}else if ("U0044".equals(redEnvelopeModel.respstat)){
					//用户未实名认证
					showBindCardTipDialog(DIALOG_AUTH);
				} else {
					ToastUtil.showToast(context, redEnvelopeModel.respmsg);
				}
			}

			@Override public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				ToastUtil.showToast(context, getString(R.string.jrmf_rp_network_error));
			}
		});
	}

	private void showTips(String message) {
		pop_message.setText(message);
		pop_message.setVisibility(View.VISIBLE);
	}

	private void closeTips() {
		pop_message.setText("");
		pop_message.setVisibility(View.INVISIBLE);
	}
}
