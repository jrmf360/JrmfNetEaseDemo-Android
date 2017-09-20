package com.jrmf360.neteaselib.wallet.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.utils.LogUtil;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.utils.ToastUtil;
import com.jrmf360.neteaselib.base.view.TitleBar;
import com.jrmf360.neteaselib.wallet.http.WalletHttpManager;
import com.jrmf360.neteaselib.wallet.http.model.WalletModel;
import com.jrmf360.neteaselib.wallet.manager.UserInfoManager;

/**
 * 如果没有拿到token，就不让进入到该页面
 * 
 * @author honglin
 *
 */
public class MyWalletActivity extends BaseActivity {

	private TitleBar titleBar;

	private TextView		tv_cardNum, tv_change;

	private LinearLayout	ll_recharge, ll_getDeposit;

	private RelativeLayout	rl_accountInfo, rl_mybank, rl_tradeDetail, rl_secureSetting, rl_my_rp;

	/**
	 *
	 * @param fromActivity
	 * @param userId
	 * @param thirdToken
	 * @param userName
	 * @param userIcon
	 */
	public static void intent(Activity fromActivity, String userId, String thirdToken, String userName, String userIcon) {
		Intent intent = new Intent(fromActivity, MyWalletActivity.class);
		UserInfoManager.getInstance().clear();
		UserInfoManager.getInstance().setUserId(userId);
		UserInfoManager.getInstance().setThirdToken(thirdToken);
		UserInfoManager.getInstance().setUserName(userName);
		UserInfoManager.getInstance().setUserIcon(userIcon);
		fromActivity.startActivity(intent);
	}

	@Override public int getLayoutId() {
		return R.layout.jrmf_w_activity_my_wallet;
	}

	@Override public void initView() {
		titleBar = (TitleBar) findViewById(R.id.jrmf_w_titleBar);
		ll_recharge = (LinearLayout) findViewById(R.id.ll_wallet_recharge);
		ll_getDeposit = (LinearLayout) findViewById(R.id.ll_getDeposit);
		rl_accountInfo = (RelativeLayout) findViewById(R.id.rl_accountInfo);
		rl_mybank = (RelativeLayout) findViewById(R.id.rl_mybank);
		rl_tradeDetail = (RelativeLayout) findViewById(R.id.rl_tradeDetail);
		rl_secureSetting = (RelativeLayout) findViewById(R.id.rl_secureSetting);
		rl_my_rp = (RelativeLayout) findViewById(R.id.rl_my_rp);
		tv_cardNum = (TextView) findViewById(R.id.tv_cardNum);
		tv_change = (TextView) findViewById(R.id.tv_change);

	}

	@Override public void initListener() {
		titleBar.getIvBack().setOnClickListener(this);
		ll_recharge.setOnClickListener(this);
		ll_getDeposit.setOnClickListener(this);
		rl_accountInfo.setOnClickListener(this);
		rl_mybank.setOnClickListener(this);
		rl_tradeDetail.setOnClickListener(this);
		rl_secureSetting.setOnClickListener(this);
		rl_my_rp.setOnClickListener(this);
	}

	@Override protected void initData(Bundle bundle) {
		titleBar.setTitle(getString(R.string.jrmf_w_title));
		DialogDisplay.getInstance().dialogLoading(context, getString(R.string.jrmf_w_loading), this);
	}

	@Override protected void onStart() {
		super.onStart();
		loadHttpData();
		LogUtil.i("onstart");
	}

	public void refresh() {
		loadHttpData();
	}

	/**
	 * 从网络加载数据
	 */
	private void loadHttpData() {
		WalletHttpManager.wallet(context, UserInfoManager.getInstance().getUserName(),
				UserInfoManager.getInstance().getUserIcon(), new OkHttpModelCallBack<WalletModel>() {

			@Override public void onSuccess(WalletModel walletModel) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				if (walletModel.isSuccess()) {
					if (StringUtil.isEmpty(walletModel.balance)) {
						tv_change.setText("0.00");
					} else {
						tv_change.setText(walletModel.balance);
					}
					tv_cardNum.setText(walletModel.userCardCount + getString(R.string.jrmf_w_zhang));
					if (1 == walletModel.isSupportRedEnvelope) {
						// 支持红包
						rl_my_rp.setVisibility(View.VISIBLE);
					}
					UserInfoManager.getInstance().setHasPwd(walletModel.isHasPwd == 1);
					UserInfoManager.getInstance().setAuthentication(walletModel.isAuthentication);
				} else {
					ToastUtil.showToast(context, walletModel.respmsg);
				}
			}

			@Override public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				ToastUtil.showToast(context, getString(R.string.jrmf_w_net_error_l));
			}
		});
	}

	@Override public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.iv_back) {
			finish();
		} else if (id == R.id.ll_wallet_recharge) {
			// 充值 － 直接跳转到充值页面 －在充值页面判断是否需要密码
			RechargeActivity.intent(this);
		} else if (id == R.id.ll_getDeposit) {
			// 提现 － 直接跳转到提现页面 － 在提现页面判断是否需要设置密码
			GetDepositActivity.intent(this);
		} else if (id == R.id.rl_accountInfo) {// 账户信息
			AccountInfoActivity.intent(this);
		} else if (id == R.id.rl_mybank) {// 银行卡设置
			BankSettingActivity.intent(this);
		} else if (id == R.id.rl_tradeDetail) {// 收支明细
			TradeHistoryActivity.intent(this);
		} else if (id == R.id.rl_secureSetting) {// 安全设置
			SecureSettingActivity.intent(this);
		} else if (id == R.id.rl_my_rp) {
			// 我的红包
			MyRpActivity.intent(this);
		}
	}
}
