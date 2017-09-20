package com.jrmf360.neteaselib.rp.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.utils.ImageLoadUtil;
import com.jrmf360.neteaselib.base.utils.KeyboardUtil;
import com.jrmf360.neteaselib.base.utils.LogUtil;
import com.jrmf360.neteaselib.base.utils.NoDoubleClickUtils;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.utils.ToastUtil;
import com.jrmf360.neteaselib.base.view.RoundImageView;
import com.jrmf360.neteaselib.base.wrapper.BaseTextWatcher;
import com.jrmf360.neteaselib.rp.constants.ConstantUtil;
import com.jrmf360.neteaselib.rp.http.RpHttpManager;
import com.jrmf360.neteaselib.rp.http.model.TransInfoModel;
import com.jrmf360.neteaselib.rp.http.model.TransModel;
import com.jrmf360.neteaselib.rp.widget.ActionBarView;

/**
 * @创建人 honglin
 * @创建时间 17/2/21 上午11:29
 * @类描述 转账页面
 */
public class TransAccountActivity extends TransBaseActivity{

	private TextView		tv_name, tv_trans_tip, tv_update_tip;

	private EditText		et_trans_money;

	private RoundImageView iv_header;

	private Button			btn_trans_account;

	private String			targetId;

	private String			recUserName;							// 接受者的名字

	private AlertDialog		transDesdialog;

	private AlertDialog		transRepeatdialog;

	private boolean			isFirstShowDialog	= true;

	private String			transferLimit = "20000";

	private TransModel mTransModel;

	private int				isAuthentication;						// 1 真实名

	private int				isCompleteInfo;							// 2 假实名

	@Override public int getLayoutId() {
		return R.layout.jrmf_rp_activity_trans_account;
	}

	@Override public void initView() {
		actionBarView = (ActionBarView) findViewById(R.id.actionbar);
		iv_header = (RoundImageView) findViewById(R.id.iv_header);
		tv_name = (TextView) findViewById(R.id.tv_name);
		et_trans_money = (EditText) findViewById(R.id.et_trans_money);
		tv_trans_tip = (TextView) findViewById(R.id.tv_trans_tip);
		tv_update_tip = (TextView) findViewById(R.id.tv_update_tip);
		btn_trans_account = (Button) findViewById(R.id.btn_trans_account);
		setClickable(btn_trans_account, false);
		// 弹出软键盘
		KeyboardUtil.popInputMethod(et_trans_money);
	}

	@Override public void initListener() {
		actionBarView.getIvBack().setOnClickListener(this);
		tv_trans_tip.setOnClickListener(this);
		tv_update_tip.setOnClickListener(this);
		btn_trans_account.setOnClickListener(this);
		et_trans_money.addTextChangedListener(new BaseTextWatcher() {

			@Override public void onTextChanged(CharSequence s, int start, int before, int count) {
				super.onTextChanged(s, start, before, count);
			}

			@Override public void afterTextChanged(Editable s) {
				super.afterTextChanged(s);
				checkButtomButtom(s);
			}
		});
		// 设置过滤器
		et_trans_money.setFilters(new InputFilter[] { new InputMoney() });
	}

	/**
	 * 检查底部按钮是不是可点击
	 *
	 * @param s
	 */
	private void checkButtomButtom(Editable s) {
		if (s == null || StringUtil.isEmpty(s.toString()) || s.toString().startsWith(".")) {
			setClickable(btn_trans_account, false);
		} else {
			setClickable(btn_trans_account, true);
		}
	}

	@Override protected void initData(Bundle bundle) {
		if (bundle != null) {
			targetId = bundle.getString(ConstantUtil.R_TargetId);
			recUserName = bundle.getString(ConstantUtil.JRMF_REC_USER_NAME);
			String recUserIcon = bundle.getString(ConstantUtil.JRMF_REC_USER_ICON);
			String userName = bundle.getString(ConstantUtil.JRMF_USER_NAME);
			String userIcon = bundle.getString(ConstantUtil.JRMF_USER_ICON);
			tv_name.setText(recUserName);
			if (StringUtil.isNotEmpty(recUserIcon)) {
				ImageLoadUtil.getInstance().loadImage(iv_header, recUserIcon);
			}
			getTransInfo(userName,userIcon,recUserName, recUserIcon);
		}
	}

	/**
	 * 从网络获取转账信息
	 */
	private void getTransInfo(String username,String usericon,String recUserName, String recUserIcon) {

		RpHttpManager.getTransInfo(context, userid, username, usericon, thirdToken, targetId, recUserName, recUserIcon, new OkHttpModelCallBack<TransInfoModel>() {

			@Override public void onSuccess(TransInfoModel transInfo) {
				transferLimit = transInfo.transferLimit;
			}

			@Override public void onFail(String result) {
				LogUtil.e("获取转账信息失败：" + result);
			}
		});
	}

	@Override public void onClick(int id) {
		if (id == R.id.iv_back) {
			finish();
		} else if (id == R.id.tv_trans_tip) {
			// 添加转账说明
			showTransTipDialog();
		} else if (id == R.id.tv_update_tip) {
			// 修改转账说明
			showTransTipDialog();
		} else if (id == R.id.btn_trans_account) {
			// 点击转账
			if (!NoDoubleClickUtils.isDoubleClick()) {
				transAccount();
			}
		}
	}

	/**
	 * 用户点击转账按钮
	 */
	private void transAccount() {
		// 验证金额最小值
		final String transMoney = et_trans_money.getText().toString();
		if (StringUtil.formatMoneyDouble(transMoney) <= 0) {
			ToastUtil.showToast(context, getResources().getString(R.string.jrmf_rp_trans_little_tip));
			return;
		}

		// 验证金额最大值
		if (StringUtil.formatMoneyDouble(transMoney) > StringUtil.formatMoneyDouble(transferLimit)) {
//			String toast = "转账金额超过限额" + StringUtil.formatMoney(transferLimit) + "元";
			String toast = String.format(getString(R.string.jrmf_rp_trans_most_tip),transferLimit);
			ToastUtil.showToast(context, toast);
			return;
		}
		// 调用转账接口，请求网络
		String transDesc = isFirstShowDialog ? "" : tv_trans_tip.getText().toString().trim();

		DialogDisplay.getInstance().dialogLoading(context, getString(R.string.jrmf_rp_loading));
		RpHttpManager.confirmTransInfo(context, userid, thirdToken, targetId, transMoney, transDesc, new OkHttpModelCallBack<TransModel>() {

			@Override public void onSuccess(TransModel transModel) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				mTransModel = transModel;
				isCompleteInfo = transModel.isComplete;
				isAuthentication = transModel.isAuthentication;
				if (transModel.isSuccess()) {
					// 先判断是否有相同的转账
					if (transModel.isWarn) {
						// 有相同的转账-弹框提示
						showTransRepeatDialog(transModel);
					} else {
						// 跳转到付款页面
						TransPayActivity.intent(context, targetId, recUserName, transModel, transMoney);
					}
				} else {
					ToastUtil.showToast(context, transModel.respmsg);
				}
			}

			@Override public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				ToastUtil.showToast(context, getString(R.string.jrmf_rp_network_error));
			}
		});
	}

	/**
	 * 显示转账说明弹框
	 */
	private void showTransTipDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.jrmf_rp_trans_tip_dialog, null);
		builder.setView(view);
		builder.setCancelable(false);
		final EditText cet_trans_des = (EditText) view.findViewById(R.id.cet_trans_des);// 输入内容
		if (!isFirstShowDialog) {
			cet_trans_des.setText(tv_trans_tip.getText().toString().trim());
		}
		TextView tv_quit = (TextView) view.findViewById(R.id.tv_quit);// 输入内容
		TextView tv_confirm = (TextView) view.findViewById(R.id.tv_confirm);// 输入内容

		tv_quit.setOnClickListener(new View.OnClickListener() {

			@Override public void onClick(View v) {
				transDesdialog.dismiss();
				KeyboardUtil.closeSoftKeybord(cet_trans_des, context);

			}
		});

		tv_confirm.setOnClickListener(new View.OnClickListener() {

			@Override public void onClick(View v) {
				String transDes = cet_trans_des.getText().toString().trim();
				if (StringUtil.isNotEmpty(transDes)) {
					tv_trans_tip.setText(transDes.trim());
					tv_trans_tip.setTextColor(getResources().getColor(R.color.color_888888));
					tv_update_tip.setVisibility(View.VISIBLE);
					if (isFirstShowDialog) {
						isFirstShowDialog = false;
					}
				} else {
					tv_trans_tip.setText(getResources().getString(R.string.jrmf_rp_add_trans_tip));
					tv_trans_tip.setTextColor(getResources().getColor(R.color.jrmf_b_title_bar_color));
					tv_update_tip.setVisibility(View.INVISIBLE);
				}
				transDesdialog.dismiss();
				KeyboardUtil.closeSoftKeybord(cet_trans_des, context);

			}
		});
		// 取消或确定按钮监听事件处理
		transDesdialog = builder.create();
		transDesdialog.show();
		KeyboardUtil.popInputMethod(cet_trans_des);
	}

	/**
	 * 显示转账重复弹框
	 */
	private void showTransRepeatDialog(final TransModel transModel) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.jrmf_rp_trans_repeat_dialog, null);
		builder.setView(view);
		builder.setCancelable(false);
		TextView tv_same_trans_tip = (TextView) view.findViewById(R.id.tv_same_trans_tip);
		tv_same_trans_tip.setText(String.format(getString(R.string.jrmf_rp_trans_repeat_tip), transModel.count));
		TextView tv_continue_trans = (TextView) view.findViewById(R.id.tv_continue_trans);// 继续转账
		TextView tv_quit = (TextView) view.findViewById(R.id.tv_quit);// 取消

		tv_quit.setOnClickListener(new View.OnClickListener() {

			@Override public void onClick(View v) {
				transRepeatdialog.dismiss();
			}
		});

		tv_continue_trans.setOnClickListener(new View.OnClickListener() {

			@Override public void onClick(View v) {
				// 跳转到付款页面- 先去掉
//				TransPayActivity.intent(context, targetId, recUserName, transModel, et_trans_money.getText().toString().trim());
				transRepeatdialog.dismiss();
			}
		});
		// 取消或确定按钮监听事件处理
		transRepeatdialog = builder.create();
		transRepeatdialog.show();
	}

	public void finishWithResult() {
		Intent intent = new Intent();
		intent.putExtra("transferOrder", mTransModel.transferOrderNo);
		intent.putExtra("transferAmount", StringUtil.formatMoney(et_trans_money.getText().toString().trim()));
		intent.putExtra("transferDesc", isFirstShowDialog ? "" : tv_trans_tip.getText().toString().trim());
		intent.putExtra("transferStatus", "0");
		setResult(RESULT_OK, intent);
		this.finish();
	}


	/**
	 * 限制只能输入金额
	 *
	 * @author zengchao
	 */
	private class InputMoney implements InputFilter {

		@Override public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			if (source.toString().equals(".") && dstart == 0 && dend == 0) {// 判断小数点是否在第一位
				et_trans_money.setText(0 + "" + source + dest);// 给小数点前面加0
				et_trans_money.setSelection(2);// 设置光标
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
