package com.jrmf360.neteaselib.rp.ui;


import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.utils.KeyboardUtil;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.utils.ToastUtil;
import com.jrmf360.neteaselib.base.view.ClearEditText;
import com.jrmf360.neteaselib.rp.http.RpHttpManager;
import com.jrmf360.neteaselib.rp.http.model.SubmitCardResModel;

/**
 * @创建人 honglin
 * @创建时间 2017/5/11 下午1:51
 * @类描述 模仿钱包添加银行卡第一步
 */
public class AcfActivity extends BaseActivity {

	private ClearEditText cet_bankCardNum;

	private TextView		tv_support;

	private Button			btn_next;

	public static void intent(Activity fromActivity) {
		Intent intent = new Intent(fromActivity, AcfActivity.class);
		fromActivity.startActivity(intent);
	}

	@Override public int getLayoutId() {
		return R.layout.jrmf_rp_activity_acf;
	}

	@Override public void initView() {
		cet_bankCardNum = (ClearEditText) findViewById(R.id.cet_bankCardNum);
		tv_support = (TextView) findViewById(R.id.tv_support);
		btn_next = (Button) findViewById(R.id.btn_next);
		KeyboardUtil.popInputMethod(cet_bankCardNum);

	}

	@Override public void initListener() {
		actionBarView.getIvBack().setOnClickListener(this);
		tv_support.setOnClickListener(this);
		btn_next.setOnClickListener(this);
	}

	@Override public void onClick(int id) {
		if (id == R.id.iv_back) {
			finish();
		} else if (id == R.id.btn_next) {
			// 请求接口，提交银行卡号
			submit();
		} else if (id == R.id.tv_support) {
			// 查看支持的银行卡
			Intent intent = new Intent(context, BankCardListActivity.class);
			startActivity(intent);
		}
	}

	private void submit() {
		String bankCardNum = cet_bankCardNum.getText().toString().trim();
		if (!StringUtil.checkBankCard(bankCardNum)) {
			ToastUtil.showToast(context, getString(R.string.jrmf_rp_card_num_error));
			return;
		}

		DialogDisplay.getInstance().dialogLoading(this,getString(R.string.jrmf_rp_loading), this);
		RpHttpManager.submitCardno(context, userid, thirdToken, bankCardNum, new OkHttpModelCallBack<SubmitCardResModel>() {

			@Override public void onSuccess(SubmitCardResModel cardResModel) {
				DialogDisplay.getInstance().dialogCloseLoading(context);

				if (cardResModel.isSuccess()) {
					// 银行卡校验成功跳转到绑卡第二步
					AcsActivity.intent(context,cardResModel.realName,cardResModel.bankName,
							cardResModel.identityNo,cardResModel.bankCardNo,cardResModel.isAuthentication,cardResModel.bankNo);
				} else {
					ToastUtil.showToast(context, cardResModel.respmsg);
				}
			}

			@Override public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				ToastUtil.showToast(context, getString(R.string.jrmf_rp_network_error));
			}
		});
	}
}
