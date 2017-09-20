package com.jrmf360.neteaselib.rp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.fragment.LARDialogFragment;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.utils.SPManager;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.rp.constants.ConstantUtil;
import com.jrmf360.neteaselib.rp.http.RpHttpManager;
import com.jrmf360.neteaselib.rp.http.model.InitRpInfo;
import com.jrmf360.neteaselib.rp.http.model.RedEnvelopeModel;

/**
 * @创建人 honglin
 * @创建时间 2017/8/22 上午11:53
 * @类描述 一句话描述 你的UI
 */
public abstract class BaseSendActivity extends BaseActivity implements LARDialogFragment.InputPwdErrorListener {

	private LARDialogFragment bindCardTipDialog;								// 绑卡弹框

	protected String	envelopeMessage	= "";

	protected String	envelopeName	= "";

	protected String	summary			= "";

	protected EditText	et_peak_message;

	protected String	maxLimitMoney	= "200";	// 红包总额限制

	protected int		maxCount		= 100;		// 最大的红包数量,默认100

	protected EditText	et_amount;					// 输入金额

	protected final int			DIALOG_SET_PWD	= 10;			// 设置密码弹框提示

	protected final int			DIALOG_AUTH		= 11;			// 用户认证弹框提示

	private int DIALOG_TYPE;

	/**
	 * 从网络请求红包信息
	 */
	protected void getRpInfo(String username,String usericon) {
		RpHttpManager.getRpInfo(context, userid, thirdToken, username, usericon, new OkHttpModelCallBack<InitRpInfo>() {

			@Override public void onSuccess(InitRpInfo rpInfo) {
				if (context.isFinishing()) {
					return;
				}
				if (rpInfo != null && rpInfo.isSuccess()) {
					if (StringUtil.isNotEmptyAndNull(rpInfo.maxLimitMoney)) {
						maxLimitMoney = rpInfo.maxLimitMoney;
					}

					if (rpInfo.maxCount > 0) {
						maxCount = rpInfo.maxCount;
					}

					if (StringUtil.isNotEmptyAndNull(rpInfo.summary)) {
						summary = rpInfo.summary;
					}
					envelopeName = SPManager.getInstance().getString(context, "partner_name", "");
					if (StringUtil.isEmpty(envelopeName) && StringUtil.isNotEmptyAndNull(rpInfo.envelopeName)) {
						SPManager.getInstance().putString(context,"partner_name",rpInfo.envelopeName);//红包详情设置标题用的这个保存值
						envelopeName = rpInfo.envelopeName;
					}
					et_peak_message.setHint(summary);
				}
			}
			@Override public void onFail(String result) {}
		});
	}


	/**
	 * 显示绑卡提示弹框
	 *
	 */
	protected void showBindCardTipDialog(int dialogType) {
		String title = dialogType == DIALOG_AUTH ? getString(R.string.jrmf_rp_auth_tip):getString(R.string.jrmf_rp_set_pwd_tip);
		DIALOG_TYPE = dialogType;
		bindCardTipDialog = DialogDisplay.getInstance().dialogLeftAndRight(LARDialogFragment.FROM_DEFAULT,title, getString(R.string.jrmf_rp_quit), getString(R.string.jrmf_rp_confirm), this);
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
	public void onRight() {//跳转到绑卡页面
		if (DIALOG_TYPE == DIALOG_AUTH){
			AuthenActivity.intent(context);
		}else{
			PwdH5Activity.intent(context,0);
		}
		if (bindCardTipDialog != null){
			bindCardTipDialog.dismissAllowingStateLoss();
			bindCardTipDialog = null;
		}
	}

	/**
	 * 群红包陶砖到支付页面
	 *
	 * @param temp
	 *            红包祝福语
	 * @param amount
	 *            单个红包金额
	 * @param envelopesType
	 *            红包类型
	 * @param number
	 *            红包
	 */
	public void jumpPayTypeActivity(RedEnvelopeModel temp, String amount, int envelopesType, String number, boolean hasPwd) {
		Intent intent = new Intent(this, PActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("temp", temp);
		bundle.putInt(ConstantUtil.ENVELOPES_TYPE,envelopesType);
		bundle.putString(ConstantUtil.AMOUNT,amount);
		bundle.putString(ConstantUtil.NUMBER,number);
		bundle.putBoolean(ConstantUtil.HAS_PWD,hasPwd);
		bundle.putString(ConstantUtil.RED_PACKET_NAME,envelopeName);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * 关闭页面并返回数据
	 *
	 * @param envelopesID
	 */
	public void finishWithResult(String envelopesID) {
		Intent intent = new Intent();
		intent.putExtra("envelopesID", envelopesID);
		intent.putExtra("envelopeMessage", envelopeMessage);
		intent.putExtra("envelopeName", envelopeName);
		setResult(RESULT_OK, intent);
		this.finish();
	}

	/**
	 * 限制只能输入金额
	 *
	 * @author zengchao
	 */
	protected class InputMoney implements InputFilter {

		@Override public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			if (source.toString().equals(".") && dstart == 0 && dend == 0) {// 判断小数点是否在第一位
				et_amount.setText(0 + "" + source + dest);// 给小数点前面加0
				et_amount.setSelection(2);// 设置光标
			}

			if (dstart >= 8) {
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
