package com.jrmf360.neteaselib.rp.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.utils.DrawableUtil;
import com.jrmf360.neteaselib.base.utils.KeyboardUtil;
import com.jrmf360.neteaselib.base.utils.NoDoubleClickUtils;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.utils.ToastUtil;
import com.jrmf360.neteaselib.base.wrapper.BaseTextWatcher;
import com.jrmf360.neteaselib.rp.constants.ConstantUtil;
import com.jrmf360.neteaselib.rp.http.RpHttpManager;
import com.jrmf360.neteaselib.rp.http.model.RedEnvelopeModel;
import com.jrmf360.neteaselib.rp.widget.NoUnderClickableSpan;

import java.math.BigDecimal;

import static com.jrmf360.neteaselib.R.id.et_peak_amount;

public class SgRpActivity extends BaseSendActivity {

	private int				ENVELOPES_TYPE	= ConstantUtil.ENVELOPES_TYPE_PIN;	// 红包类型

	private TextView		pop_message;

	private LinearLayout	ll_peak_num_layout;									// 红包个数根节点

	private EditText		et_peak_num;										// 红包

	private TextView		tv_group_member_num;								// 群成员人数

	private LinearLayout	ll_peak_amount_layout;								// 红包金额根节点

	private TextView		tv_peak_amount_icon;								// 总金额的icon

	private TextView		tv_peak_type;										// 当前红包类型

	private TextView		tv_amount_for_show;									// 红包实际金额

	private Button			btn_putin;											// 塞钱进红包

	private String			receiveUserid;

	@Override public int getLayoutId() {
		return R.layout.jrmf_rp_activity_sg;
	}

	@Override public void initView() {
		pop_message = (TextView) findViewById(R.id.pop_message);
		ll_peak_num_layout = (LinearLayout) findViewById(R.id.ll_peak_num_layout);
		et_peak_num = (EditText) findViewById(R.id.et_peak_num);
		et_peak_num.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
		et_peak_num.setFocusable(true);
		et_peak_num.setFocusableInTouchMode(true);
		et_peak_num.requestFocus();
		et_peak_num.setSelection(et_peak_num.getText().length());
		tv_group_member_num = (TextView) findViewById(R.id.tv_group_member_num);
		ll_peak_amount_layout = (LinearLayout) findViewById(R.id.ll_peak_amount_layout);
		tv_peak_amount_icon = (TextView) findViewById(R.id.tv_peak_amount_icon);
		et_amount = (EditText) findViewById(et_peak_amount);
		tv_peak_type = (TextView) findViewById(R.id.tv_peak_type);
		et_peak_message = (EditText) findViewById(R.id.et_peak_message);
		tv_amount_for_show = (TextView) findViewById(R.id.tv_amount_for_show);
		btn_putin = (Button) findViewById(R.id.btn_putin);
		// 弹出软键盘
		KeyboardUtil.popInputMethod(et_peak_num);
	}

	@Override public void initListener() {
		actionBarView.getIvBack().setOnClickListener(this);
		btn_putin.setOnClickListener(this);
		et_peak_num.addTextChangedListener(new BaseTextWatcher() {

			@Override public void afterTextChanged(Editable s) {
				NUMBER = checkNum();
				if (NUMBER != -1) {
					AMOUNTMONEY = checkAmount();
				}
				checkForAllAmount();
				checkForButton();
			}
		});

		et_amount.addTextChangedListener(new BaseTextWatcher() {

			@Override public void afterTextChanged(Editable s) {
				NUMBER = checkNum();
				if (NUMBER != -1) {
					AMOUNTMONEY = checkAmount();
				}
				checkForAllAmount();
				checkForButton();
			}
		});

		// 设置过滤器
		et_amount.setFilters(new InputFilter[] { new InputMoney() });
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

	@Override protected void initData(Bundle bundle) {
		summary = getString(R.string.jrmf_rp_bribery_message);
		String userName = "";
		String userIcon = "";
		if (bundle != null) {
			receiveUserid = bundle.getString(ConstantUtil.R_TargetId);
			int gNum = bundle.getInt(ConstantUtil.GNUM, 0);
			if (gNum <= 0) {
				tv_group_member_num.setVisibility(View.GONE);
			} else {
				tv_group_member_num.setText(String.format(getString(R.string.jrmf_rp_group_num), gNum));
			}
			userName = bundle.getString(ConstantUtil.JRMF_USER_NAME);
			userIcon = bundle.getString(ConstantUtil.JRMF_USER_ICON);
		}

		setDefaultView();
		saveUserId();
		getRpInfo(userName,userIcon);
	}

	private void setDefaultView() {
		pop_message.setVisibility(View.INVISIBLE);
		pop_message.getBackground().mutate().setAlpha(80);// 0~255透明度值

		setPeakTypeStyle();
		tv_amount_for_show.setText("0.00");
		setClickable(btn_putin, false);
	}

	private void setPeakTypeStyle() {
		String peak_type_msg = "";
		if (ENVELOPES_TYPE == ConstantUtil.ENVELOPES_TYPE_PIN) {// 拼手气红包
			DrawableUtil.setRightDrawable(this, tv_peak_amount_icon, R.drawable.jrmf_rp_ic_pin, true);
			tv_peak_amount_icon.setText(getString(R.string.jrmf_rp_amount));
			peak_type_msg = getString(R.string.jrmf_rp_luck_to_normal);
		} else if (ENVELOPES_TYPE == ConstantUtil.ENVELOPES_TYPE_NORMAL) {
			DrawableUtil.setDrawableRightWithIntrinsicBounds(null, tv_peak_amount_icon);
			tv_peak_amount_icon.setText(getString(R.string.jrmf_rp_single_money));
			peak_type_msg = getString(R.string.jrmf_rp_normal_to_luck);
		}
		int index = peak_type_msg.indexOf("，") + 1;
		if (index < 1) {
			return;
		}
		SpannableString spannableString1 = new SpannableString(peak_type_msg);
		spannableString1.setSpan(peakTypeClick, index, peak_type_msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannableString1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.jrmf_b_blue)), index, peak_type_msg.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv_peak_type.setText(spannableString1);
		tv_peak_type.setMovementMethod(LinkMovementMethod.getInstance());
	}

	private NoUnderClickableSpan peakTypeClick	= new NoUnderClickableSpan() {

														@Override public void onClick(View widget) {
															if (ENVELOPES_TYPE == ConstantUtil.ENVELOPES_TYPE_PIN) {
																ENVELOPES_TYPE = ConstantUtil.ENVELOPES_TYPE_NORMAL;
															} else if (ENVELOPES_TYPE == ConstantUtil.ENVELOPES_TYPE_NORMAL) {
																ENVELOPES_TYPE = ConstantUtil.ENVELOPES_TYPE_PIN;
															}
															setPeakTypeStyle();
															checkType();
														}
													};



	private int						NUMBER			= -1;

	private float					AMOUNTMONEY		= -1f;

	private void requestInfo() {
		KeyboardUtil.hideKeyboard(this);
		DialogDisplay.getInstance().dialogLoading(context, getString(R.string.jrmf_rp_loading), this);
		envelopeMessage = StringUtil.isEmpty(et_peak_message.getText().toString().trim()) ? et_peak_message.getHint().toString().trim() : et_peak_message.getText().toString().trim();
		RpHttpManager.sendGroupPeak(context, userid, thirdToken, et_peak_num.getText().toString(),
				et_amount.getText().toString(), envelopeMessage, ENVELOPES_TYPE, envelopeName, receiveUserid,
				new OkHttpModelCallBack<RedEnvelopeModel>() {

					@Override public void onSuccess(RedEnvelopeModel redModel) {
						DialogDisplay.getInstance().dialogCloseLoading(context);
						if (SgRpActivity.this.isFinishing()) {
							return;
						}
						if (redModel.isSuccess()) {
							//先判断用户有没有密码
							if (redModel.hasPwd == 1){//用户已设置密码
								jumpPayTypeActivity(redModel,et_amount.getText().toString().trim(), ENVELOPES_TYPE,
										et_peak_num.getText().toString().trim(), 1 == redModel.hasPwd);
							}else{//未设置密码
								showBindCardTipDialog(DIALOG_SET_PWD);
							}
						}else if ("U0044".equals(redModel.respstat)){
							//用户未实名认证
							showBindCardTipDialog(DIALOG_AUTH);
						}else{
							ToastUtil.showToast(context, redModel.respmsg);
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

	private void checkForAllAmount() {
		int NUM = -1;
		String str1 = et_peak_num.getText().toString();
		if (!StringUtil.isEmpty(str1)) {
			if (StringUtil.isNumber(str1)) {
				BigDecimal bd = new BigDecimal(str1);
				NUM = bd.intValue();
			}
		}

		float AMOUNT = 0f;
		String str2 = et_amount.getText().toString();
		if (!StringUtil.isEmpty(str2)) {
			if (!str2.startsWith(".")) {
				BigDecimal bd = new BigDecimal(str2);
				AMOUNT = bd.floatValue();
			}
		}

		if (NUM > 0 && AMOUNT > 0) {
			if (ENVELOPES_TYPE == ConstantUtil.ENVELOPES_TYPE_PIN) {
				BigDecimal zonggong = new BigDecimal(str2);
				zonggong = zonggong.setScale(2, BigDecimal.ROUND_HALF_UP);
				tv_amount_for_show.setText(zonggong.toString());
			} else if (ENVELOPES_TYPE == ConstantUtil.ENVELOPES_TYPE_NORMAL) {
				BigDecimal dange = new BigDecimal(str2);
				BigDecimal zonggong = dange.multiply(new BigDecimal(NUM));
				zonggong = zonggong.setScale(2, BigDecimal.ROUND_HALF_UP);
				tv_amount_for_show.setText(zonggong.toString());
			}
		} else {
			tv_amount_for_show.setText("0.00");
		}
	}

	private void checkForButton() {// button按钮是否可疑点击
		setClickable(btn_putin, false);
		if (NUMBER > 0 && AMOUNTMONEY > 0) {
			setClickable(btn_putin, true);
		}
	}

	private void checkType() {
		if (ENVELOPES_TYPE == ConstantUtil.ENVELOPES_TYPE_PIN) {
			String amountStr = tv_amount_for_show.getText().toString();
			et_amount.setText(amountStr);
		} else if (ENVELOPES_TYPE == ConstantUtil.ENVELOPES_TYPE_NORMAL) {// 拼手气--》总共
			String amountStr = et_amount.getText().toString();
			String numStr = et_peak_num.getText().toString();
			if (amountStr != null && !amountStr.isEmpty() && numStr != null && !numStr.isEmpty()) {
				BigDecimal zonggong = new BigDecimal(amountStr);
				BigDecimal num = new BigDecimal(numStr);
				if (num.floatValue() > 0) {
					BigDecimal dange = zonggong.divide(num, 3, BigDecimal.ROUND_HALF_DOWN);
					java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");
					et_amount.setText(df.format(dange));
				}
			}
		}
	}

	private void closeTips() {
		pop_message.setText("");
		pop_message.setVisibility(View.INVISIBLE);
	}

	// 红包个数
	private int checkNum() {
		String str = et_peak_num.getText().toString();
		if (!StringUtil.isEmpty(str)) {
			if (StringUtil.isNumber(str)) {
				BigDecimal bd = new BigDecimal(str);
				int num = bd.intValue();
				if (num == 0) {
					showTips(getString(R.string.jrmf_rp_last_one));
					ll_peak_num_layout.setBackgroundResource(R.drawable.jrmf_rp_bg_white_round_stroke);
					return -1;
				} else if (num > maxCount) {
					showTips(String.format(getString(R.string.jrmf_rp_most), maxCount));
					ll_peak_num_layout.setBackgroundResource(R.drawable.jrmf_rp_bg_white_round_stroke);
					return -1;
				} else {
					ll_peak_num_layout.setBackgroundResource(R.drawable.jrmf_rp_bg_white_round);
					closeTips();
					return num;
				}
			} else {
				showTips(getString(R.string.jrmf_rp_count_error));
				ll_peak_num_layout.setBackgroundResource(R.drawable.jrmf_rp_bg_white_round_stroke);
				return -1;
			}
		} else {
			closeTips();
			ll_peak_num_layout.setBackgroundResource(R.drawable.jrmf_rp_bg_white_round);
			return -1;
		}
	}

	private float checkAmount() {
		String str = et_amount.getText().toString();
		if (!StringUtil.isEmpty(str)) {
			if (!str.startsWith(".")) {
				BigDecimal bd = new BigDecimal(str);
				float amount = bd.floatValue();
				if (amount == 0) {
					ll_peak_amount_layout.setBackgroundResource(R.drawable.jrmf_rp_bg_white_round);
					closeTips();
					return -1;
				} else {
					if (amount < 0.01f) {
						showTips(getString(R.string.jrmf_rp_last_money));
						ll_peak_amount_layout.setBackgroundResource(R.drawable.jrmf_rp_bg_white_round_stroke);
						return -1;
					} else {
						if (ENVELOPES_TYPE == ConstantUtil.ENVELOPES_TYPE_PIN) {
							if (amount > StringUtil.string2double(maxLimitMoney)) {
								showTips(String.format(getString(R.string.jrmf_rp_most_money), maxLimitMoney));
								ll_peak_amount_layout.setBackgroundResource(R.drawable.jrmf_rp_bg_white_round_stroke);
								return -1;
							} else {
								ll_peak_amount_layout.setBackgroundResource(R.drawable.jrmf_rp_bg_white_round);
								closeTips();
								return amount;
							}
						}
						if (ENVELOPES_TYPE == ConstantUtil.ENVELOPES_TYPE_NORMAL) {
							String numStr = et_peak_num.getText().toString();
							if (numStr != null && !numStr.isEmpty()) {
								float num = 0;
								num = StringUtil.formatMoneyFloat(numStr);
								if (num != 0 && num * amount > StringUtil.string2double(maxLimitMoney)) {
									showTips(String.format(getString(R.string.jrmf_rp_most_money), maxLimitMoney));
									ll_peak_amount_layout.setBackgroundResource(R.drawable.jrmf_rp_bg_white_round_stroke);
									return -1;
								} else {
									ll_peak_amount_layout.setBackgroundResource(R.drawable.jrmf_rp_bg_white_round);
									closeTips();
									return amount;
								}
							} else {
								if (amount > StringUtil.string2double(maxLimitMoney)) {
									showTips(String.format(getString(R.string.jrmf_rp_most_money), maxLimitMoney));
									ll_peak_amount_layout.setBackgroundResource(R.drawable.jrmf_rp_bg_white_round_stroke);
									return -1;
								} else {
									closeTips();
									ll_peak_amount_layout.setBackgroundResource(R.drawable.jrmf_rp_bg_white_round);
									return amount;
								}
							}
						}
						return -1;
					}
				}
			} else {
				showTips(getString(R.string.jrmf_rp_money_error));
				ll_peak_amount_layout.setBackgroundResource(R.drawable.jrmf_rp_bg_white_round_stroke);
				return -1;
			}
		} else {
			closeTips();
			ll_peak_amount_layout.setBackgroundResource(R.drawable.jrmf_rp_bg_white_round);
			return -1;
		}
	}
}
