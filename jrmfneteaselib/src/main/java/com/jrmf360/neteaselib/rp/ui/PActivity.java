package com.jrmf360.neteaselib.rp.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.jrmf360.neteaselib.JrmfClient;
import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.fragment.LARDialogFragment;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.manager.CusActivityManager;
import com.jrmf360.neteaselib.base.model.BaseModel;
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
import com.jrmf360.neteaselib.rp.http.model.RedEnvelopeModel;
import com.jrmf360.neteaselib.rp.widget.LimitDialog;
import com.jrmf360.neteaselib.rp.widget.RpPayTypeCheckPopWindow;
import com.switfpass.pay.MainApplication;
import com.switfpass.pay.activity.PayPlugin;
import com.switfpass.pay.bean.RequestMsg;

import java.lang.ref.WeakReference;

/**
 * 发单人红包支付方式
 */
public class PActivity extends BaseActivity implements LARDialogFragment.InputPwdErrorListener {

	private boolean						hasPwd, hasBindCard;

	private TextView					tv_pay_title;

	private ImageView					iv_exit;									// 关闭按钮

	private TextView					tv_redenvelope_name, tv_redenvelope_amount;	// 红包名称,红包金额

	private LinearLayout				layout_paytype;								// 支付方式根节点

	private ImageView					iv_paytype_icon;							// 支付方式icon

	private TextView					tv_paytype_name;							// 支付方式名称

	private Button						btn_pay;

	// 自定义的弹出框类
	private RpPayTypeCheckPopWindow		menuWindow;

	private RedEnvelopeModel redEnvelopeModel;

	private int							envelopesType;								// 红包类型

	private int							payTypeTag;									// 支付方式

	private float						envelopesAmount;							// 单个红包金额

	private double						balanceNum;									// 账户余额

	private float						envelopesNum;								// 红包个数

	private GridPasswordView			gpv_pswd;

	private TextView					tv_pswd_tips, tv_forget_pswd;				// 请输入零钱支付密码，忘记支付密码

	private String						tradeId			= null;

	private MyCount						mc;

	private View						rootView;

	private static final int			SDK_PAY_FLAG	= 1;

	private static final int			SDK_AUTH_FLAG	= 2;

	private LARDialogFragment pwdErrorDialog;								// 密码输入错误弹框

	private int							lastCardIndex	= 0;


	@Override public int getLayoutId() {
		return R.layout.jrmf_rp_activity_p;
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

			/**
			 * 密码输入完成，请求网络去支付
			 *
			 * @param psw
			 */
			@Override public void onInputFinish(String psw) {
				exePay();
				KeyboardUtil.hideKeyboard(PActivity.this);
			}
		});
	}

	@Override protected void initData(Bundle bundle) {
		hasPwd = bundle.getBoolean(ConstantUtil.HAS_PWD, false);
		redEnvelopeModel = (RedEnvelopeModel) bundle.getSerializable("temp");
		tv_redenvelope_name.setText(bundle.getString(ConstantUtil.RED_PACKET_NAME));
		balanceNum = StringUtil.formatMoneyDouble(redEnvelopeModel.balance);
		envelopesType = bundle.getInt(ConstantUtil.ENVELOPES_TYPE, -1);
		envelopesNum = StringUtil.formatMoneyFloat(bundle.getString(ConstantUtil.NUMBER));
		envelopesAmount = StringUtil.formatMoneyFloat(bundle.getString(ConstantUtil.AMOUNT));
		if (redEnvelopeModel.myBankcards != null && redEnvelopeModel.myBankcards.size() > 0) {
			hasBindCard = true;
		}
		if (envelopesType == ConstantUtil.ENVELOPES_TYPE_SINGLE) {// 个人红包
			tv_redenvelope_amount.setText(StringUtil.formatMoney(envelopesAmount));
		} else if (envelopesType == ConstantUtil.ENVELOPES_TYPE_PIN) {// 群拼手气红包
			tv_redenvelope_amount.setText(StringUtil.formatMoney(envelopesAmount));
		} else if (envelopesType == ConstantUtil.ENVELOPES_TYPE_NORMAL) {// 群普通红包
			tv_redenvelope_amount.setText(StringUtil.formatMoney(envelopesAmount * envelopesNum));
		}
		setPayTypeView(getDefaultPayType(), 0);
	}

	@Override protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		LogUtil.i("onNewIntent");
		initData(intent.getExtras());
	}

	@Override public void onClick(int id) {
		if (id == R.id.iv_exit) {// 关闭
			KeyboardUtil.hideKeyboard(context);
			finish();
		} else if (id == R.id.layout_paytype) {
			// 选择支付方式
			if (!NoDoubleClickUtils.isDoubleClick()) {
				showPayTypeViewDialog();
			}
		} else if (id == R.id.btn_pay) {// 支付
			if (!NoDoubleClickUtils.isDoubleClick()) {
				if (payTypeTag == ConstantUtil.PAY_TYPE_OLDCARD) {
					// 如果是老卡支付－发送验证码－显示密码输入框
					setOldCardInputPwd();
				} else if (payTypeTag == ConstantUtil.PAY_TYPE_NEWCARD) {
					// 新卡支付-直接跳转到添加银行卡页面
					AcfActivity.intent(context);
					finish();
				} else {
					exePay();
				}
			}
		} else if (id == R.id.tv_forget_pswd) {
			// 忘记密码或者发送验证码
			if (payTypeTag == ConstantUtil.PAY_TYPE_CHANGE || payTypeTag == ConstantUtil.PAY_TYPE_NEWCARD) {
				// 忘记密码
				PwdH5Activity.intent(context, 2);
			} else if (payTypeTag == ConstantUtil.PAY_TYPE_OLDCARD) {
				// 发送验证码
				jdsendCode();
			}
		}
	}

	/**
	 * 设置老卡支付输入密码页面
	 */
	private void setOldCardInputPwd() {
		toggleInputPwd(true);
		// 把电话号码中间四位换成＊＊＊＊
		String mobileNo = redEnvelopeModel.myBankcards.get(lastCardIndex).mobileNo;
		mobileNo = StringUtil.phoneNumReplace(mobileNo);
		tv_pswd_tips.setText(String.format(getString(R.string.jrmf_rp_input_verify_code), mobileNo));
		tv_forget_pswd.setText(getString(R.string.jrmf_rp_send_code));
		tv_pay_title.setText(getString(R.string.jrmf_rp_input_verify_code_title));
		jdsendCode();
	}

	/**
	 * @param payType
	 * @param index
	 *            表示account列表里的索引
	 */
	private void setPayTypeView(int payType, int index) {
		gpv_pswd.setPasswordVisibility(false);
		rootView.setVisibility(View.VISIBLE);
		toggleInputPwd(false);
		btn_pay.setTag(null);
		Drawable drawable = null;
		String payname = "";

		// 新卡支付
		if (payType == ConstantUtil.PAY_TYPE_NEWCARD) {
			drawable = getResources().getDrawable(R.drawable.jrmf_rp_ic_card);
			payname = getString(R.string.jrmf_rp_add_card_pay);
			iv_paytype_icon.setImageDrawable(drawable);
			tv_paytype_name.setText(payname);
			return;
		}

		// 老卡支付
		if (payType == ConstantUtil.PAY_TYPE_OLDCARD) {
			// 用户选择了老卡支付
			gpv_pswd.setPasswordVisibility(true);
			tv_pay_title.setText(getString(R.string.jrmf_rp_please_pay));
			lastCardIndex = index;
			btn_pay.setTag(redEnvelopeModel.myBankcards.get(lastCardIndex).bankCardNo);
			payname = redEnvelopeModel.myBankcards.get(lastCardIndex).bankName + "(" + redEnvelopeModel.myBankcards.get(lastCardIndex).bankCardNoDesc + ")";
			tv_paytype_name.setText(payname);
			ImageLoadUtil.getInstance().loadImage(iv_paytype_icon, redEnvelopeModel.myBankcards.get(lastCardIndex).logo_url);
			return;
		}

		// 零钱支付
		if (payType == ConstantUtil.PAY_TYPE_CHANGE) {
			drawable = getResources().getDrawable(R.drawable.jrmf_rp_ic_charge);
			payname = String.format(getString(R.string.jrmf_rp_balance), StringUtil.formatMoney(balanceNum));
			tv_pay_title.setText(getString(R.string.jrmf_rp_please_pay));
		} else if (payType == ConstantUtil.PAY_TYPE_WECHAT) {
			// 微信支付
			drawable = getResources().getDrawable(R.drawable.jrmf_rp_ic_wx);
			payname = getString(R.string.jrmf_rp_wechat);
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
	 * 未绑卡，未设置密码： 1. 页面只提示选择支付方式，不谈出密码框 2. 零钱足够的话默认使用零钱支付 3. 零钱不够的话默认使用微信支付
	 */
	private int getDefaultPayType() {
		double payamountNum = StringUtil.formatMoneyDouble(tv_redenvelope_amount.getText().toString().trim());
		payTypeTag = ConstantUtil.PAY_TYPE_CHANGE;
		if (payamountNum <= balanceNum) {
			// 零钱足够
			payTypeTag = ConstantUtil.PAY_TYPE_CHANGE;
		} else {
			// 零钱不够-先判断是否绑卡
			if (hasBindCard) {
				// 绑卡
				payTypeTag = ConstantUtil.PAY_TYPE_OLDCARD;
			} else {
				// 没绑卡-判断是否支持支付宝
				if ("1".equals(redEnvelopeModel.isSupportAliPay)) {
					// 支持支付宝
					payTypeTag = ConstantUtil.PAY_TYPE_ALIPAY;
				} else if ("1".equals(redEnvelopeModel.isSupportWeChatPay)) {
					// 支持微信支付
					payTypeTag = ConstantUtil.PAY_TYPE_WECHAT;
				} else {
					// 不支持支付宝-添加新卡
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
		RpHttpManager.jdGetCode(context, false, userid, thirdToken, redEnvelopeModel.envelopeId, (String) btn_pay.getTag(), tradeId, new OkHttpModelCallBack<CodeModel>() {

			@Override public void onSuccess(CodeModel codeModel) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				if (codeModel.isSuccess()) {
					tradeId = codeModel.trade_id;
					Toast.makeText(PActivity.this, getString(R.string.jrmf_rp_verify_code_suss), Toast.LENGTH_LONG).show();
					mc = new MyCount(60000, 1000);
					mc.start();

					// 弹出软键盘
					KeyboardUtil.popInputMethod(gpv_pswd.getEditText());
				} else {
					ToastUtil.showToast(context, codeModel.respmsg);
				}
			}

			@Override public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				ToastUtil.showToast(context, getString(R.string.jrmf_rp_network_error));
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
				tv_forget_pswd.setText(String.format(getString(R.string.jrmf_rp_count_down), millisUntilFinished / 1000));
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
		if (payTypeTag == ConstantUtil.PAY_TYPE_CHANGE) { // 零钱支付
			if (!hasPwd) {// 零钱支付没有密码需要先去设置密码
				PwdH5Activity.intent(context, 0);
				finish();
			} else {
				payMFKJ();
			}
		} else {
			payMFKJ();
		}
	}

	/**
	 * 零钱支付，银行卡支付
	 */
	private void payMFKJ() {
		DialogDisplay.getInstance().dialogLoading(context, getString(R.string.jrmf_rp_loading), this);
		int paytype = payTypeTag;
		String envelopeId = redEnvelopeModel.envelopeId;
		String bankCardNo = (String) btn_pay.getTag();
		RpHttpManager.exePay(context, userid, thirdToken, paytype, envelopeId, bankCardNo, gpv_pswd.getPassWord(), tradeId, new OkHttpModelCallBack<PayResModel>() {

			@Override public void onSuccess(PayResModel payResModel) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				gpv_pswd.clearPassword();
				if (payResModel.isSuccess()) {
					if (payTypeTag == ConstantUtil.PAY_TYPE_ALIPAY) {// 唤起支付宝支付
						alipay(payResModel.paramStr);
					} else if (payTypeTag == ConstantUtil.PAY_TYPE_WECHAT) {// 微信支付
						RequestMsg msg = new RequestMsg();
						msg.setTokenId(payResModel.token_id);
						msg.setTradeType(MainApplication.WX_APP_TYPE);
						msg.setAppId(JrmfClient.getWxAppId());
						PayPlugin.unifiedAppPay(PActivity.this, msg);
					} else if (payTypeTag == ConstantUtil.PAY_TYPE_CHANGE) {// 零钱支付
						PwdH5Activity.intentSendRp(context, 5, payResModel.url);
					} else {
						finishWithResult();
					}
				} else if ("R0015".equals(payResModel.respmsg)) {
					// 您尚未绑定银行卡，发红包的累计限额为100元，请绑定银行卡后再发红包
					LimitDialog dialog = new LimitDialog(context, payResModel.limitMoney);
					dialog.show();
				}else if ("ER025".equals(payResModel.respstat)){//三方发红包异常
					showLeftAndRightDialog(getString(R.string.jrmf_rp_send_exception));
				}else {
					ToastUtil.showToast(context, payResModel.respmsg);
				}
			}

			@Override public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				ToastUtil.showToast(context, getString(R.string.jrmf_rp_network_error));
			}
		});
	}

	/**
	 * 显示输入密码错误弹框
	 *
	 * @param title
	 */
	private void showLeftAndRightDialog(String title) {
		rootView.setVisibility(View.INVISIBLE);
		pwdErrorDialog = DialogDisplay.getInstance().dialogLeftAndRight(LARDialogFragment.FROM_DEFAULT,
				title,getString(R.string.jrmf_rp_fail_not_send),getString(R.string.jrmf_rp_fail_send), this);
		pwdErrorDialog.setCancelable(false);
		pwdErrorDialog.showAllowingStateLoss(getFragmentManager(), this.getClass().getSimpleName());

	}

	@Override public void onLeft() {
		pwdErrorDialog.dismissAllowingStateLoss();
		rootView.setVisibility(View.VISIBLE);
		KeyboardUtil.popInputMethod(gpv_pswd.getEditText());
	}

	@Override public void onRight() {
		pwdErrorDialog.dismissAllowingStateLoss();
		refreshRedStatus();
	}

	/**
	 * 刷新红包状态
	 */
	private void refreshRedStatus(){
		DialogDisplay.getInstance().dialogLoading(context,getString(R.string.jrmf_rp_loading),this);
		RpHttpManager.refreshRedStatus(context, userid, thirdToken, redEnvelopeModel.envelopeId, new OkHttpModelCallBack<BaseModel>() {
			@Override
			public void onSuccess(BaseModel baseModel) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				if (baseModel.isSuccess()){
					finishWithResult();
				}else if ("ER025".equals(baseModel.respstat)){//三方发红包异常
					showLeftAndRightDialog(getString(R.string.jrmf_rp_send_exception));
				}else{
					ToastUtil.showNoWaitToast(context,baseModel.respmsg);
					finish();
				}
			}

			@Override
			public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				ToastUtil.showNoWaitToast(context,getString(R.string.jrmf_rp_net_error_l));
			}
		});
	}

	// 打开选择支付方式的页面
	private void showPayTypeViewDialog() {
		KeyboardUtil.hideKeyboard(this);
		createMenuWindow();
		// 显示窗口 设置layout在PopupWindow中显示的位置
		rootView.setVisibility(View.INVISIBLE);
		menuWindow.showAtLocation(findViewById(R.id.rootLayout), Gravity.CENTER, 0, 0);
	}

	private void createMenuWindow() {
		boolean flag = false;// 零钱是否足够发送此次红包
		if (envelopesType == ConstantUtil.ENVELOPES_TYPE_SINGLE) {
			flag = balanceNum >= envelopesAmount;
		} else if (envelopesType == ConstantUtil.ENVELOPES_TYPE_PIN) {
			flag = balanceNum >= envelopesAmount;
		} else if (envelopesType == ConstantUtil.ENVELOPES_TYPE_NORMAL) {
			flag = balanceNum >= envelopesAmount * envelopesNum;
		}
		if (menuWindow == null) {
			menuWindow = new RpPayTypeCheckPopWindow(this, redEnvelopeModel, redEnvelopeModel.balance, flag);
			menuWindow.setOnClickListener(new RpPayTypeCheckPopWindow.OnClickListener() {

				@Override public void onClick(int paytype, int tag) {
					payTypeTag = paytype;
					if (paytype == ConstantUtil.PAY_TYPE_NEWCARD) {
						// 新卡支付-直接跳转到添加银行卡页面
						AcfActivity.intent(context);
						finish();
					} else {
						setPayTypeView(paytype, tag);
					}
				}

				@Override public void onDismisss() {
					rootView.setVisibility(View.VISIBLE);
				}
			});
		}
	}

	@Override public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if (menuWindow != null && menuWindow.isShowing()) {
				menuWindow.dismiss();
			}
		} else {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	// -------------------------------------------支付宝------------------------------------------------------------------------------

	private Handler mHandler = new MyHandler(this);

	private class MyHandler extends Handler {

		private WeakReference<Context> reference;

		public MyHandler(Context context) {
			reference = new WeakReference<Context>(context);
		}

		@Override public void handleMessage(Message msg) {
			switch (msg.what) {
				case SDK_PAY_FLAG:
					PActivity activity = (PActivity) reference.get();
					PayResult payResult = new PayResult((String) msg.obj);
					/**
					 * 同步返回的结果必须放置到服务端进行验证（
					 * 验证的规则请看https://doc.open.alipay.com/doc2/detail.htm?
					 * spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&docType=1)
					 * 建议商户依赖异步通知
					 */
					// String resultInfo = payResult.getResult();// 同步返回需要验证的信息
					String resultStatus = payResult.getResultStatus();
					// 判断resultStatus
					// 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
					if (TextUtils.equals(resultStatus, "9000")) {// 支付成功
						if (activity != null) {
							finishWithResult();
						}
					} else {
						// 判断resultStatus
						// 为非"9000"则代表可能支付失败
						// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
						if (TextUtils.equals(resultStatus, "8000")) {
							if (activity != null) {
								ToastUtil.showToast(activity, activity.getString(R.string.jrmf_rp_pay_waiting));
							}
						} else {
							// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
							Log.i("alipay", resultStatus);
							if (activity != null) {
								ToastUtil.showToast(activity, activity.getString(R.string.jrmf_rp_pay_failure));
							}
						}
					}
					break;
			}
		}
	}

	/**
	 * call alipay sdk pay. 调用SDK支付
	 */
	public void alipay(final String paramStr) {

		Runnable payRunnable = new Runnable() {

			@Override public void run() {
				Message msg = new Message();
				// 构造PayTask 对象
				PayTask alipay = new PayTask(PActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(paramStr, true);
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};
		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	public void finishWithResult() {
		SgRpActivity groupEnvelopesActivity = CusActivityManager.getInstance().findActivity(SgRpActivity.class);
		if (groupEnvelopesActivity != null) {
			groupEnvelopesActivity.finishWithResult(redEnvelopeModel.envelopeId);
		}
		SsRpActivity singleEnvelopesActivity = CusActivityManager.getInstance().findActivity(SsRpActivity.class);
		if (singleEnvelopesActivity != null) {
			singleEnvelopesActivity.finishWithResult(redEnvelopeModel.envelopeId);
		}
		finish();
	}
}
