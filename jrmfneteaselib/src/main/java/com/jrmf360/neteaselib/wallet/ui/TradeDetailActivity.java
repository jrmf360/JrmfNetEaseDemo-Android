package com.jrmf360.neteaselib.wallet.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.view.TitleBar;

/**
 * 交易详情页面
 * 
 * @author honglin
 *
 */
public class TradeDetailActivity extends BaseActivity {

	private TextView tv_tradeMoney, tv_tradeType, tv_tradeTime, tv_tradeNo;
	private TextView tv_transferCharge,tv_transferType,tv_transferTypeTitle,tv_remark,tv_tradeState;
	private RelativeLayout rl_tradeNo, rl_transferCharge,rl_transferType,rl_remark;

	/**
	 * @param fromActivity
	 * @param tradeType    交易类型
	 * @param tradeTime    交易时间
	 * @param tradeNo      交易单号
	 */
	public static void intent(Activity fromActivity, String tradeType, String tradeTime, String tradeNo,
							  String tradeAmount, int transferType, String charge, String tradeName, String remark, String bankName, String bankCardNo, String payTypeDesc) {
		Intent intent = new Intent(fromActivity, TradeDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("tradeType", tradeType);
		bundle.putString("tradeTime", tradeTime);
		bundle.putString("tradeNo", tradeNo);
		bundle.putString("tradeAmount", tradeAmount);
		bundle.putInt("transferType", transferType);
		bundle.putString("charge", charge);
		bundle.putString("tradeName", tradeName);
		bundle.putString("remark", remark);
		bundle.putString("payTypeDesc", payTypeDesc);
		bundle.putString("bankName", bankName);
		bundle.putString("bankCardNo", bankCardNo);
		intent.putExtras(bundle);
		fromActivity.startActivity(intent);
	}


	@Override public int getLayoutId() {
		return R.layout.jrmf_w_activity_trade_detail;
	}

	@Override public void initView() {
		titleBar = (TitleBar) findViewById(R.id.jrmf_w_titleBar);
		tv_tradeMoney = (TextView) findViewById(R.id.tv_tradeMoney);
		tv_tradeState = (TextView) findViewById(R.id.tv_tradeState);
		tv_tradeType = (TextView) findViewById(R.id.tv_tradeType);
		tv_tradeTime = (TextView) findViewById(R.id.tv_tradeTime);
		tv_tradeNo = (TextView) findViewById(R.id.tv_tradeNo);
		tv_transferCharge = (TextView) findViewById(R.id.tv_transferCharge);
		tv_transferType = (TextView) findViewById(R.id.tv_transferType);
		tv_transferTypeTitle = (TextView) findViewById(R.id.tv_transferTypeTitle);
		tv_remark = (TextView) findViewById(R.id.tv_remark);
		rl_tradeNo = (RelativeLayout) findViewById(R.id.rl_tradeNo);
		rl_transferCharge = (RelativeLayout) findViewById(R.id.rl_transferCharge);
		rl_transferType = (RelativeLayout) findViewById(R.id.rl_transferType);
		rl_remark = (RelativeLayout) findViewById(R.id.rl_remark);

	}

	@Override
	public void initListener() {
		titleBar.getIvBack().setOnClickListener(this);
	}

	@Override protected void initData(Bundle bundle) {
		titleBar.setTitle(getString(R.string.jrmf_w_trade_detail));
		if (bundle != null) {
			String tradeType = bundle.getString("tradeType");
			String tradeTime = bundle.getString("tradeTime");
			String tradeNo = bundle.getString("tradeNo");
			String tradeAmount = bundle.getString("tradeAmount");
			String tradeName = bundle.getString("tradeName");
			String remark = bundle.getString("remark");
			String payTypeDesc = bundle.getString("payTypeDesc");
			int transferType = bundle.getInt("transferType");
			//银行名称和后四位
			String bankName = bundle.getString("bankName");
			String bankCardNo = bundle.getString("bankCardNo");

			//交易名称
			tv_tradeState.setText(tradeName);

			//交易金额
			if ("in".equals(tradeType.trim())) {
				// 收入
				tv_tradeType.setText("收入");
				tv_tradeMoney.setText("+" + tradeAmount);
				tv_tradeMoney.setTextColor(getResources().getColor(R.color.jrmf_w_red_dark));
			} else {
				// 支出
				tv_tradeType.setText("支出");
				tv_tradeMoney.setText("-" + tradeAmount);

//				String charge = bundle.getString("charge");
//				if (transferType == 1 && StringUtil.isNotEmptyAndNull(charge)) {
//					//有手续费
//					rl_transferCharge.setVisibility(View.VISIBLE);
//					tv_transferCharge.setText(charge + "元");
//				}
			}
			//时间
			tv_tradeTime.setText(tradeTime);
			if (StringUtil.isNotEmptyAndNull(tradeNo)) {
				rl_tradeNo.setVisibility(View.VISIBLE);
				tv_tradeNo.setText(tradeNo);
			} else {
				rl_tradeNo.setVisibility(View.GONE);
			}

			//冻结账户  交易方式不显示
			if (transferType == 101) {
				rl_transferType.setVisibility(View.GONE);
			}

			//交易方式
			if ("in".equals(tradeType.trim())) {//收入
				//0 用户充值 1 红包退款 2 抢红包 3 保险返款 4 收款(B端消费) 5 转账 6 活动赠送 7 用户支付（C端消费） 8 转账退款 9 退款 10 提现失败退款
				if (2 == transferType || 4 == transferType || 5 == transferType || 7 == transferType) {
					rl_transferType.setVisibility(View.GONE);
				} else if (8 == transferType || 9 == transferType || 10 == transferType) {
					tv_transferTypeTitle.setText("退款方式");
				} else {
					tv_transferTypeTitle.setText("交易方式");
				}

			} else {//支出
				//0 用户消费 1 用户提现 2 发红包 3 转账 4 收银台支付 9 无手续费提现
				if (1 == transferType || 9 == transferType) {
					tv_transferTypeTitle.setText("提现银行");
				} else {
					tv_transferTypeTitle.setText("交易方式");
				}
			}

			//只要返回数据有银行名称和卡号 就显示银行加后四位
			if (StringUtil.isNotEmpty(bankName) && StringUtil.isNotEmpty(bankCardNo)) {
				tv_transferType.setText(bankName + "(" + bankCardNo + ")");
			} else {
				tv_transferType.setText(payTypeDesc);
			}

			//备注
			if (StringUtil.isNotEmptyAndNull(remark)) {
				rl_remark.setVisibility(View.VISIBLE);
				tv_remark.setText(remark);
			}

		}
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.iv_back){
			finish();
		}
	}
}













