package com.jrmf360.neteaselib.wallet.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.fragment.LARDialogFragment;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.utils.KeyboardUtil;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.utils.ToastUtil;
import com.jrmf360.neteaselib.base.view.ClearEditText;
import com.jrmf360.neteaselib.base.view.TitleBar;
import com.jrmf360.neteaselib.wallet.http.WalletHttpManager;
import com.jrmf360.neteaselib.wallet.http.model.BankInfoModel;

/**
 * 添加银行卡第一步
 * @author honglin
 *
 */
public class AddCardFirstActivity extends BaseActivity implements LARDialogFragment.InputPwdErrorListener {

	private ClearEditText cet_bankCardNum;
	private Button btn_next;
	private TextView tv_support;
	private LARDialogFragment pwdErrorDialog;						// 乌行校验失败弹框

	public static void intent(Activity fromActivity){
		Intent intent = new Intent(fromActivity,AddCardFirstActivity.class);
		fromActivity.startActivity(intent);
	}
	@Override
	public int getLayoutId() {
		return R.layout.jrmf_w_activity_add_card_first;
	}
	
	@Override
	public void initView() {
		titleBar = (TitleBar) findViewById(R.id.jrmf_w_titleBar);
		cet_bankCardNum = (ClearEditText) findViewById(R.id.cet_bankCardNum);
		btn_next = (Button) findViewById(R.id.btn_next);
		tv_support = (TextView) findViewById(R.id.tv_support);
	}
	
	@Override
	public void initListener() {
		titleBar.getIvBack().setOnClickListener(this);
		btn_next.setOnClickListener(this);
		tv_support.setOnClickListener(this);
	}
	
	@Override
	protected void initData(Bundle bundle) {
		titleBar.setTitle(getString(R.string.jrmf_w_add_card));
		KeyboardUtil.popInputMethod(cet_bankCardNum);
	}

	
	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.iv_back) {
			onBackPressed();
		}else if(id == R.id.btn_next){
			//请求网络
			submit();
		}else if (id == R.id.tv_support) {
			//跳到支持的银行卡列表页面
			SupportBankCardActivity.intent(this);
		}
	}
	/**
	 * 提交用户输入的密码
	 */
	private void submit() {
		String bankCardNum = cet_bankCardNum.getText().toString().trim();
		if (!StringUtil.checkBankCard(bankCardNum)) {
			ToastUtil.showToast(context, getString(R.string.jrmf_w_card_num_error));
			return;
		}
		
		DialogDisplay.getInstance().dialogLoading(context, "",this);
		WalletHttpManager.VerifyBankInfo(context,bankCardNum,new OkHttpModelCallBack<BankInfoModel>() {
			
			@Override
			public void onSuccess(BankInfoModel bankInfoModel) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				if (bankInfoModel == null) {

					ToastUtil.showToast(context, getString(R.string.jrmf_w_net_error_l));
					return;
				}
				
				if (bankInfoModel.isSuccess()) {
					//请求成功
					AddCardSecondActivity.intent(AddCardFirstActivity.this, bankInfoModel.realName,bankInfoModel.bankName, 
							bankInfoModel.identityNo,bankInfoModel.bankCardNo,bankInfoModel.isAuthentication,bankInfoModel.bankNo);
				}else {
					ToastUtil.showToast(context, bankInfoModel.respmsg);
				}
			}
			@Override
			public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				ToastUtil.showToast(context, getString(R.string.jrmf_w_net_error_l));
			}
		});
		
	}

	/**
	 * 显示放弃绑卡提示
	 */
	private void showCheckFailDialog() {
		pwdErrorDialog = DialogDisplay.getInstance().dialogLeftAndRight(LARDialogFragment.FROM_DEFAULT,getString(R.string.jrmf_w_quit_bind_card), getString(R.string.jrmf_w_no), getString(R.string.jrmf_w_yes), this);
		pwdErrorDialog.setCancelable(false);
		pwdErrorDialog.showAllowingStateLoss(getFragmentManager(),this.getClass().getSimpleName());
	}

	@Override
	public void onLeft() {
		pwdErrorDialog.dismissAllowingStateLoss();
	}

	@Override
	public void onRight() {
		pwdErrorDialog.dismissAllowingStateLoss();
		finish();
	}

	@Override
	public void onBackPressed() {
		String cardNum = cet_bankCardNum.getText().toString().trim();
		if (StringUtil.isEmptyOrNull(cardNum)){
			super.onBackPressed();
		}else{
			//弹框-提示
			showCheckFailDialog();
		}
	}
}











