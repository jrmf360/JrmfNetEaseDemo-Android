package com.jrmf360.neteaselib.rp.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.auth.AuthActivity;
import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.fragment.LARDialogFragment;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.utils.ImageLoadUtil;
import com.jrmf360.neteaselib.base.utils.KeyboardUtil;
import com.jrmf360.neteaselib.base.utils.LogUtil;
import com.jrmf360.neteaselib.base.utils.NoDoubleClickUtils;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.utils.ToastUtil;
import com.jrmf360.neteaselib.base.view.passwordview.GridPasswordView;
import com.jrmf360.neteaselib.rp.alipay.PayResult;
import com.jrmf360.neteaselib.rp.constants.ConstantUtil;
import com.jrmf360.neteaselib.rp.http.RpHttpManager;
import com.jrmf360.neteaselib.rp.http.model.CodeModel;
import com.jrmf360.neteaselib.rp.http.model.PayResModel;
import com.jrmf360.neteaselib.rp.http.model.TransModel;
import com.jrmf360.neteaselib.rp.widget.LimitDialog;
import com.jrmf360.neteaselib.rp.widget.TransPayTypeCheckPopWindow;

/**
 * 转账支付方式
 */
public class TransPayActivity extends TransBaseActivity implements LARDialogFragment.InputPwdErrorListener {

	private boolean						HAS_PWD, hasBindCard;

	private TextView					tv_pay_title;

	private ImageView					iv_exit;									// 关闭按钮

	private TextView					tv_redenvelope_name, tv_redenvelope_amount;	// 红包名称,红包金额

	private LinearLayout				layout_paytype;								// 支付方式根节点

	private ImageView					iv_paytype_icon;							// 支付方式icon

	private TextView					tv_paytype_name;							// 支付方式名称

	private Button						btn_pay;

	// 自定义的弹出框类
	private TransPayTypeCheckPopWindow menuWindow;

	private int							payTypeTag;									// 支付方式

	private double						balanceNum;									// 账户余额

	private GridPasswordView gpv_pswd;

	private TextView					tv_pswd_tips, tv_forget_pswd;				// 请输入零钱支付密码，忘记支付密码

	private LARDialogFragment pwdErrorDialog;								// 密码输入错误弹框

	private String						tradeId;

	private MyCount						mc;

	private View						rootView;

	private int							lastCardIndex		= 0;

	private TransModel transModel;

	private String						mAmount;									// 转账金额

	private String						recUserName;								// 转账接受者名字

	private boolean						firstSelctNewCard	= true;

	/**
	 * @param fromActivity
	 * @param targetId
	 * @param transModel
	 */
	public static void intent(Activity fromActivity, String targetId, String recUserName, TransModel transModel, String amount) {
		Intent intent = new Intent(fromActivity, TransPayActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("TRANS_MODEL", transModel);
		bundle.putString(ConstantUtil.R_TargetId, targetId);
		bundle.putString(ConstantUtil.JRMF_REC_USER_NAME, recUserName);
		bundle.putString("AMOUNT", amount);
		intent.putExtras(bundle);
		fromActivity.startActivity(intent);
	}

	@Override public int getLayoutId() {
		return R.layout.jrmf_rp_activity_trans_pay;
	}

	@Override public void initView() {
		rootView = findViewById(android.R.id.content);
		iv_exit = (ImageView) findViewById(R.id.iv_exit);
		tv_redenvelope_name = (TextView) findViewById(R.id.tv_redenvelope_name);
		tv_redenvelope_amount = (TextView) findViewById(R.id.tv_redenvelope_amount);
		layout_paytype = (LinearLayout) findViewById(R.id.layout_paytype);
		iv_paytype_icon = (ImageView) findViewById(R.id.iv_paytype_icon);
		tv_paytype_name = (TextView) findViewById(R.id.tv_paytype_name);
		btn_pay = (Button) findViewById(R.id.btn_pay);
		gpv_pswd = (GridPasswordView) findViewById(R.id.gpv_pswd);
		tv_pswd_tips = (TextView) findViewById(R.id.tv_pswd_tips);
		tv_forget_pswd = (TextView) findViewById(R.id.tv_forget_pswd);
		tv_pay_title = (TextView) findViewById(R.id.tv_pay_title);
	}

	@Override public void initListener() {
		iv_exit.setOnClickListener(this);
		layout_paytype.setOnClickListener(this);
		btn_pay.setOnClickListener(this);
		tv_forget_pswd.setOnClickListener(this);
		gpv_pswd.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {

			@Override public void onTextChanged(String psw) {}

			@Override public void onInputFinish(String psw) {
				// 密码输入完成去请求网络
				exePay();
				KeyboardUtil.hideKeyboard(TransPayActivity.this);
			}
		});
	}

	@Override protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		LogUtil.i("onNewIntent");
		initData(intent.getExtras());
	}

	@Override protected void initData(Bundle bundle) {
		if (bundle != null) {
			transModel = (TransModel) bundle.getSerializable("TRANS_MODEL");
			HAS_PWD = ("1".equals(transModel.hasPwd));
			balanceNum = StringUtil.formatMoneyDouble(transModel.balance);
			if (transModel.myBankcards != null && transModel.myBankcards.size() > 0) {
				hasBindCard = true;
			}
			mAmount = bundle.getString("AMOUNT");
			tv_redenvelope_amount.setText(StringUtil.formatMoney(mAmount));

			// 接受者名字
			recUserName = bundle.getString(ConstantUtil.JRMF_REC_USER_NAME);
			tv_redenvelope_name.setText(String.format(getString(R.string.jrmf_rp_trans_to), StringUtil.getFixUserName(recUserName)));
		}
		setPayTypeView(getDefaultPayType(), 0, false);
	}

	@Override public void onClick(int id) {
		if (id == R.id.iv_exit) {
			// 关闭
			KeyboardUtil.hideKeyboard(context);
			finish();
		} else if (id == R.id.layout_paytype) {
			// 选择支付方式
			showPayTypeViewDialog();
		} else if (id == R.id.btn_pay) {
			if (!NoDoubleClickUtils.isDoubleClick()) {
				if (payTypeTag == ConstantUtil.PAY_TYPE_OLDCARD) {
					// 如果是老卡支付－发送验证码－显示密码输入框
					setOldCardInputPwd();
				} else if (payTypeTag == ConstantUtil.PAY_TYPE_NEWCARD) {
					// 第一次选择新卡支付
					showNewCard();
				} else {
					exePay();
				}
			}
		} else if (id == R.id.tv_forget_pswd) {
			if (payTypeTag == ConstantUtil.PAY_TYPE_CHANGE || payTypeTag == ConstantUtil.PAY_TYPE_NEWCARD) {
				if (payTypeTag == ConstantUtil.PAY_TYPE_CHANGE || payTypeTag == ConstantUtil.PAY_TYPE_NEWCARD) {
					if (hasBindCard) {
						// 绑卡
						// CheckAuthActivity.intent(context);
					} else {
						// 没绑卡-跳转到根据用户信息找回支付密码页面
						// CheckUserInfoActivity.intent(this);
					}
				}
			} else if (payTypeTag == ConstantUtil.PAY_TYPE_OLDCARD) {// 这里其实已经转变为京东支付了
				// 发送验证码
				jdsendCode();
			}
		}
	}

	/**
	 * 是否显示密码输入框
	 *
	 * @param isShow
	 *            true 显示 false 不显示
	 */
	private void toggleInputPwd(boolean isShow) {
		if (isShow) {
			btn_pay.setVisibility(View.GONE);
			gpv_pswd.setFocusable(true);
			gpv_pswd.setEnabled(true);
			gpv_pswd.setVisibility(View.VISIBLE);
			tv_pswd_tips.setVisibility(View.VISIBLE);
			tv_forget_pswd.setVisibility(View.VISIBLE);
			KeyboardUtil.popInputMethod(gpv_pswd.getEditText());
		} else {
			btn_pay.setVisibility(View.VISIBLE);
			gpv_pswd.setVisibility(View.GONE);
			gpv_pswd.setFocusable(false);
			gpv_pswd.setEnabled(false);
			tv_pswd_tips.setVisibility(View.GONE);
			tv_forget_pswd.setVisibility(View.GONE);
			KeyboardUtil.hideKeyboard(this);
		}
	}

	/**
	 * 设置老卡支付输入密码页面
	 */
	private void setOldCardInputPwd() {
		toggleInputPwd(true);
		// 把电话号码中间四位换成＊＊＊＊
		String mobileNo = transModel.myBankcards.get(lastCardIndex).mobileNo;
		mobileNo = mobileNo.substring(0, 3) + "****" + mobileNo.substring(7, 11);
		// tv_pswd_tips.setText(getString(R.string.jrmf_rp_input_verify_code).replace("{phonenum}",
		// mobileNo));
		tv_pswd_tips.setText(String.format(getString(R.string.jrmf_rp_input_verify_code), mobileNo));
		tv_forget_pswd.setText(getString(R.string.jrmf_rp_send_code));
		jdsendCode();
		tv_pay_title.setText(getString(R.string.jrmf_rp_input_verify_code_title));
	}

	/**
	 * @param payType
	 * @param index
	 *            表示account列表里的索引
	 */
	private void setPayTypeView(int payType, int index, boolean isClick) {
		gpv_pswd.setPasswordVisibility(false);
		rootView.setVisibility(View.VISIBLE);
		btn_pay.setTag(null);
		Drawable drawable = null;
		String payname = "";

		// 新卡支付
		if (payType == ConstantUtil.PAY_TYPE_NEWCARD) {
			if (firstSelctNewCard && !isClick) {
				// 第一次设置为新卡，不跳转
				toggleInputPwd(false);
				drawable = getResources().getDrawable(R.drawable.jrmf_rp_ic_card);
				payname = getString(R.string.jrmf_rp_add_card_pay);
				iv_paytype_icon.setImageDrawable(drawable);
				tv_paytype_name.setText(payname);
			} else {
				showNewCard();
			}
			firstSelctNewCard = false;
			return;
		}

		// 老卡支付
		if (payType == ConstantUtil.PAY_TYPE_OLDCARD) {
			// 用户选择了老卡支付
			gpv_pswd.setPasswordVisibility(true);
			tv_pay_title.setText(getString(R.string.jrmf_rp_please_pay));
			toggleInputPwd(false);
			lastCardIndex = index;
			btn_pay.setTag(transModel.myBankcards.get(lastCardIndex).bankCardNo);
			payname = transModel.myBankcards.get(lastCardIndex).bankName + "(" + transModel.myBankcards.get(lastCardIndex).bankCardNoDesc + ")";
			tv_paytype_name.setText(payname);
			ImageLoadUtil.getInstance().loadImage(iv_paytype_icon, transModel.myBankcards.get(lastCardIndex).logo_url);
			return;
		}

		if (HAS_PWD && payType != ConstantUtil.PAY_TYPE_ALIPAY && payType != ConstantUtil.PAY_TYPE_WECHAT) {
			toggleInputPwd(true);
		} else {
			toggleInputPwd(false);
		}

		// 零钱支付
		if (payType == ConstantUtil.PAY_TYPE_CHANGE) {
			drawable = getResources().getDrawable(R.drawable.jrmf_rp_ic_charge);
			payname = "零钱（余额 ¥  " + StringUtil.formatMoney(balanceNum) + "）";
			tv_pswd_tips.setText(getString(R.string.jrmf_rp_please_input_pwd));
			tv_forget_pswd.setClickable(true);
			tv_forget_pswd.setText(getString(R.string.jrmf_rp_forget_pwd));
			if (HAS_PWD) {
				tv_pay_title.setText(getString(R.string.jrmf_rp_please_input_pwd));
			} else {
				tv_pay_title.setText(getString(R.string.jrmf_rp_please_pay));
			}
		} else if (payType == ConstantUtil.PAY_TYPE_WECHAT) {
			// 微信支付
			drawable = getResources().getDrawable(R.drawable.jrmf_rp_ic_wx);
			payname = "微信支付";
			tv_pay_title.setText(getString(R.string.jrmf_rp_please_pay));
		} else if (payType == ConstantUtil.PAY_TYPE_ALIPAY) {
			// 支付宝
			drawable = getResources().getDrawable(R.drawable.jrmf_rp_ic_alipay);
			payname = getString(R.string.jrmf_rp_alipay);
			tv_pay_title.setText(getString(R.string.jrmf_rp_please_pay));
		}
		iv_paytype_icon.setImageDrawable(drawable);
		tv_paytype_name.setText(payname);
	}

	/**
	 * 用户选择添加银行卡，页面显示
	 */
	private void showNewCard() {
		Drawable drawable;
		String payname;// 添加新卡－直接跳转到添加银行卡页面
		if (transModel.isAuthentication == 1) {
			// 用户实名认证了－跳转到绑卡第二步
			// startActivity(new Intent(context,
			// AddCardActivity.class).putExtra("realname",
			// transModel.realName));
			finish();
		} else {
			// 用户没有实名认证
			if (transModel.isComplete == 1) {
				// 有用户信息
				if (transModel.hasPwd == 1) {
					// 有密码－先验证密码
					toggleInputPwd(true);
					drawable = getResources().getDrawable(R.drawable.jrmf_rp_ic_card);
					payname = getString(R.string.jrmf_rp_add_card_pay);
					tv_pswd_tips.setText(getString(R.string.jrmf_rp_please_input_pwd));
					tv_forget_pswd.setClickable(true);
					tv_forget_pswd.setText(getString(R.string.jrmf_rp_forget_pwd));
					tv_pay_title.setText(getString(R.string.jrmf_rp_please_input_pwd));

					// 设置支付图标和名称
					iv_paytype_icon.setImageDrawable(drawable);
					tv_paytype_name.setText(payname);
				} else {
					// 无密码-输入卡号
					// startActivity(new Intent(context,
					// AddCardActivity.class).putExtra("realname",
					// transModel.realName));
					finish();
				}
			} else {
				// 无用户信息－跳转到实名认证页面
				startActivity(new Intent(context, AuthActivity.class));
				finish();
			}
		}
	}

	/**
	 * 未绑卡，未设置密码： 1. 页面只提示选择支付方式，不谈出密码框 2. 零钱足够的话默认使用零钱支付 3. 零钱不够的话默认使用微信支付
	 */
	private int getDefaultPayType() {
		double payamountNum = StringUtil.formatMoneyDouble(tv_redenvelope_amount.getText().toString().trim());
		payTypeTag = ConstantUtil.PAY_TYPE_CHANGE;
		if (payamountNum <= balanceNum) {
			payTypeTag = ConstantUtil.PAY_TYPE_CHANGE;
		} else {
			// 零钱不够-判断是否绑卡
			if (hasBindCard) {
				// 绑定了银行卡
				payTypeTag = ConstantUtil.PAY_TYPE_OLDCARD;
			} else {
				// 没有绑定银行卡-判断是否支持支付宝
				if ("1".equals(transModel.isSupportAliPay)) {
					// 支持支付宝
					payTypeTag = ConstantUtil.PAY_TYPE_ALIPAY;
				} else if ("1".equals(transModel.isSupportWeChatPay)) {
					// 支持微信支付
					payTypeTag = ConstantUtil.PAY_TYPE_WECHAT;
				} else {
					payTypeTag = ConstantUtil.PAY_TYPE_NEWCARD;
				}
			}
		}
		return payTypeTag;
	}

	/**
	 * 京东支付获取验证码 当选择已绑定的银行卡支付时，其实就是京东支付，这时的支付方式是5，再ReqApi中已经写死
	 */
	private void jdsendCode() {
		KeyboardUtil.hideKeyboard(this);
		DialogDisplay.getInstance().dialogLoading(context, getString(R.string.jrmf_rp_loading));
		RpHttpManager.jdGetCode(context, true, userid, thirdToken, transModel.transferOrderNo, (String) btn_pay.getTag(), tradeId, new OkHttpModelCallBack<CodeModel>() {

			@Override public void onSuccess(CodeModel codeModel) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				if (codeModel.isSuccess()) {
					tradeId = codeModel.trade_id;
					Toast.makeText(context, getString(R.string.jrmf_rp_verify_code_suss), Toast.LENGTH_LONG).show();
					mc = new MyCount(90000, 1000);
					mc.start();

					// 弹出软键盘
					KeyboardUtil.popInputMethod(gpv_pswd.getEditText());
				} else {
					ToastUtil.showToast(context, codeModel.respmsg);
				}
			}

			@Override public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				ToastUtil.showToast(context, getString(R.string.jrmf_rp_net_error_l));
			}
		});
	}

	/**
	 * 计时器
	 */
	public class MyCount extends CountDownTimer {

		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override public void onTick(long millisUntilFinished) {
			if (payTypeTag == ConstantUtil.PAY_TYPE_OLDCARD) {
				tv_forget_pswd.setText((new StringBuilder().append(millisUntilFinished / 1000).append("秒后重试")));
				tv_forget_pswd.setClickable(false);
			}
		}

		@Override public void onFinish() {
			if (payTypeTag == ConstantUtil.PAY_TYPE_OLDCARD) {
				tv_forget_pswd.setText(getString(R.string.jrmf_rp_re_send_code));
				tv_forget_pswd.setClickable(true);
			}
		}
	}

	private void exePay() {
		if (payTypeTag == ConstantUtil.PAY_TYPE_CHANGE) {
			// 零钱支付
			if (HAS_PWD) {
				// 使用余额支付-跳转到H5页面验证密码，必须或得到H5的返回结果再关闭该页面
//				 PwdH5Activity.intentForResult(context, 5,redEnvelopeModel.envelopeId,REQ_CODE);
			} else {
				// 零钱支付没有密码需要设置先去设置密码
				PwdH5Activity.intent(context, 0);
				finish();
			}
		} else {
			payMFKJ();
		}
	}

	/**
	 * 零钱支付，银行卡支付，
	 */
	private void payMFKJ() {

		DialogDisplay.getInstance().dialogLoading(context, getString(R.string.jrmf_rp_loading), this);
		int paytype = payTypeTag;
		String bankCardNo = (String) btn_pay.getTag();
		RpHttpManager.transPay(context, userid, thirdToken, paytype, transModel.transferOrderNo, bankCardNo, gpv_pswd.getPassWord(), tradeId, new OkHttpModelCallBack<PayResModel>() {

			@Override public void onSuccess(PayResModel payResModel) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				gpv_pswd.clearPassword();
				if (payResModel.isSuccess()) {
					if (payTypeTag == ConstantUtil.PAY_TYPE_ALIPAY) {
						alipay(payResModel.paramStr);// 唤起支付宝支付
					} else {
						enterFinishActivity();
					}
				} else if (payResModel.respstat != null && payResModel.respstat.equals("R0012")) {
					showPwdErrorDialog(false, payResModel.respmsg);
				} else if (payResModel.respstat != null && payResModel.respstat.equals("TP010")) {
					// 密码输入次数剩余0次
					showPwdErrorDialog(true, payResModel.respmsg);
				} else if ("ER011".equals(payResModel.respstat)) {
					// 同一个红包id,多次支付
				} else if (payResModel.respstat != null && payResModel.respstat.equals("R0015")) {
					// 您尚未绑定银行卡，发红包的累计限额为100元，请绑定银行卡后再发红包
					LimitDialog dialog = new LimitDialog(context, payResModel.limitMoney);
					dialog.show();
				} else {
					ToastUtil.showToast(context, payResModel.respmsg);
				}
			}

			@Override public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				ToastUtil.showToast(context, getString(R.string.jrmf_rp_net_error_l));
			}
		});
	}

	/**
	 * 显示输入密码错误弹框
	 *
	 * @param title
	 */
	private void showPwdErrorDialog(boolean islast, String title) {
		rootView.setVisibility(View.INVISIBLE);
		pwdErrorDialog = DialogDisplay.getInstance().dialogLeftAndRight(LARDialogFragment.FROM_DEFAULT, title,
				islast ? getString(R.string.jrmf_rp_confirm) : getString(R.string.jrmf_rp_retry), "重置密码", this);
		pwdErrorDialog.setCancelable(false);
		pwdErrorDialog.showAllowingStateLoss(getFragmentManager(), getClass().getSimpleName());
	}

	@Override public void onLeft() {
		pwdErrorDialog.dismiss();
		KeyboardUtil.popInputMethod(gpv_pswd.getEditText());
		rootView.setVisibility(View.VISIBLE);
		KeyboardUtil.popInputMethod(gpv_pswd.getEditText());
	}

	@Override public void onRight() {
		PwdH5Activity.intent(context, 2);
		pwdErrorDialog.dismiss();
		finish();
	}

	// 打开选择支付方式的页面
	private void showPayTypeViewDialog() {

		KeyboardUtil.hideKeyboard(this);
		// 实例化SelectPicPopupWindow
		createMenuWindow();
		menuWindow.setOnClickListener(new TransPayTypeCheckPopWindow.OnClickListener() {

			@Override public void onClick(int paytype, int tag) {
				payTypeTag = paytype;
				setPayTypeView(paytype, tag, true);
			}

			@Override public void onDismisss() {
				rootView.setVisibility(View.VISIBLE);
			}
		});
	}

	/**
	 * 生成支付方式的选项
	 */
	private void createMenuWindow() {
		boolean flag = StringUtil.formatMoneyDouble(transModel.balance) >= StringUtil.formatMoneyDouble(tv_redenvelope_amount.getText().toString().trim());
		if (menuWindow == null) {
			menuWindow = new TransPayTypeCheckPopWindow(TransPayActivity.this, transModel, transModel.balance, flag);
		}
		// 显示窗口
		rootView.setVisibility(View.INVISIBLE);
		menuWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0); // 设置layout在PopupWindow中显示的位置
	}

	// -------------------------------------------支付宝----------------------------------------------------------------------------------------------------------------
	private static final int						SDK_PAY_FLAG	= 1;

	@SuppressLint("HandlerLeak") private Handler	mHandler		= new Handler() {

																		@SuppressWarnings("unused") public void handleMessage(Message msg) {
																			switch (msg.what) {
																				case SDK_PAY_FLAG: {
																					PayResult payResult = new PayResult((String) msg.obj);
																					String resultInfo = payResult.getResult();																			// 同步返回需要验证的信息

																					String resultStatus = payResult.getResultStatus();
																					// 判断resultStatus
																					// 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
																					if (TextUtils.equals(resultStatus, "9000")) {																		// 支付成功
																						enterFinishActivity();
																					} else {
																						// 判断resultStatus
																						// 为非"9000"则代表可能支付失败
																						// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
																						if (TextUtils.equals(resultStatus, "8000")) {
																							ToastUtil.showToast(TransPayActivity.this, getString(R.string.jrmf_rp_pay_waiting));
																						} else if ("6001".equals(resultStatus)) {
																							ToastUtil.showToast(context, getString(R.string.jrmf_rp_quit_pay));
																						} else {
																							// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
																							Toast.makeText(TransPayActivity.this, getString(R.string.jrmf_rp_pay_failure), Toast.LENGTH_SHORT).show();
																						}
																					}
																					break;
																				}
																				default:
																					break;
																			}
																		}
																	};

	/**
	 * call alipay sdk pay. 调用SDK支付
	 */
	public void alipay(final String paramStr) {

		Runnable payRunnable = new Runnable() {

			@Override public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(TransPayActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(paramStr, true);
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	private void enterFinishActivity() {
		TransSuccActivity.intent(context, recUserName, mAmount);
		finish();
	}

	public void finishWithResult() {
		setResult(RESULT_OK);
		finish();
	}

	@Override public void onBackPressed() {
		if (menuWindow != null && menuWindow.isShowing()) {
			menuWindow.dismiss();
		}
		super.onBackPressed();
	}
}
