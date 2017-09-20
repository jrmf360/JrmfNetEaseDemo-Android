package com.jrmf360.neteaselib.rp.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.manager.CusActivityManager;
import com.jrmf360.neteaselib.base.utils.CountUtil;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.utils.ToastUtil;
import com.jrmf360.neteaselib.base.view.ClearEditText;
import com.jrmf360.neteaselib.rp.constants.ConstantUtil;
import com.jrmf360.neteaselib.rp.http.RpHttpManager;
import com.jrmf360.neteaselib.rp.http.model.BindCardModel;
import com.jrmf360.neteaselib.rp.http.model.CodeModel;

/**
 * @创建人 honglin
 * @创建时间 2017/5/11 下午3:21
 * @类描述 模仿钱包添加银行卡第二步
 */
public class AcsActivity extends BaseActivity {

	private ClearEditText cet_name, cet_idCardNum, cet_phone, cet_code;

	private Button			btn_confirm;

	private TextView		tv_idCardNum, tv_name, tv_bankCardNum, tv_send_code, tv_protocol;

	private String			mobileToken;														// 交易令牌

	private String			realName, idCardNum, bankCardNum, cardName, bankNo;

	private int				isAuthentication;


	/**
	 *
	 * @param fromActivity
	 * @param realName
	 *            用户姓名
	 * @param idCardNum
	 *            身份证号码
	 * @param bankCardNum
	 *            银行卡号
	 */
	public static void intent(Activity fromActivity, String realName, String cardName, String idCardNum, String bankCardNum, int isAuthentication, String bankNo) {
		Intent intent = new Intent(fromActivity, AcsActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("realName", realName);
		bundle.putString("cardName", cardName);
		bundle.putString("idCardNum", idCardNum);
		bundle.putString("bankCardNum", bankCardNum);
		bundle.putInt("isAuthentication", isAuthentication);
		bundle.putString("bankNo", bankNo);
		intent.putExtras(bundle);
		fromActivity.startActivity(intent);
	}

	@Override public int getLayoutId() {
		return R.layout.jrmf_rp_activity_acs;
	}

	@Override public void initView() {
		tv_bankCardNum = (TextView) findViewById(R.id.tv_bankCardNum);

		cet_idCardNum = (ClearEditText) findViewById(R.id.cet_idCardNum);
		cet_phone = (ClearEditText) findViewById(R.id.cet_phone);
		cet_name = (ClearEditText) findViewById(R.id.cet_name);
		cet_code = (ClearEditText) findViewById(R.id.cet_code);

		btn_confirm = (Button) findViewById(R.id.btn_confirm);

		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_idCardNum = (TextView) findViewById(R.id.tv_idCardNum);
		tv_send_code = (TextView) findViewById(R.id.tv_send_code);
		tv_protocol = (TextView) findViewById(R.id.tv_protocol);
	}

	@Override public void initListener() {
		actionBarView.getIvBack().setOnClickListener(this);
		btn_confirm.setOnClickListener(this);
		tv_send_code.setOnClickListener(this);
		tv_protocol.setOnClickListener(this);
	}

	@Override protected void initData(Bundle bundle) {
		if (bundle != null) {
			realName = bundle.getString("realName");
			idCardNum = bundle.getString("idCardNum");
			bankCardNum = bundle.getString("bankCardNum");
			cardName = bundle.getString("cardName");
			bankNo = bundle.getString("bankNo");
			isAuthentication = bundle.getInt("isAuthentication");
			// 银行卡显示
			tv_bankCardNum.setText(cardName + "(" + bankCardNum.substring(bankCardNum.length() - 4, bankCardNum.length()) + ")");
			// 判断有没有实名
			if (isAuthentication == 1) {
				// 实名
				cet_name.setVisibility(View.GONE);
				tv_name.setVisibility(View.VISIBLE);
				tv_name.setText(StringUtil.nameReplace(realName));

				tv_idCardNum.setVisibility(View.VISIBLE);
				cet_idCardNum.setVisibility(View.GONE);
				tv_idCardNum.setText(StringUtil.idCardReplace(idCardNum));
			} else {
				cet_name.setVisibility(View.VISIBLE);
				tv_name.setVisibility(View.GONE);

				tv_idCardNum.setVisibility(View.GONE);
				cet_idCardNum.setVisibility(View.VISIBLE);

				if (StringUtil.isNotEmptyAndNull(realName) && StringUtil.isNotEmptyAndNull(idCardNum)) {
					// 有名字和身份证号
					cet_name.setHint(StringUtil.nameReplace(realName));
					cet_idCardNum.setHint(StringUtil.idCardReplace(idCardNum));
				} else {
					// 没有做过实名－让用户输入

				}
			}
		}
	}

	@Override public void onClick(int id) {
		if (id == R.id.iv_back) {
			finish();
		} else if (id == R.id.btn_confirm) {
			// 修改用户信息
			submit();
		} else if (id == R.id.tv_send_code) {
			// 发送验证码
			bindCardReqCode();
		} else if (id == R.id.tv_protocol) {
			// 点击用户协议-跳转到用户协议页面
			PwdH5Activity.intent(this,6);
		}
	}

	private void submit() {
		// 验证mobiletoken
		if (StringUtil.isEmpty(mobileToken)) {
			ToastUtil.showToast(context, getString(R.string.jrmf_rp_req_code));
			return;
		}
		// 判断验证码
		String phoneCode = cet_code.getText().toString().trim();
		if (StringUtil.isEmpty(phoneCode)) {
			ToastUtil.showToast(context, getString(R.string.jrmf_rp_input_code));
			return;
		}
		// 请求网络

		DialogDisplay.getInstance().dialogLoading(this, getString(R.string.jrmf_rp_loading), this);
		RpHttpManager.bindCard(context, userid, thirdToken, mobileToken, phoneCode, new OkHttpModelCallBack<BindCardModel>() {

			@Override public void onSuccess(BindCardModel baseModel) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				if (baseModel.isSuccess()) {
					// 绑卡成功－ 如果没有密码去设置密码
					ToastUtil.showToast(context, getString(R.string.jrmf_rp_add_card_suc));
					if (baseModel.isHasPwd != 1){//需要设置密码
						// 跳转到设置密码页面
						PwdH5Activity.intentFromAddCardSec(context,0,baseModel.url, ConstantUtil.FROM_ADD_CARD_SEC);
					}

					// 关闭添加银行卡第一步页面
					CusActivityManager.getInstance().finishActivity(AcfActivity.class);
					// 关闭当前页面
					finish();
				} else {
					ToastUtil.showToast(context, baseModel.respmsg);
				}
			}

			@Override public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				ToastUtil.showToast(context, getString(R.string.jrmf_rp_net_error_l));
			}
		});

	}

	/**
	 * 绑卡请求验证码
	 */
	private void bindCardReqCode() {
		// thirdToken,realName,identityNo,bankCardNo,mobileNo,bankNo
		// 验证姓名
		if (isAuthentication == 1) {
			// 用户已做过实名认证-不管
		} else {
			// 用户没有做过实名
			// 姓名
			if (StringUtil.isEmpty(cet_name.getText().toString().trim())) {
				// 用户没有输入名字
				if (StringUtil.isEmpty(realName)) {
					// 用户没有输入姓名
					ToastUtil.showToast(context, getString(R.string.jrmf_rp_name_error));
					return;
				}
			} else {
				realName = cet_name.getText().toString().trim();
			}

			// 身份证号
			if (StringUtil.isEmpty(cet_idCardNum.getText().toString().trim())) {
				// 用户没有输入身份证号
				if (!StringUtil.isIDCard(idCardNum)) {
					ToastUtil.showToast(context, getString(R.string.jrmf_rp_id_card_error));
					return;
				}
			} else {
				idCardNum = cet_idCardNum.getText().toString().trim();
			}

		}
		// 手机号
		final String mobileNum = cet_phone.getText().toString().trim();
		if (!StringUtil.isMobile(mobileNum)) {
			ToastUtil.showToast(context, getString(R.string.jrmf_rp_phone_tip));
			return;
		}
		// 银行卡编号
		if (StringUtil.isEmpty(bankNo)) {
			ToastUtil.showToast(context, getString(R.string.jrmf_rp_id_card_error));
			return;
		}
		// 验证通过－请求网络
		DialogDisplay.getInstance().dialogLoading(this, getString(R.string.jrmf_rp_loading), this);
		RpHttpManager.sendCode(context, userid, thirdToken, realName, idCardNum, bankNo, bankCardNum, mobileNum, new OkHttpModelCallBack<CodeModel>() {

			@Override public void onSuccess(CodeModel reqCodeModel) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				if (reqCodeModel == null) {
					ToastUtil.showToast(context, getString(R.string.jrmf_rp_net_error_l));
					return;
				}
				if (reqCodeModel.isSuccess()) {
					// 验证码发送成功-开始记时
					CountUtil countUtil = new CountUtil(60000, 1000, tv_send_code, 0);
					countUtil.start();

					ToastUtil.showToast(context, getString(R.string.jrmf_rp_verify_code_suss));
					mobileToken = reqCodeModel.mobileToken;
				} else {
					ToastUtil.showToast(context, reqCodeModel.respmsg);
				}
			}

			@Override public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				ToastUtil.showToast(context, getString(R.string.jrmf_rp_network_error));
			}
		});
	}
}
