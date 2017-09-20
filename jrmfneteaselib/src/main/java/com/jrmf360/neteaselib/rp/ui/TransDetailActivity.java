package com.jrmf360.neteaselib.rp.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.fragment.LARDialogFragment;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.model.BaseModel;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.utils.ToastUtil;
import com.jrmf360.neteaselib.rp.bean.TransAccountBean;
import com.jrmf360.neteaselib.rp.constants.ConstantUtil;
import com.jrmf360.neteaselib.rp.http.RpHttpManager;
import com.jrmf360.neteaselib.rp.http.model.TransDetailModel;
import com.jrmf360.neteaselib.rp.utils.callback.TransAccountCallBack;
import com.jrmf360.neteaselib.rp.widget.ActionBarView;
import com.jrmf360.neteaselib.rp.widget.NoUnderClickableSpan;

/**
 * @创建人 honglin
 * @创建时间 17/2/21 下午4:51
 * @类描述 转账详情页面
 */
public class TransDetailActivity extends TransBaseActivity implements LARDialogFragment.InputPwdErrorListener {

	private ImageView					iv_trans_state;

	private TextView					tv_trans_state, tv_trans_money, tv_trans_tip, tv_trans_reback_tip, tv_trans_time, tv_collect_money_time;

	private LinearLayout				ll_confirm_collect_money;

	private Button						btn_trans_finish;

	private String						mTransferOrderNo;																						// 转账订单号

	private LARDialogFragment dialogFragment;

	private TransDetailModel mTransDetailModel;

	private static TransAccountCallBack transAccountCallBack;

	/**
	 * 带回调接口
	 * 
	 * @param fromActivity
	 * @param userId
	 * @param thirdToken
	 * @param transferOrder
//	 * @param username
//	 * @param usericon
	 * @param transCallBack
	 */
	public static void intent(Activity fromActivity, String userId, String thirdToken, String transferOrder, TransAccountCallBack transCallBack) {
		Intent intent = new Intent(fromActivity, TransDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(ConstantUtil.JRMF_USER_ID, userId);
		bundle.putString(ConstantUtil.JRMF_THIRD_TOKEN, thirdToken);
		bundle.putString("transferOrder", transferOrder);
		transAccountCallBack = transCallBack;
		intent.putExtras(bundle);
		fromActivity.startActivity(intent);
	}

	@Override public int getLayoutId() {
		return R.layout.jrmf_rp_activity_trans_detail;
	}

	@Override public void initView() {
		actionBarView = (ActionBarView) findViewById(R.id.actionbar);
		tv_trans_state = (TextView) findViewById(R.id.tv_trans_state);
		tv_trans_money = (TextView) findViewById(R.id.tv_trans_money);
		tv_trans_tip = (TextView) findViewById(R.id.tv_trans_tip);
		tv_trans_reback_tip = (TextView) findViewById(R.id.tv_trans_reback_tip);
		tv_trans_time = (TextView) findViewById(R.id.tv_trans_time);
		tv_collect_money_time = (TextView) findViewById(R.id.tv_collect_money_time);
		iv_trans_state = (ImageView) findViewById(R.id.iv_trans_state);
		btn_trans_finish = (Button) findViewById(R.id.btn_trans_finish);
		ll_confirm_collect_money = (LinearLayout) findViewById(R.id.ll_confirm_collect_money);
	}

	@Override public void initListener() {
		actionBarView.getIvBack().setOnClickListener(this);
		btn_trans_finish.setOnClickListener(this);

		String rebackTip = getString(R.string.jrmf_rp_reback_tip);
		SpannableString spannableString = new SpannableString(rebackTip);
		int index = rebackTip.indexOf(getString(R.string.jrmf_rp_now_back));
		spannableString.setSpan(peakTypeClick, index, rebackTip.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_5b6a91)), index, rebackTip.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv_trans_reback_tip.setText(spannableString);
		tv_trans_reback_tip.setMovementMethod(LinkMovementMethod.getInstance());
		// 不设置没有点击事件
		tv_trans_reback_tip.setHighlightColor(Color.TRANSPARENT); // 设置点击后的颜色为透明
	}

	/**
	 * 用户点击立即退还按钮
	 */
	private NoUnderClickableSpan peakTypeClick = new NoUnderClickableSpan() {

		@Override public void onClick(View widget) {
			// 弹框提示用户
			showRebackMoneyDialog();
		}
	};

	@Override
    protected void initData(Bundle bundle) {
        if (bundle != null) {
            mTransferOrderNo = bundle.getString("transferOrder");
            // 请求网络获取订单信息
            getTransDetail(mTransferOrderNo);
        }
    }

	/**
	 * 获取转账详情
	 *
	 * @param transferOrderNo
	 */
	private void getTransDetail(final String transferOrderNo) {
		DialogDisplay.getInstance().dialogLoading(context, getString(R.string.jrmf_rp_loading));
		RpHttpManager.getTransDetail(context, userid, thirdToken, transferOrderNo, new OkHttpModelCallBack<TransDetailModel>() {

			@Override public void onSuccess(TransDetailModel transDetailModel) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				if (transDetailModel.isSuccess()) {
					mTransDetailModel = transDetailModel;
					showUI(transDetailModel);
				} else {
					ToastUtil.showToast(context, transDetailModel.respmsg);
				}
			}

			@Override public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				ToastUtil.showToast(context, getString(R.string.jrmf_rp_network_error));
			}
		});
	}

	/**
	 * 获得转账信息之后，显示界面
	 *
	 * @param transDetailModel
	 */
	private void showUI(TransDetailModel transDetailModel) {
		if (transDetailModel.isSelf) {
			// 自己发起的转账
			if (transDetailModel.transferType == 0) {
				// 等待领取
				iv_trans_state.setImageDrawable(getResources().getDrawable(R.drawable.jrmf_rp_ic_trans_wait));
				tv_trans_state.setText(String.format(getString(R.string.jrmf_rp_trans_wait), StringUtil.getFixUserName(transDetailModel.username)));
				tv_trans_tip.setText(getString(R.string.jrmf_rp_timeout_back));
				tv_trans_money.setText(transDetailModel.amount);
				tv_trans_time.setText(String.format(getString(R.string.jrmf_rp_trans_time),transDetailModel.transferTime));
				tv_collect_money_time.setVisibility(View.GONE);
			} else if (transDetailModel.transferType == 1) {
				// 已领取
				iv_trans_state.setImageDrawable(getResources().getDrawable(R.drawable.jrmf_rp_ic_trans_succ));
				tv_trans_state.setText(String.format(getString(R.string.jrmf_rp_trans_succ), StringUtil.getFixUserName(transDetailModel.username)));
				tv_trans_money.setText(transDetailModel.amount);
				tv_trans_time.setText(String.format(getString(R.string.jrmf_rp_trans_time),transDetailModel.transferTime));
				tv_collect_money_time.setText(String.format(getString(R.string.jrmf_rp_receive_money_time),transDetailModel.dealTime));
			} else if (transDetailModel.transferType == 2) {
				// 退还
				iv_trans_state.setImageDrawable(getResources().getDrawable(R.drawable.jrmf_rp_ic_trans_reback));
				tv_trans_state.setText(String.format(getString(R.string.jrmf_rp_trans_raback), StringUtil.getFixUserName(transDetailModel.username)));
				tv_trans_money.setText(transDetailModel.amount);
				tv_trans_tip.setText(getString(R.string.jrmf_rp_back_to_wallet));
				tv_trans_time.setText(String.format(getString(R.string.jrmf_rp_trans_time),transDetailModel.transferTime));
				tv_collect_money_time.setText(String.format(getString(R.string.jrmf_rp_back_time),transDetailModel.dealTime));
			} else if (transDetailModel.transferType == 3) {
				// 过期
				iv_trans_state.setImageDrawable(getResources().getDrawable(R.drawable.jrmf_rp_ic_trans_fail));
				tv_trans_state.setText(getString(R.string.jrmf_rp_timeout_back2));
				tv_trans_money.setText(transDetailModel.amount);
				tv_trans_tip.setText(getString(R.string.jrmf_rp_back_to_wallet));
				tv_trans_time.setText(String.format(getString(R.string.jrmf_rp_trans_time),transDetailModel.transferTime));
				tv_collect_money_time.setText(String.format(getString(R.string.jrmf_rp_back_time),transDetailModel.dealTime));
			}
		} else {
			// 别人发起的转账
			if (transDetailModel.transferType == 0) {
				// 待领取
				iv_trans_state.setImageDrawable(getResources().getDrawable(R.drawable.jrmf_rp_ic_trans_wait));
				tv_trans_state.setText(getString(R.string.jrmf_rp_waitint_confirm));
				tv_trans_money.setText(transDetailModel.amount);
				ll_confirm_collect_money.setVisibility(View.VISIBLE);
				tv_trans_time.setText(String.format(getString(R.string.jrmf_rp_trans_time),transDetailModel.transferTime));
				tv_collect_money_time.setVisibility(View.GONE);
			}
			if (transDetailModel.transferType == 1) {
				// 已收钱
				iv_trans_state.setImageDrawable(getResources().getDrawable(R.drawable.jrmf_rp_ic_trans_succ));
				tv_trans_state.setText(getString(R.string.jrmf_rp_had_money));
				tv_trans_money.setText(transDetailModel.amount);
				tv_trans_tip.setText(getString(R.string.jrmf_rp_save_to_wallet));
				tv_trans_time.setText(String.format(getString(R.string.jrmf_rp_trans_time),transDetailModel.transferTime));
				tv_collect_money_time.setText(String.format(getString(R.string.jrmf_rp_receive_money_time),transDetailModel.transferTime));
			}
			if (transDetailModel.transferType == 2 || transDetailModel.transferType == 3) {
				// 已退还
				iv_trans_state.setImageDrawable(getResources().getDrawable(R.drawable.jrmf_rp_ic_trans_reback));
				tv_trans_state.setText(getString(R.string.jrmf_rp_timeout_back3));
				tv_trans_money.setText(transDetailModel.amount);
				tv_trans_time.setText(String.format(getString(R.string.jrmf_rp_trans_time),transDetailModel.transferTime));
				tv_collect_money_time.setText(String.format(getString(R.string.jrmf_rp_back_time),transDetailModel.dealTime));
			}
		}
	}

	@Override public void onClick(int id) {
		if (id == R.id.iv_back) {
			finish();
		} else if (id == R.id.btn_trans_finish) {
			// 确认收钱
			transReceiveMoney();
		}
	}

	/**
	 * 请求网络确认收钱
	 */
	private void transReceiveMoney() {

		DialogDisplay.getInstance().dialogLoading(context, getString(R.string.jrmf_rp_loading));
		RpHttpManager.transReceiveMoney(context, userid, thirdToken, mTransferOrderNo, new OkHttpModelCallBack<BaseModel>() {

			@Override public void onSuccess(BaseModel baseModel) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				if (baseModel.isSuccess()) {
					// 发送收款成功消息
					sendTransBaceAndReceiveMessage("1");
					finish();
				} else {
					ToastUtil.showToast(context, baseModel.respmsg);
				}
			}

			@Override public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				ToastUtil.showToast(context, getString(R.string.jrmf_rp_network_error));
			}
		});
	}

	/**
	 * 显示退款弹框
	 */
	private void showRebackMoneyDialog() {
		dialogFragment = DialogDisplay.getInstance().dialogLeftAndRight(LARDialogFragment.FROM_DEFAULT,String.format(getString(R.string.jrmf_rp_reback_dialog), mTransDetailModel.sendUserName), getString(R.string.jrmf_rp_quit),
				getString(R.string.jrmf_rp_confirm), this);
		dialogFragment.showAllowingStateLoss(getFragmentManager(),this.getClass().getSimpleName());

	}

	@Override public void onLeft() {
		if (dialogFragment != null) {
			dialogFragment.dismissAllowingStateLoss();
		}
	}

	@Override public void onRight() {
		// 请求网络退款
		transBack();
	}

	/**
	 * 请求网络发起退款
	 */
	private void transBack() {
		DialogDisplay.getInstance().dialogLoading(context, getString(R.string.jrmf_rp_loading));
		RpHttpManager.transBack(context, userid, thirdToken, mTransferOrderNo, new OkHttpModelCallBack<BaseModel>() {

			@Override public void onSuccess(BaseModel baseModel) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				if (baseModel.isSuccess()) {
					// 发送退款成功消息
					sendTransBaceAndReceiveMessage("2");
					finish();
				} else {
					ToastUtil.showToast(context, baseModel.respmsg);
				}
			}

			@Override public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				ToastUtil.showToast(context, getString(R.string.jrmf_rp_network_error));
			}
		});
	}

	/**
	 * 退款成功发送消息
	 */
	private void sendTransBaceAndReceiveMessage(String transferStatus) {
        TransAccountBean transAccountBean = new TransAccountBean();
        transAccountBean.setTransferStatus(transferStatus);
		transAccountBean.setTransferAmount(mTransDetailModel.amount);
		transAccountBean.setTransferOrder(mTransferOrderNo);
		String transferDesp = "1".equals(transferStatus) ? getString(R.string.jrmf_rp_had_money) : getString(R.string.jrmf_rp_timeout_back3);
		transAccountBean.setTransferDesc(transferDesp);

		if (transAccountCallBack != null){
			transAccountCallBack.transResult(transAccountBean);
		}
	}

}
