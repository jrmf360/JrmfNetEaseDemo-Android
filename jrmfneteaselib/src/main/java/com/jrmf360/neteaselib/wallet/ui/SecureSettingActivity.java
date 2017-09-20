package com.jrmf360.neteaselib.wallet.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.fragment.LARDialogFragment;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.utils.ToastUtil;
import com.jrmf360.neteaselib.base.view.TitleBar;
import com.jrmf360.neteaselib.wallet.constants.Constants;
import com.jrmf360.neteaselib.wallet.http.WalletHttpManager;
import com.jrmf360.neteaselib.wallet.http.model.SecureSettingModel;
import com.jrmf360.neteaselib.wallet.manager.UserInfoManager;

/**
 * 安全设置页面
 * 
 * @author honglin
 *
 */
public class SecureSettingActivity extends BaseActivity implements LARDialogFragment.InputPwdErrorListener {

	private LARDialogFragment bindCardTipDialog;								// 绑卡弹框

	private RelativeLayout	rl_set_pwd, rl_get_pwd, rl_update_pwd;

	public static void intent(Activity fromActivity) {
		Intent intent = new Intent(fromActivity, SecureSettingActivity.class);
		fromActivity.startActivity(intent);
	}

	@Override public int getLayoutId() {
		return R.layout.jrmf_w_activity_secure_setting;
	}

	@Override public void initView() {
		titleBar = (TitleBar) findViewById(R.id.jrmf_w_titleBar);
		rl_set_pwd = (RelativeLayout) findViewById(R.id.rl_set_pwd);
		rl_get_pwd = (RelativeLayout) findViewById(R.id.rl_get_pwd);
		rl_update_pwd = (RelativeLayout) findViewById(R.id.rl_update_pwd);
	}

	@Override public void initListener() {
		titleBar.getIvBack().setOnClickListener(this);
		rl_set_pwd.setOnClickListener(this);
		rl_get_pwd.setOnClickListener(this);
		rl_update_pwd.setOnClickListener(this);
	}

	/**
	 * 密码状态改变 刷新页面
	 */
	public void refresh() {
		loadData();
	}

	@Override protected void initData(Bundle bundle) {
		titleBar.setTitle(getString(R.string.jrmf_w_secure_setting));
		DialogDisplay.getInstance().dialogLoading(this, getString(R.string.jrmf_w_loading));
		loadData();
	}

	/**
	 * 从网络获取数据
	 */
	private void loadData() {
		WalletHttpManager.secureSetting(context, new OkHttpModelCallBack<SecureSettingModel>() {

			@Override public void onSuccess(SecureSettingModel secureSettingModel) {
				DialogDisplay.getInstance().dialogCloseLoading(SecureSettingActivity.this);
				if (secureSettingModel.isSuccess()) {
					UserInfoManager.getInstance().setAuthentication(secureSettingModel.isAuthentication);
					if (secureSettingModel.isSetPwd == 0) {
						// 用户设置了密码
						rl_set_pwd.setVisibility(View.GONE);
						rl_get_pwd.setVisibility(View.VISIBLE);
						rl_update_pwd.setVisibility(View.VISIBLE);
					} else {
						// 用户没有设置密码
						rl_set_pwd.setVisibility(View.VISIBLE);
						rl_get_pwd.setVisibility(View.GONE);
						rl_update_pwd.setVisibility(View.GONE);
					}
				} else {
					ToastUtil.showToast(context, secureSettingModel.respmsg);
				}
			}

			@Override public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(SecureSettingActivity.this);
				ToastUtil.showToast(context, getString(R.string.jrmf_w_net_error_l));
			}
		});
	}

	@Override public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.iv_back) {
			finish();
		} else if (id == R.id.rl_update_pwd) {//修改密码
			PwdH5Activity.intent(context, 1, Constants.FROM_SECURE_SETTING);
		} else if (id == R.id.rl_get_pwd) {// 找回密码
			PwdH5Activity.intent(context, 2, Constants.FROM_SECURE_SETTING);
		} else if (id == R.id.rl_set_pwd) {//设置密码
			if (UserInfoManager.getInstance().getAuthentication() == 1){//已认证
				PwdH5Activity.intent(context, 0, Constants.FROM_SECURE_SETTING);
			}else{
				showBindCardTipDialog();
			}
		}
	}

	/**
	 * 显示绑卡提示弹框
	 *
	 */
	protected void showBindCardTipDialog() {
		bindCardTipDialog = DialogDisplay.getInstance().dialogLeftAndRight(LARDialogFragment.FROM_DEFAULT,
				getString(R.string.jrmf_w_auth_tip), getString(R.string.jrmf_w_quit),
				getString(R.string.jrmf_w_confirm), this);
		bindCardTipDialog.setCancelable(false);
		bindCardTipDialog.showAllowingStateLoss(getFragmentManager(),this.getClass().getSimpleName());
	}

	@Override
	public void onLeft() {
		if (bindCardTipDialog != null){
			bindCardTipDialog.dismissAllowingStateLoss();
			bindCardTipDialog = null;
		}
	}

	@Override
	public void onRight() {
		if (bindCardTipDialog != null){
			bindCardTipDialog.dismissAllowingStateLoss();
			bindCardTipDialog = null;
		}
		AuthenActivity.intent(context);
	}
}
