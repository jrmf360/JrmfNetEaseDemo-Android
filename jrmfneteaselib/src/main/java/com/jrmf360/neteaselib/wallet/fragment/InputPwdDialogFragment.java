package com.jrmf360.neteaselib.wallet.fragment;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.fragment.BaseDialogFragment;
import com.jrmf360.neteaselib.base.utils.CountUtil;
import com.jrmf360.neteaselib.base.utils.KeyboardUtil;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.view.passwordview.GridPasswordView;

import static com.jrmf360.neteaselib.R.id.gpv_pswd;

/**
 * 显示密码输入框
 * @author honglin
 */
public class InputPwdDialogFragment extends BaseDialogFragment {

	private int fromKey = -1;//表示从哪个地方跳转过来的
	private GridPasswordView passwordView;
	private View iv_exit;
	private TextView tv_forget_pwd,tv_money,tv_title;
	private InputPwdListener mListener;
	@Override
	public int getLayoutId() {
		return R.layout.jrmf_w_dialog_input_pwd;
	}
	
	@Override
	public void initView() {
		passwordView = (GridPasswordView) rootView.findViewById(gpv_pswd);
		iv_exit =  rootView.findViewById(R.id.iv_exit);
		tv_forget_pwd =  (TextView) rootView.findViewById(R.id.tv_forget_pwd);
		tv_money =  (TextView) rootView.findViewById(R.id.tv_money);
		tv_title =  (TextView) rootView.findViewById(R.id.tv_title);
		setCancelable(true);
		KeyboardUtil.popInputMethod(passwordView.getEditText());
	}
	
	@Override
	protected void initData(Bundle bundle) {
		if (bundle != null) {
			fromKey = bundle.getInt("fromKey");
			if (0 == fromKey) {
				//充值－ 输入手机验证码
				tv_forget_pwd.setGravity(Gravity.CENTER);
				tv_title.setText(getString(R.string.jrmf_w_input_code));
				tv_forget_pwd.setText(getString(R.string.jrmf_w_get_code));
				String recharge = StringUtil.formatMoney(bundle.getString("jrmf_w_recharge"));
				tv_money.setText(String.format(getString(R.string.jrmf_w_recharge_money), recharge));
				passwordView.setPasswordVisibility(true);
				//显示倒计时
				CountUtil countUtil = new CountUtil(60000,1000,tv_forget_pwd,1);
				countUtil.start();
			}else if (1 == fromKey) {
				//提现输入密码
				String money = StringUtil.formatMoney(bundle.getString("money"));
				tv_money.setText(String.format(getString(R.string.jrmf_w_deposit_money), money));
			}else if (2 == fromKey) {
				//解绑银行卡输入密码
				String bankDes = getString(R.string.jrmf_w_del_card_des);
				tv_money.setText(String.format(bankDes,  bundle.getString("bankDes")));
			}
		}
	}
	
	
	@Override
	public void initListener() {
		iv_exit.setOnClickListener(this);
		tv_forget_pwd.setOnClickListener(this);
		passwordView.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
			
			@Override
			public void onTextChanged(String psw) {
				
			}
			
			@Override
			public void onInputFinish(String psw) {
				//密码输入完成
				mListener.onFinish(passwordView);
			}
		});
	}


	/**
	 * 显示dialogFragment
	 * @param fragmentManager
	 * @return
	 */
	public DialogFragment showAllowingStateLoss(FragmentManager fragmentManager) {
		FragmentTransaction ft = fragmentManager.beginTransaction();
		ft.add(this, this.getClass().getName());
		ft.commitAllowingStateLoss();
		return this;
	}
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.iv_exit) {
			dismissAllowingStateLoss();
		}else if (id == R.id.tv_forget_pwd) {
			//忘记密码或者重新获取验证码
			mListener.forgetPwd();
		}
	}


	public void setListener(InputPwdListener listener){
		this.mListener = listener;
	}
	
	public interface InputPwdListener{
		void onFinish(GridPasswordView passwordView);
		void forgetPwd();
	}
}





