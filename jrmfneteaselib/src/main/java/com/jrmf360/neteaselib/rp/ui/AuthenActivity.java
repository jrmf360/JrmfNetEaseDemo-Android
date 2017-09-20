package com.jrmf360.neteaselib.rp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.model.BaseModel;
import com.jrmf360.neteaselib.base.utils.CountUtil;
import com.jrmf360.neteaselib.base.utils.KeyboardUtil;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.utils.ToastUtil;
import com.jrmf360.neteaselib.base.view.ClearEditText;
import com.jrmf360.neteaselib.rp.constants.ConstantUtil;
import com.jrmf360.neteaselib.rp.http.RpHttpManager;
import com.jrmf360.neteaselib.rp.http.model.CodeModel;

/**
 * @创建人 honglin
 * @创建时间 2017/8/22 上午10:23
 * @类描述 用户实名认证页面
 */
public class AuthenActivity extends BaseActivity {

	private ClearEditText	cet_name, cet_idCardNum, cet_phone, cet_code;

	private TextView		tv_send_code;

	private Button			btn_next;

	private String			mobileToken;

	public static void intent(Activity fromActivty) {
		Intent intent = new Intent(fromActivty, AuthenActivity.class);
		fromActivty.startActivity(intent);
	}

	public static void openRpIntent(Activity fromActivty,String userId,String thirdToken) {
		Intent intent = new Intent(fromActivty, AuthenActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(ConstantUtil.JRMF_USER_ID, userId);
		bundle.putString(ConstantUtil.JRMF_THIRD_TOKEN, thirdToken);
		intent.putExtras(bundle);
		fromActivty.startActivity(intent);
	}

	@Override public int getLayoutId() {
		return R.layout.jrmf_rp_activity_authen;
	}

	@Override public void initView() {
		cet_name = (ClearEditText) findViewById(R.id.cet_name);
		cet_idCardNum = (ClearEditText) findViewById(R.id.cet_idCardNum);
		cet_phone = (ClearEditText) findViewById(R.id.cet_phone);
		cet_code = (ClearEditText) findViewById(R.id.cet_code);
		tv_send_code = (TextView) findViewById(R.id.tv_send_code);
		btn_next = (Button) findViewById(R.id.btn_next);

		KeyboardUtil.popInputMethod(cet_name);
	}

	@Override public void initListener() {
		actionBarView.getIvBack().setOnClickListener(this);
		btn_next.setOnClickListener(this);
		tv_send_code.setOnClickListener(this);
	}

	@Override public void onClick(int id) {
		if (id == R.id.iv_back) {
			finish();
		} else if (id == R.id.btn_next) {
			authen();
		} else if (id == R.id.tv_send_code) {// 发送验证码
			sendCode();
		}
	}

	private void sendCode() {
		String userName = cet_name.getText().toString().trim();
		if (StringUtil.isEmpty(userName)) {
			ToastUtil.showToast(context, getString(R.string.jrmf_rp_name_error));
			return;
		}

		String idCardNum = cet_idCardNum.getText().toString().trim();
		if (!StringUtil.isIDCard(idCardNum)) {
			ToastUtil.showToast(context, getString(R.string.jrmf_rp_id_card_error));
			return;
		}

		String phoneNum = cet_phone.getText().toString().trim();
		if (!StringUtil.isMobile(phoneNum)) {
			ToastUtil.showToast(context, getString(R.string.jrmf_rp_phone_tip));
			return;
		}

        DialogDisplay.getInstance().dialogLoading(context, getString(R.string.jrmf_rp_loading), this);
        RpHttpManager.certifySendCode(context, userid, thirdToken, idCardNum, userName, phoneNum, new OkHttpModelCallBack<CodeModel>() {

			@Override public void onSuccess(CodeModel codeModel) {
                DialogDisplay.getInstance().dialogCloseLoading(context);
				if (codeModel.isSuccess()){
					mobileToken = codeModel.mobileToken;
					ToastUtil.showToast(context,getString(R.string.jrmf_rp_verify_code_suss));
					// 验证码发送成功-开始记时
					CountUtil countUtil = new CountUtil(60000, 1000, tv_send_code, 0);
					countUtil.start();
				}else{
					ToastUtil.showToast(context,codeModel.respmsg);
				}

			}

			@Override public void onFail(String result) {
                DialogDisplay.getInstance().dialogCloseLoading(context);
                ToastUtil.showNoWaitToast(context, getString(R.string.jrmf_rp_net_error_l));
			}
		});

	}

	private void authen() {
        //判断有没有请求过验证码
        if (StringUtil.isEmptyOrNull(mobileToken)){
            ToastUtil.showToast(context,getString(R.string.jrmf_rp_req_code));
            return;
        }

        String code = cet_code.getText().toString().trim();
        if (StringUtil.isEmptyOrNull(code)){
            ToastUtil.showToast(context,getString(R.string.jrmf_rp_input_verify_code2));
            return;
        }

        //请求网络实名认证
        DialogDisplay.getInstance().dialogLoading(context, getString(R.string.jrmf_rp_loading), this);
        RpHttpManager.certifyValiCode(context, userid, thirdToken, mobileToken, code,new OkHttpModelCallBack<BaseModel>() {
            @Override
            public void onSuccess(BaseModel baseModel) {
                DialogDisplay.getInstance().dialogCloseLoading(context);
				if (baseModel.isSuccess()){
					PwdH5Activity.intent(context, 0);
					finish();

//					ToastUtil.showToast(context,getString(R.string.jrmf_rp_auth_success));
//					cet_name.postDelayed(new Runnable() {
//						@Override
//						public void run() {
//							finish();
//						}
//					},500);
				}else{
					ToastUtil.showToast(context,baseModel.respmsg);
				}
            }

            @Override
            public void onFail(String result) {
                DialogDisplay.getInstance().dialogCloseLoading(context);
                ToastUtil.showNoWaitToast(context, getString(R.string.jrmf_rp_net_error_l));
            }
        });

	}
}









