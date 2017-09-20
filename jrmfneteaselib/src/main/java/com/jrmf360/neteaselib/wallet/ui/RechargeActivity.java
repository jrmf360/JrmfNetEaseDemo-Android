package com.jrmf360.neteaselib.wallet.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.fragment.LARDialogFragment;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.model.BaseModel;
import com.jrmf360.neteaselib.base.utils.ImageLoadUtil;
import com.jrmf360.neteaselib.base.utils.KeyboardUtil;
import com.jrmf360.neteaselib.base.utils.NoDoubleClickUtils;
import com.jrmf360.neteaselib.base.utils.ScreenUtil;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.utils.ToastUtil;
import com.jrmf360.neteaselib.base.view.ClearEditText;
import com.jrmf360.neteaselib.base.view.TitleBar;
import com.jrmf360.neteaselib.base.view.passwordview.GridPasswordView;
import com.jrmf360.neteaselib.wallet.constants.Constants;
import com.jrmf360.neteaselib.wallet.fragment.InputPwdDialogFragment;
import com.jrmf360.neteaselib.wallet.http.WalletHttpManager;
import com.jrmf360.neteaselib.wallet.http.model.AccountModel;
import com.jrmf360.neteaselib.wallet.http.model.ChargeInfoModel;
import com.jrmf360.neteaselib.wallet.http.model.ChargeReqCode;
import com.jrmf360.neteaselib.wallet.manager.UserInfoManager;

import java.util.List;


/**
 * 充值页面
 * 
 * @author honglin
 *
 */
public class RechargeActivity extends BaseActivity implements LARDialogFragment.InputPwdErrorListener {

	private LARDialogFragment setPwdDialog;	// 乌行校验失败弹框

	private int						PAY_TYPE	= -1;

	private InputPwdDialogFragment dialogFragment;

	private List<AccountModel> accountList;							// 用户已绑定的银行卡

	private Dialog					dialog;

	private View					buttomLayout;

	private LinearLayout			ll_bankCard, ll_add_card,ll_paytype_container;

	private ImageView				iv_bankCardIcon;

	private TextView				tv_cardName, tv_cardType, tv_mount_tip;

	private ClearEditText cet_money;

	private Button					btn_next;

	private int						lastIndex	= 0;						// 上次选中的银行卡索引

	private float					rechageLimit;							// 充值限额

	public static void intent(Activity fromActivity) {
		Intent intent = new Intent(fromActivity, RechargeActivity.class);
		fromActivity.startActivity(intent);

	}

	@Override public int getLayoutId() {
		return R.layout.jrmf_w_activity_recharge;
	}

	@Override public void initView() {
		titleBar = (TitleBar) findViewById(R.id.jrmf_w_titleBar);
		ll_bankCard = (LinearLayout) findViewById(R.id.ll_bankCard);
		ll_add_card = (LinearLayout) findViewById(R.id.ll_add_card);
		iv_bankCardIcon = (ImageView) findViewById(R.id.iv_bankCardIcon);
		tv_cardName = (TextView) findViewById(R.id.tv_cardName);
		tv_cardType = (TextView) findViewById(R.id.tv_cardType);
		tv_mount_tip = (TextView) findViewById(R.id.tv_mount_tip);
		cet_money = (ClearEditText) findViewById(R.id.cet_money);
		btn_next = (Button) findViewById(R.id.btn_next);
	}

	@Override public void initListener() {
		titleBar.getIvBack().setOnClickListener(this);
		btn_next.setOnClickListener(this);
		ll_bankCard.setOnClickListener(this);
		ll_add_card.setOnClickListener(this);
		cet_money.setFilters(new InputFilter[] { new InputMoney() });
	}

	@Override protected void initData(Bundle bundle) {
		titleBar.setTitle(getString(R.string.jrmf_w_recharge));
		DialogDisplay.getInstance().dialogLoading(this,getString(R.string.jrmf_w_loading),this);
		loadHttpData();
	}

	public void refresh() {
		loadHttpData();
	}

	/**
	 * 从网络加载数据
	 */
	private void loadHttpData() {
		WalletHttpManager.chargeInfo(context, new OkHttpModelCallBack<ChargeInfoModel>() {

			@Override public void onSuccess(ChargeInfoModel chargeInfoModel) {
				DialogDisplay.getInstance().dialogCloseLoading(RechargeActivity.this);
				if (chargeInfoModel == null) {
					ToastUtil.showToast(context, getString(R.string.jrmf_w_net_error_l));
					ll_bankCard.setVisibility(View.GONE);
					DialogDisplay.getInstance().dialogCloseLoading(RechargeActivity.this);
					return;
				}

				if (chargeInfoModel.isSuccess()){
					if (chargeInfoModel.accountList == null || chargeInfoModel.accountList.size() <= 0) {
						// 没有绑定银行卡
						ll_add_card.setVisibility(View.VISIBLE);
						tv_mount_tip.setVisibility(View.INVISIBLE);
					} else {
						// 绑定了银行卡
						ll_add_card.setVisibility(View.GONE);
						ll_bankCard.setVisibility(View.VISIBLE);
						accountList = chargeInfoModel.accountList;
						AccountModel accountModel = chargeInfoModel.accountList.get(0);
						tv_cardName.setText(accountModel.bankName);
						tv_cardType.setText(String.format(getString(R.string.jrmf_w_card_des), accountModel.bankCardNoDesc));
						tv_mount_tip.setVisibility(View.VISIBLE);
						tv_mount_tip.setText(String.format(getString(R.string.jrmf_w_card_recharge_limit), accountModel.payOrderlimit));
						rechageLimit = StringUtil.formatMoneyFloat(accountModel.payOrderlimit);
						if (StringUtil.isNotEmpty(accountModel.logo_url)) {
							ImageLoadUtil.getInstance().loadImage(iv_bankCardIcon, accountModel.logo_url);

						}
						KeyboardUtil.popInputMethod(cet_money);
					}
				}else{
					ToastUtil.showToast(context,chargeInfoModel.respmsg);
				}

			}

			@Override public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(RechargeActivity.this);
				ToastUtil.showToast(context, getString(R.string.jrmf_w_net_error_l));
				ll_bankCard.setVisibility(View.GONE);
			}
		});

	}

	@Override public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.iv_back) {
			finish();
		} else if (id == R.id.ll_bankCard) {
			// 选择银行卡
			showButtomDialog();
		} else if (id == R.id.btn_next) {
			// 点击下一步-先显示弹框
			if (!NoDoubleClickUtils.isDoubleClick()) {
				reqVerifyCode();
			}
		} else if (id == R.id.iv_quit) {
			// 关闭dialog
			dialog.dismiss();
		} else if (id == R.id.ll_add_card) {
			// 跳到添加银行页面
			AddCardFirstActivity.intent(this);
		}
	}

	/**
	 * 充值请求验证码
	 */
	private void reqVerifyCode() {
		if (accountList == null || accountList.size() <= 0) {
			// 还没有银行卡－需要先添加银行卡
			ToastUtil.showToast(context, getString(R.string.jrmf_w_no_card_bind));
			return;
		}
		// 验证充值金额
		String amount = cet_money.getText().toString().trim();
		if (StringUtil.isEmpty(amount)) {
			// 请输入充值金额
			ToastUtil.showToast(context, getString(R.string.jrmf_w_input_recharge_amount));
			return;
		}

		// 验证充值金额格式
		if (amount.endsWith(".")) {
			amount = StringUtil.formatMoney(amount);
		}

		//验证充值是否超限
		if (StringUtil.formatMoneyDouble(amount) > rechageLimit){
			ToastUtil.showToast(this,getString(R.string.jrmf_w_recharge_beyond));
			return;
		}

		// 如果需要设置密码－显示设置密码弹框
		if (!UserInfoManager.getInstance().isHasPwd()) {
			showSetPwdDialog();
			return;
		}

		AccountModel accountModel = accountList.get(lastIndex);
		DialogDisplay.getInstance().dialogLoading(context, getString(R.string.jrmf_w_loading),this);
		WalletHttpManager.chargeReqCode(context, accountModel.bankCardNo, amount, new OkHttpModelCallBack<ChargeReqCode>() {

			@Override public void onSuccess(ChargeReqCode chargeReqCode) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				if (RechargeActivity.this.isFinishing()){
					return;
				}
				if (chargeReqCode == null) {
					ToastUtil.showToast(context, getString(R.string.jrmf_w_net_error_l));
					return;
				}

				if (chargeReqCode.isSuccess()) {
					// 请求成功－显示弹框
					ToastUtil.showToast(context, getString(R.string.jrmf_w_send_code_suc));
					showDialog(chargeReqCode.trade_id);
				} else {
					ToastUtil.showToast(context, chargeReqCode.respmsg);
				}
			}

			@Override public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				ToastUtil.showToast(context, getString(R.string.jrmf_w_net_error_l));
			}
		});
	}

	private void showSetPwdDialog() {
		setPwdDialog = DialogDisplay.getInstance().dialogLeftAndRight(LARDialogFragment.FROM_DEFAULT,
				getString(R.string.jrmf_w_not_pwd_tip), getString(R.string.jrmf_w_quit), getString(R.string.jrmf_w_confirm), this);
		setPwdDialog.showAllowingStateLoss(getFragmentManager(), this.getClass().getSimpleName());
	}

	@Override
	public void onLeft() {
		if (setPwdDialog != null){
			setPwdDialog.dismissAllowingStateLoss();
			setPwdDialog = null;
		}
	}

	@Override
	public void onRight() {
		if (setPwdDialog != null){
			setPwdDialog.dismissAllowingStateLoss();
			setPwdDialog = null;
		}
		PwdH5Activity.intent(context, 0, Constants.FROM_GET_DEPOSIT);
	}

	/**
	 * 显示输入密码弹框
	 */
	private void showDialog(final String tradeId) {
		if (dialogFragment == null) {
			dialogFragment = new InputPwdDialogFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("fromKey", 0);
			bundle.putString("jrmf_w_recharge", cet_money.getText().toString().trim());
			dialogFragment.setArguments(bundle);
			dialogFragment.setListener(new InputPwdDialogFragment.InputPwdListener() {

				@Override public void onFinish(final GridPasswordView passwordView) {
					// 验证码输入完成-请求网络验证
					KeyboardUtil.hideKeyboard(RechargeActivity.this);
					DialogDisplay.getInstance().dialogLoading(context,getString(R.string.jrmf_w_loading),RechargeActivity.this);
					WalletHttpManager.chargeVerifyCode(context, tradeId, passwordView.getPassWord(), new OkHttpModelCallBack<BaseModel>() {
						@Override public void onSuccess(BaseModel baseModel) {
							DialogDisplay.getInstance().dialogCloseLoading(context);
							passwordView.clearPassword();
							if (baseModel == null) {
								ToastUtil.showToast(context, getString(R.string.jrmf_w_recharge_fail));
								return;
							}
							if (baseModel.isSuccess()) {
								ToastUtil.showToast(context, getString(R.string.jrmf_w_recharge_suc));
								passwordView.clearPassword();
								dialogFragment.dismiss();
								//充值成功后,跳转回主页面
								btn_next.postDelayed(new Runnable() {
									@Override
									public void run() {
										finish();
									}
								},500);
							} else {
								ToastUtil.showToast(context, baseModel.respmsg);
							}
						}

						@Override public void onFail(String result) {
							DialogDisplay.getInstance().dialogCloseLoading(context);
							ToastUtil.showToast(context, getString(R.string.jrmf_w_recharge_fail));
						}
					});
				}

				@Override public void forgetPwd() {
					// 重新发送验证码
					dialogFragment.dismissAllowingStateLoss();
					reqVerifyCode();
				}
			});
		} else {
			Bundle bundle = new Bundle();
			bundle.putInt("fromKey", 0);
			bundle.putString("jrmf_w_recharge", cet_money.getText().toString().trim());
			dialogFragment.setArguments(bundle);
		}
		dialogFragment.showAllowingStateLoss(getFragmentManager());
	}

	/**
	 * 用户点击切换付款方式
	 */
	public void showButtomDialog() {
		if (dialog == null) {
			dialog = new Dialog(this, R.style.Jrmf_w_ActionSheetDialogStyle);
			// 填充对话框的布局
			buttomLayout = LayoutInflater.from(this).inflate(R.layout.jrmf_w_dialog_select_pay_type, null);
			ll_paytype_container = (LinearLayout) buttomLayout.findViewById(R.id.ll_paytype_container);
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
			windowparams.height = ScreenUtil.dp2px(this, 300);
			windowparams.width = ScreenUtil.getScreenWidth(this);
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
	 * 用户选择了支付方式 只能选择新卡支付和已有银行卡支付（也是京东支付）
	 */
	private void selectPayType(int payType, int index) {
		if (index >= 0){
			lastIndex = index;
		}
		// 弹框消失
		dialog.dismiss();
		PAY_TYPE = payType;
		if (PAY_TYPE == Constants.PAY_TYPE_NEWCARD) {
			// 新卡支付－跳转到添加银行卡页面
			AddCardFirstActivity.intent(this);
		} else {
			// 京东支付，根据索引找到卡信息
			AccountModel accountModel = accountList.get(index);
			tv_cardName.setText(accountModel.bankName);
			tv_cardType.setText(String.format(getString(R.string.jrmf_w_card_des), accountModel.bankCardNoDesc));
			tv_mount_tip.setText(String.format(getString(R.string.jrmf_w_card_recharge_limit), accountModel.payOrderlimit));
			if (StringUtil.isNotEmpty(accountModel.logo_url)) {
				ImageLoadUtil.getInstance().loadImage(iv_bankCardIcon, accountModel.logo_url);
			}
		}
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
			rl_pay_type.setOnClickListener(new OnClickListener() {

				@Override public void onClick(View v) {
					selectPayType(Constants.PAY_TYPE_JD, index);
				}
			});
			ll_paytype_container.addView(bankCradView, LayoutParams.MATCH_PARENT, ScreenUtil.dp2px(context, 48));
		}

		// 使用新卡支付
		RelativeLayout newCardView = (RelativeLayout) View.inflate(context, R.layout.jrmf_w_item_new_card, null);
		RelativeLayout rl_new_card = (RelativeLayout) newCardView.findViewById(R.id.rl_new_card);
		rl_new_card.setOnClickListener(new OnClickListener() {

			@Override public void onClick(View v) {
				selectPayType(Constants.PAY_TYPE_NEWCARD, -1);
			}
		});
		ll_paytype_container.addView(newCardView, LayoutParams.MATCH_PARENT, ScreenUtil.dp2px(context, 48));
		// 账户余额-不可选择
		RelativeLayout changeView = (RelativeLayout) View.inflate(context, R.layout.jrmf_w_item_select_change, null);
		ImageView iv_changeLogo = (ImageView) changeView.findViewById(R.id.iv_changeLogo);
		iv_changeLogo.getBackground().mutate().setAlpha(100);
		ll_paytype_container.addView(changeView, LayoutParams.MATCH_PARENT, ScreenUtil.dp2px(context, 48));
	}

	/**
	 * 限制输入金额两位小数，并且总位数八位
	 *
	 */
	private class InputMoney implements InputFilter {

		@Override public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			if (source.toString().equals(".") && dstart == 0 && dend == 0) {// 判断小数点是否在第一位
				cet_money.setText(0 + "" + source + dest);// 给小数点前面加0
				cet_money.setSelection(2);// 设置光标
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
