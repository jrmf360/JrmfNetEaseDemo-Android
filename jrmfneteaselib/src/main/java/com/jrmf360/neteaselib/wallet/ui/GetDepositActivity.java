package com.jrmf360.neteaselib.wallet.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.fragment.LARDialogFragment;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.utils.ImageLoadUtil;
import com.jrmf360.neteaselib.base.utils.NoDoubleClickUtils;
import com.jrmf360.neteaselib.base.utils.ScreenUtil;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.utils.ToastUtil;
import com.jrmf360.neteaselib.base.view.ClearEditText;
import com.jrmf360.neteaselib.base.view.TitleBar;
import com.jrmf360.neteaselib.wallet.constants.Constants;
import com.jrmf360.neteaselib.wallet.http.WalletHttpManager;
import com.jrmf360.neteaselib.wallet.http.model.AccountModel;
import com.jrmf360.neteaselib.wallet.http.model.GetDepositModel;
import com.jrmf360.neteaselib.wallet.http.model.RedeemInfoModel;
import com.jrmf360.neteaselib.wallet.manager.UserInfoManager;

import java.util.List;

/**
 * 零钱提现页面
 * 
 * @author honglin
 *
 */
public class GetDepositActivity extends BaseActivity implements LARDialogFragment.InputPwdErrorListener {

	private List<AccountModel> accountList;																	// 用户已绑定的银行卡

	private int							lastIndex		= 0;															// 上次选中的银行卡索引

	private int							PAY_TYPE		= -1;

	private ClearEditText cet_get_money_time, cet_get_money;

	private LinearLayout				ll_add_card, ll_bankCard, ll_paytype_container;

	private ImageView					iv_bankIcon;

	private TextView					tv_cardName, tv_cardType, tv_limit, tv_all_money, tv_show_explain, tv_amoun_tip;

	private View						btn_next, buttomLayout;

	private FrameLayout					fl_not_deposit_amount;															// 不可提现金额说明

	private String						limitMoney;																		// 可用零钱

	private Dialog						dialog;

	private int							PWD_H5_REQ_CODE	= 0x120;

	private int whCheckFailDialog = 0,notDepExplainDialog = 1,mDialogType;

	private LARDialogFragment pwdErrorDialog;	// 乌行校验失败弹框

	public static void intent(Activity fromActivity) {
		Intent intent = new Intent(fromActivity, GetDepositActivity.class);
		fromActivity.startActivity(intent);
	}

	@Override public int getLayoutId() {
		return R.layout.jrmf_w_activity_getdeposit;
	}

	@Override public void initView() {
		titleBar = (TitleBar) findViewById(R.id.jrmf_w_titleBar);
		cet_get_money_time = (ClearEditText) findViewById(R.id.cet_get_money_time);
		cet_get_money = (ClearEditText) findViewById(R.id.cet_get_money);
		ll_add_card = (LinearLayout) findViewById(R.id.ll_add_card);
		ll_bankCard = (LinearLayout) findViewById(R.id.ll_bankCard);
		iv_bankIcon = (ImageView) findViewById(R.id.iv_bankIcon);
		tv_cardName = (TextView) findViewById(R.id.tv_cardName);
		tv_cardType = (TextView) findViewById(R.id.tv_cardType);
		tv_limit = (TextView) findViewById(R.id.tv_limit);

		tv_all_money = (TextView) findViewById(R.id.tv_all_money);
		tv_amoun_tip = (TextView) findViewById(R.id.tv_amoun_tip);
		tv_show_explain = (TextView) findViewById(R.id.tv_show_explain);
		fl_not_deposit_amount = (FrameLayout) findViewById(R.id.fl_not_deposit_amount);
		btn_next = findViewById(R.id.btn_next);
	}

	@Override public void initListener() {
		titleBar.getIvBack().setOnClickListener(this);
		btn_next.setOnClickListener(this);
		ll_add_card.setOnClickListener(this);
		ll_bankCard.setOnClickListener(this);
		tv_all_money.setOnClickListener(this);
		tv_show_explain.setOnClickListener(this);
		//设置过滤器
		cet_get_money.setFilters(new InputFilter[] { new InputMoney() });
		cet_get_money.addTextChangedListener(new TextWatcher() {

			@Override public void onTextChanged(CharSequence s, int start, int before, int count) {
				checkOverMoney(s);
			}

			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override public void afterTextChanged(Editable s) {}
		});
	}

	@Override protected void initData(Bundle bundle) {
		titleBar.setTitle(getString(R.string.jrmf_w_select_get_deposit));
		loadInfo();
		DialogDisplay.getInstance().dialogLoading(context, getString(R.string.jrmf_w_loading));

	}

	@Override public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.btn_next) {
			// 显示弹框
			if (!NoDoubleClickUtils.isDoubleClick()) {
				getDeposit();
			}
		} else if (id == R.id.ll_add_card) {
			// 跳转到添加银行卡页面
			AddCardFirstActivity.intent(context);
		} else if (id == R.id.ll_bankCard) {
			// 选择银行卡弹框
			showButtomDialog();
		} else if (id == R.id.iv_quit) {
			dialog.dismiss();
		} else if (id == R.id.tv_all_money) {
			// 全部提现
			if (StringUtil.isNotEmptyAndNull(limitMoney)) {
				cet_get_money.setText(StringUtil.formatMoney(limitMoney));
				cet_get_money.setSelection(String.valueOf(limitMoney).length());
			}
		} else if (id == R.id.iv_back) {
			finish();
		}else if (id == R.id.tv_show_explain){
			showCheckFailDialog(getString(R.string.jrmf_w_not_deposit_explain),notDepExplainDialog);
		}

	}

	/**
	 * 从网络获得提现页面信息
	 */
	public void loadInfo() {
		WalletHttpManager.redeemInfo(context, new OkHttpModelCallBack<RedeemInfoModel>() {

			public void onSuccess(RedeemInfoModel redeemInfoModel) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				if (redeemInfoModel.isSuccess()) {
					// 是否有银行卡
					if (redeemInfoModel.accountList != null && redeemInfoModel.accountList.size() > 0) {
						accountList = redeemInfoModel.accountList;
						// 绑定了银行卡
						ll_add_card.setVisibility(View.GONE);
						ll_bankCard.setVisibility(View.VISIBLE);
						AccountModel accountModel = redeemInfoModel.accountList.get(0);
						tv_cardName.setText(accountModel.bankName);
						tv_cardType.setText(String.format(getString(R.string.jrmf_w_card_des), accountModel.bankCardNoDesc));
						if (StringUtil.isNotEmpty(accountModel.logo_url)) {
							ImageLoadUtil.getInstance().loadImage(iv_bankIcon, accountModel.logo_url);
						}
						tv_limit.setVisibility(View.VISIBLE);
						lastIndex = 0;
					} else {
						// 没有绑定银行卡
						ll_add_card.setVisibility(View.VISIBLE);
						ll_bankCard.setVisibility(View.GONE);
						// 如果没有绑定银行卡-就隐藏该卡最多可提现
						tv_limit.setVisibility(View.INVISIBLE);
					}
					// 到账时间
					cet_get_money_time.setText(redeemInfoModel.redeemDesc);

					// 卡的提现限制 - 本次可转出
					limitMoney = redeemInfoModel.balance;
					String limit = getString(R.string.jrmf_w_card_limit);
					if (StringUtil.string2double(redeemInfoModel.balance) < StringUtil.string2double(redeemInfoModel.maxRedeemMoney)) {
						limitMoney = redeemInfoModel.balance;
						tv_limit.setText(String.format(limit, redeemInfoModel.balance));
					} else {
						limitMoney = redeemInfoModel.maxRedeemMoney;
						tv_limit.setText(String.format(limit, redeemInfoModel.maxRedeemMoney));
					}
					//在途金额
					if (StringUtil.formatMoneyDouble(redeemInfoModel.transitamount) > 0){//在途金额大于0
						fl_not_deposit_amount.setVisibility(View.VISIBLE);
						tv_amoun_tip.setText(String.format(getString(R.string.jrmf_w_not_deposit_amount),redeemInfoModel.transitamount));
					}else{
						fl_not_deposit_amount.setVisibility(View.GONE);
					}

				} else {
					ToastUtil.showToast(context, redeemInfoModel.respmsg);
				}
			}

			@Override public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				ToastUtil.showToast(context, getString(R.string.jrmf_w_net_error_l));
			}
		});
	}

	/**
	 * 用户点击下一步 提现
	 */
	private void getDeposit() {
		// 判断有没有选择银行卡
		if (accountList == null || accountList.size() <= 0) {
			ToastUtil.showToast(context, getString(R.string.jrmf_w_no_card_bind));
			return;
		}

		//判断银行卡是否可提现
		if (lastIndex >= 0){
			AccountModel accountModel = accountList.get(lastIndex);
			if (accountModel != null && accountModel.isRedeemCard != 1){//该银行卡不可提现
				showCheckFailDialog(getString(R.string.jrmf_w_card_not_deposit_tip),whCheckFailDialog);
				return;
			}
		}

		// 验证提现金额
		String money = cet_get_money.getText().toString().trim();
		if (StringUtil.isEmpty(money)) {
			ToastUtil.showToast(context, getString(R.string.jrmf_w_no_input_amount));
			return;
		}
		// 验证输入金额格式
		if (money.endsWith(".")) {
			ToastUtil.showToast(context, getString(R.string.jrmf_w_money_format_error));
			return;
		}
		// 验证提现金额是否超限
		if (Double.valueOf(money) > Double.valueOf(limitMoney)) {
			// 提现金额超限
			ToastUtil.showNoWaitToast(context, getString(R.string.jrmf_w_get_deposit_over));
			return;
		}
		// 验证提现金额不能<=0
		if (Double.valueOf(money) <= 0) {
			ToastUtil.showToast(context, getString(R.string.jrmf_w_input_amount_error));
			return;
		}

		// 如果需要设置密码－跳转到设置密码页面
		if (!UserInfoManager.getInstance().isHasPwd()) {
			PwdH5Activity.intent(context, 0, Constants.FROM_GET_DEPOSIT);
			return;
		}

		// 请求网络进行提现
		reqDeposit();
	}

	/**
	 * 用户点击切换付款方式
	 *
	 */
	public void showButtomDialog() {
		if (dialog == null) {
			dialog = new Dialog(context, R.style.Jrmf_w_ActionSheetDialogStyle);
			// 填充对话框的布局
			buttomLayout = LayoutInflater.from(context).inflate(R.layout.jrmf_w_dialog_select_pay_type, null);
			ll_paytype_container = (LinearLayout) buttomLayout.findViewById(R.id.ll_paytype_container);
			TextView tv_title = (TextView) buttomLayout.findViewById(R.id.tv_title);
			tv_title.setText(getString(R.string.jrmf_w_select_bank_card));
			View ivQuit = buttomLayout.findViewById(R.id.iv_quit);
			ivQuit.setOnClickListener(this);
			converseModel();
			WindowManager.LayoutParams windowparams = dialog.getWindow().getAttributes();
			// 将布局设置给Dialog
			dialog.setContentView(buttomLayout);
			// 获取当前Activity所在的窗体
			Window dialogWindow = dialog.getWindow();
			// 设置Dialog从窗体底部弹出
			dialogWindow.setGravity(Gravity.BOTTOM);
			windowparams.height = ScreenUtil.dp2px(context, 300);
			windowparams.width = ScreenUtil.getScreenWidth(context);
			// 获得窗体的属性
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
			// lp.y = 20;// 设置Dialog距离底部的距离
			// 将属性设置给窗体
			dialogWindow.setAttributes(lp);
		} else {
			converseModel();
		}
		dialog.show();// 显示对话框
	}

	/**
	 * 把网络请求数据转化成model
	 */
	private void converseModel() {
		// 添加银行卡
		ll_paytype_container.removeAllViews();
		if (accountList == null || accountList.size() <= 0) {
			// 没有银行卡
			return;
		}

		for (int i = 0; i < accountList.size(); i++) {
			AccountModel accountModel = accountList.get(i);
			final int index = i;
			RelativeLayout bankCradView = (RelativeLayout) View.inflate(context, R.layout.jrmf_w_item_select_pay_type, null);
			RelativeLayout rl_pay_type = (RelativeLayout) bankCradView.findViewById(R.id.rl_pay_type);
			final ImageView iv_selected = (ImageView) bankCradView.findViewById(R.id.iv_selected);
			ImageView iv_bankCardLogo = (ImageView) bankCradView.findViewById(R.id.iv_bankCardLogo);
			TextView tv_cardName = (TextView) bankCradView.findViewById(R.id.tv_cardName);
			tv_cardName.setText(String.format(getString(R.string.jrmf_w_cardname_card_des), accountModel.bankName, accountModel.bankCardNoDesc));
			if (StringUtil.isNotEmpty(accountModel.logo_url)) {
				ImageLoadUtil.getInstance().loadImage(iv_bankCardLogo, accountModel.logo_url);
			}
			if (lastIndex == i) {
				iv_selected.setVisibility(View.VISIBLE);
			}
			rl_pay_type.setOnClickListener(new View.OnClickListener() {

				@Override public void onClick(View v) {
					selectedCard(Constants.PAY_TYPE_JD, index);
				}
			});
			ll_paytype_container.addView(bankCradView, RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtil.dp2px(context, 48));
		}

		// 使用新卡支付
		RelativeLayout newCardView = (RelativeLayout) View.inflate(context, R.layout.jrmf_w_item_new_card, null);
		RelativeLayout rl_new_card = (RelativeLayout) newCardView.findViewById(R.id.rl_new_card);
		rl_new_card.setOnClickListener(new View.OnClickListener() {

			@Override public void onClick(View v) {
				selectedCard(Constants.PAY_TYPE_NEWCARD, -1);
			}
		});
		ll_paytype_container.addView(newCardView, RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtil.dp2px(context, 48));
		// 账户余额
	}

	/**
	 * 用户选择了支付方式 只能选择新卡支付和已有银行卡支付（也是京东支付）
	 */
	private void selectedCard(int payType, int index) {
		if (index >= 0) {
			lastIndex = index;
		}
		// 弹框消失
		dialog.dismiss();
		PAY_TYPE = payType;
		if (PAY_TYPE == Constants.PAY_TYPE_NEWCARD) {
			// 新卡支付－跳转到添加银行卡页面
			AddCardFirstActivity.intent(context);

		} else {
			// 京东支付，根据索引找到卡信息
			AccountModel accountModel = accountList.get(index);
			tv_cardName.setText(accountModel.bankName);
			tv_cardType.setText(String.format(getString(R.string.jrmf_w_card_des), accountModel.bankCardNoDesc));
			if (StringUtil.isNotEmpty(accountModel.logo_url)) {
				ImageLoadUtil.getInstance().loadImage(iv_bankIcon, accountModel.logo_url);
			}
		}
	}

	/**
	 * 用户输完密码请求网络提现
	 *
	 */
	private void reqDeposit() {
		// redeemType 1收费 2 免费 如果没有密码需要先设置密码再去提现
		AccountModel accountModel = accountList.get(lastIndex);
		DialogDisplay.getInstance().dialogLoading(context, getString(R.string.jrmf_w_loading), this);
		WalletHttpManager.reqDeposit(context, accountModel.bankCardNo, cet_get_money.getText().toString().trim(), new OkHttpModelCallBack<GetDepositModel>() {

					@Override public void onSuccess(GetDepositModel depositModel) {
						DialogDisplay.getInstance().dialogCloseLoading(context);
						if (depositModel == null) {
							ToastUtil.showToast(context, getString(R.string.jrmf_w_net_error_l));
							return;
						}
						if (depositModel.isSuccess()) {
							// 提现请求成功
							PwdH5Activity.intentForResult(context, 5, depositModel.url, PWD_H5_REQ_CODE);
						} else if ("ER024".equals(depositModel.respstat)) {
							// 提现失败-乌行校验失败-弹框提示
							showCheckFailDialog(depositModel.respmsg,whCheckFailDialog);
						} else {
							ToastUtil.showToast(context, depositModel.respmsg);
						}
					}

					@Override public void onFail(String result) {
						DialogDisplay.getInstance().dialogCloseLoading(context);
						ToastUtil.showToast(context, getString(R.string.jrmf_w_net_error_l));
					}
				});
	}

	/**
	 * 显示乌行校验不通过弹框
	 *
	 * @param title
	 */
	private void showCheckFailDialog(String title,int type) {
		mDialogType = type;
		String title2 = StringUtil.ToDBC(title);
		String left = "";
		String right;
		if (type == whCheckFailDialog){
			left = getString(R.string.jrmf_w_add_card);
			right = getString(R.string.jrmf_w_select_bank_card);
		}else{
			right = getString(R.string.jrmf_w_confirm);
		}
		pwdErrorDialog = DialogDisplay.getInstance().dialogLeftAndRight(LARDialogFragment.GET_DEPOSIT_WH_ERROR, title2, left, right, this);
		pwdErrorDialog.showAllowingStateLoss(getFragmentManager(), this.getClass().getSimpleName());
	}

	@Override public void onLeft() {
		if (mDialogType == whCheckFailDialog){//添加新银行卡
			AddCardFirstActivity.intent(context);
			bindRedeemCard();
		}
		pwdErrorDialog.dismissAllowingStateLoss();
	}

	@Override public void onRight() {
		if (mDialogType == whCheckFailDialog){// 选择银行卡
			showButtomDialog();
			bindRedeemCard();
		}
		pwdErrorDialog.dismissAllowingStateLoss();
	}

	/**
	 * 检查提现金额是否超限
	 *
	 * @param s
	 */
	protected void checkOverMoney(CharSequence s) {
		// 验证提现金额是否超限
		if (StringUtil.isEmpty(limitMoney)) {
			return;
		}
		Double getMoney = StringUtil.string2double(s.toString());
		if (getMoney == null) {
			return;
		}
		// 验证提现金额是否超限
		if (getMoney.doubleValue() > StringUtil.formatMoneyDouble(limitMoney)) {
			// 提现金额超限
			ToastUtil.showNoWaitToast(context, getString(R.string.jrmf_w_get_deposit_over));
		}
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == PWD_H5_REQ_CODE) {
				// 校验密码返回
				ToastUtil.showToast(context, getString(R.string.jrmf_w_get_deposit_success));
				finish();
			}
		}
	}


	/**
	 * 绑提现银行卡
	 */
	private void bindRedeemCard(){
		AccountModel accountModel = accountList.get(lastIndex);
		if (accountModel == null){
			return;
		}
		WalletHttpManager.bindRedeemCard(context,accountModel.bankCardNo,null);
	}

	/**
	 * 限制输入金额两位小数，并且总位数八位
	 *
	 */
	private class InputMoney implements InputFilter {

		@Override public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			if (source.toString().equals(".") && dstart == 0 && dend == 0) {// 判断小数点是否在第一位
				cet_get_money.setText(0 + "" + source + dest);// 给小数点前面加0
				cet_get_money.setSelection(2);// 设置光标
			}

			if (dstart >= 8){
				return "";
			}

			if (dest.toString().indexOf(".") != -1 && (dest.length() - dest.toString().indexOf(".")) > 2) {// 判断小数点是否存在并且小数点后面是否已有两个字符
				if ((dest.length() - dstart) < 3) {// 判断现在输入的字符是不是在小数点后面
					return "";// 过滤当前输入的字符
				}
			}

			return null;
		}
	}

}
