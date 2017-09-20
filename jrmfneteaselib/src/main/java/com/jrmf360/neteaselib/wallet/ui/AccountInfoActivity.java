package com.jrmf360.neteaselib.wallet.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.utils.ToastUtil;
import com.jrmf360.neteaselib.base.view.TitleBar;
import com.jrmf360.neteaselib.wallet.http.WalletHttpManager;
import com.jrmf360.neteaselib.wallet.http.model.AccountInfoModel;

/**
 * 账户信息页面 1 没密码 没绑卡 2 没密码 已绑卡 3 有免密 没绑卡 4 有密码 已绑卡
 *
 * @author honglin
 *
 */
public class AccountInfoActivity extends BaseActivity {

	private Button			btn_confirm;

	private TextView		tv_idCardNum, tv_name, tv_phoneNum,tv_bind_card,tv_tip;

	private ViewAnimator viewAnim;


	public static void intent(Activity fromActivity) {
		Intent intent = new Intent(fromActivity, AccountInfoActivity.class);
		fromActivity.startActivity(intent);

	}

	@Override public int getLayoutId() {
		return R.layout.jrmf_w_activity_account_info;
	}

	@Override public void initView() {
		titleBar = (TitleBar) findViewById(R.id.jrmf_w_titleBar);
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
		tv_bind_card = (TextView) findViewById(R.id.tv_bind_card);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_phoneNum = (TextView) findViewById(R.id.tv_phoneNum);
		tv_idCardNum = (TextView) findViewById(R.id.tv_idCardNum);
		tv_tip = (TextView) findViewById(R.id.tv_tip);
		viewAnim = (ViewAnimator) findViewById(R.id.viewAnim);
	}

	@Override public void initListener() {
		titleBar.getIvBack().setOnClickListener(this);
		btn_confirm.setOnClickListener(this);
	}

	@Override protected void initData(Bundle bundle) {
		titleBar.setTitle(getString(R.string.jrmf_w_account_info_title));
		loadHttpData();
	}

	public void refresh(){
		loadHttpData();
	}

	/**
	 * 加载网络数据
	 */
	private void loadHttpData() {
		DialogDisplay.getInstance().dialogLoading(this,getString(R.string.jrmf_w_loading), this);
		WalletHttpManager.getAccountInfo(context, new OkHttpModelCallBack<AccountInfoModel>() {

			@Override public void onSuccess(AccountInfoModel accountInfoModel) {
				if (accountInfoModel.isSuccess()) {
					if (accountInfoModel.isAuthentication == 1){
						viewAnim.setDisplayedChild(0);
						tv_idCardNum.setText(StringUtil.idCardReplace(accountInfoModel.identityNo));
						tv_name.setText(StringUtil.nameReplace(accountInfoModel.realName));
						tv_phoneNum.setText(StringUtil.phoneNumReplace(accountInfoModel.mobileNo));
						tv_tip.setText(getString(R.string.jrmf_w_service_phone));
					}else{
						viewAnim.setDisplayedChild(1);
					}
					viewAnim.setVisibility(View.VISIBLE);
					DialogDisplay.getInstance().dialogCloseLoading(AccountInfoActivity.this);
				} else {
					ToastUtil.showToast(context, accountInfoModel.respmsg);
				}
			}


			@Override public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(AccountInfoActivity.this);
				ToastUtil.showToast(context, getString(R.string.jrmf_w_account_net_error));
			}
		});
	}

	/**
	 * 完成假实名后，回到该页面并更新信息
	 */
//	public void togglePage(boolean isAuth, boolean hasPwd) {
//		if (!isAuth && !hasPwd) {
//			iv_bind_card.setVisibility(View.VISIBLE);
//			tv_bind_card.setGravity(Gravity.CENTER);
//			tv_bind_card.setVisibility(View.VISIBLE);
//			btn_confirm.setVisibility(View.VISIBLE);
//		}else if (!isAuth && hasPwd){
//			iv_bind_card.setVisibility(View.GONE);
//
//			tv_bind_card.setGravity(Gravity.LEFT);
//			tv_bind_card.setVisibility(View.VISIBLE);
//
//			ll_phoneNum.setVisibility(View.VISIBLE);
//			btn_confirm.setVisibility(View.VISIBLE);
//
//			ll_auth.setVisibility(View.GONE);
//			tv_tip.setVisibility(View.GONE);
//		}else if (isAuth && !hasPwd){
//			iv_bind_card.setVisibility(View.GONE);
//			tv_bind_card.setVisibility(View.GONE);
//			btn_confirm.setVisibility(View.GONE);
//			ll_phoneNum.setVisibility(View.GONE);
//
//			ll_auth.setVisibility(View.VISIBLE);
//			tv_tip.setText(getString(R.string.jrmf_w_service_phone));
//			tv_tip.setVisibility(View.VISIBLE);
//		}else{
//			iv_bind_card.setVisibility(View.GONE);
//			tv_bind_card.setVisibility(View.GONE);
//			btn_confirm.setVisibility(View.GONE);
//
//			ll_auth.setVisibility(View.VISIBLE);
//			ll_phoneNum.setVisibility(View.VISIBLE);
//			tv_tip.setText(getString(R.string.jrmf_w_service_phone));
//			tv_tip.setVisibility(View.VISIBLE);
//		}
//	}

	@Override public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.iv_back) {
			finish();
		} else if (id == R.id.btn_confirm) {
			// 添加银行卡
			AddCardFirstActivity.intent(context);
		}
	}
}















