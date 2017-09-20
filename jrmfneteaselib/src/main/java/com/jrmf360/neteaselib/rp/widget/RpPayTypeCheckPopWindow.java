package com.jrmf360.neteaselib.rp.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.utils.ImageLoadUtil;
import com.jrmf360.neteaselib.base.utils.LanguageUtil;
import com.jrmf360.neteaselib.base.utils.ScreenUtil;
import com.jrmf360.neteaselib.rp.constants.ConstantUtil;
import com.jrmf360.neteaselib.rp.http.model.AccountModel;
import com.jrmf360.neteaselib.rp.http.model.RedEnvelopeModel;

import java.util.List;

/**
 * 支付方式父类 钱包充值父类 Created by Administrator on 2016/2/26.
 */
public class RpPayTypeCheckPopWindow extends PopupWindow {

	private View			mMenuView;

	private TextView		tv_exit;		// 取消

	private LinearLayout	layout_paytype;	// 旧卡

	private Context			context;

	/**
	 * 发送红包时使用
	 *
	 * @param context
	 * @param redEnvelopeModel
	 * @param blance
	 *            余额
	 * @param flag
	 *            零钱是否足够发送此次红包
	 */
	public RpPayTypeCheckPopWindow(Activity context, RedEnvelopeModel redEnvelopeModel, String blance, boolean flag) {
		super(context);
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.jrmf_rp_dialog_paytype, null);
		LanguageUtil.setRtl(mMenuView, context);
		tv_exit = (TextView) mMenuView.findViewById(R.id.tv_exit);
		tv_exit.setOnClickListener(new View.OnClickListener() {

			@Override public void onClick(View v) {
				dismiss();
				onClickListener.onDismisss();
			}
		});
		layout_paytype = (LinearLayout) mMenuView.findViewById(R.id.layout_paytype);
		if (redEnvelopeModel.getBalance == 1){//获取余额成功
			addChangeView(inflater, flag,redEnvelopeModel.getBalance == 1, blance);
			addOldCardView(inflater, redEnvelopeModel);
			addAlipayView(inflater, redEnvelopeModel);
			addWechatView(inflater, redEnvelopeModel);
			addMulCardView(inflater, redEnvelopeModel);
		}else{
			addOldCardView(inflater, redEnvelopeModel);
			addAlipayView(inflater, redEnvelopeModel);
			addWechatView(inflater, redEnvelopeModel);
			addMulCardView(inflater, redEnvelopeModel);
			addChangeView(inflater, flag,redEnvelopeModel.getBalance == 1, blance);
		}

		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(ScreenUtil.getScreenWidth(context) / 6 * 5);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(ScreenUtil.getScreenHeight(context) / 3);

//		if (redEnvelopeModel.myBankcards != null && redEnvelopeModel.myBankcards.size() > 0) {
//			// 有银行卡
//			this.setHeight(ScreenUtil.getScreenHeight(context) / 3);
//		} else {
//			// 没有银行卡
//			this.setHeight(ScreenUtil.getScreenHeight(context) / 3);
//		}
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(false);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.Jrmf_Rp_AnimBottom);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(00000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
	}

	private void addChangeView(LayoutInflater inflater, boolean flag,boolean getBalance, String blance) {
		LinearLayout includeView = (LinearLayout) inflater.inflate(R.layout.jrmf_rp_dialog_paytype_old_card_item, null);
		LinearLayout layout = (LinearLayout) includeView.findViewById(R.id.bank_layout);
		layout.setOnClickListener(new MyOnClickListener(ConstantUtil.PAY_TYPE_CHANGE, 0));
		ImageView bankIcon = (ImageView) includeView.findViewById(R.id.iv_bank_icon);
		TextView bankName = (TextView) includeView.findViewById(R.id.tv_bank_name);
		bankIcon.setBackgroundResource(R.drawable.jrmf_rp_ic_charge);
		if (getBalance){
			if (flag) {
				bankName.setText(String.format(context.getString(R.string.jrmf_rp_balance), blance));
				bankName.setTextColor(context.getResources().getColor(R.color.jrmf_rp_black));
			} else {
				bankName.setText(String.format(context.getString(R.string.jrmf_rp_balance_less), blance));
				bankName.setTextColor(context.getResources().getColor(R.color.jrmf_rp_gray));
			}
			layout.setEnabled(flag);
		}else{
			bankName.setText(context.getString(R.string.jrmf_rp_not_get_balance));
			bankName.setTextColor(context.getResources().getColor(R.color.jrmf_rp_gray));
			layout.setEnabled(false);
		}
		layout_paytype.addView(includeView);
	}

	private void addOldCardView(LayoutInflater inflater, RedEnvelopeModel sendSinglePeakVO) {
		if (sendSinglePeakVO != null) {
			List<AccountModel> accountVOs = sendSinglePeakVO.myBankcards;
			if (accountVOs != null && accountVOs.size() > 0) {
				for (int i = 0; i < accountVOs.size(); i++) {
					LinearLayout includeView = (LinearLayout) inflater.inflate(R.layout.jrmf_rp_dialog_paytype_old_card_item, null);
					LinearLayout layout = (LinearLayout) includeView.findViewById(R.id.bank_layout);
					layout.setOnClickListener(new MyOnClickListener(ConstantUtil.PAY_TYPE_OLDCARD, i));
					ImageView bankIcon = (ImageView) includeView.findViewById(R.id.iv_bank_icon);
					TextView bankName = (TextView) includeView.findViewById(R.id.tv_bank_name);
					bankName.setText(accountVOs.get(i).bankName + "（" + accountVOs.get(i).bankCardNoDesc + "）");
					ImageLoadUtil.getInstance().loadImage(bankIcon, accountVOs.get(i).logo_url);
					layout_paytype.addView(includeView);

				}
			}
		}
	}

	private void addWechatView(LayoutInflater inflater, RedEnvelopeModel sendSinglePeakVO) {
		if (sendSinglePeakVO.isSupportWeChatPay == 1) {
			LinearLayout includeView = (LinearLayout) inflater.inflate(R.layout.jrmf_rp_dialog_paytype_old_card_item, null);
			LinearLayout layout = (LinearLayout) includeView.findViewById(R.id.bank_layout);
			layout.setOnClickListener(new MyOnClickListener(ConstantUtil.PAY_TYPE_WECHAT, 0));
			ImageView bankIcon = (ImageView) includeView.findViewById(R.id.iv_bank_icon);
			TextView bankName = (TextView) includeView.findViewById(R.id.tv_bank_name);
			bankIcon.setBackgroundResource(R.drawable.jrmf_rp_ic_wx);
			bankName.setText(context.getString(R.string.jrmf_rp_wechat));
			layout_paytype.addView(includeView);
		}
	}

	private void addAlipayView(LayoutInflater inflater, RedEnvelopeModel sendSinglePeakVO) {
		if (sendSinglePeakVO.isSupportAliPay == 1) {
			LinearLayout includeView = (LinearLayout) inflater.inflate(R.layout.jrmf_rp_dialog_paytype_old_card_item, null);
			LinearLayout layout = (LinearLayout) includeView.findViewById(R.id.bank_layout);
			layout.setOnClickListener(new MyOnClickListener(ConstantUtil.PAY_TYPE_ALIPAY, 0));
			ImageView bankIcon = (ImageView) includeView.findViewById(R.id.iv_bank_icon);
			TextView bankName = (TextView) includeView.findViewById(R.id.tv_bank_name);
			bankIcon.setBackgroundResource(R.drawable.jrmf_rp_ic_alipay);
			bankName.setText(context.getString(R.string.jrmf_rp_alipay));
			layout_paytype.addView(includeView);

		}
	}

	private void addMulCardView(LayoutInflater inflater, RedEnvelopeModel sendSinglePeakVO) {
		boolean isHasBind = sendSinglePeakVO.myBankcards != null && sendSinglePeakVO.myBankcards.size() > 0; // 已经绑过卡了
		boolean isSupportMulCard = sendSinglePeakVO.isSupportMulCard == 1;// 支持绑多卡
		if (!isHasBind || isSupportMulCard) {
			LinearLayout includeView = (LinearLayout) inflater.inflate(R.layout.jrmf_rp_dialog_paytype_old_card_item, null);
			LinearLayout layout = (LinearLayout) includeView.findViewById(R.id.bank_layout);
			layout.setOnClickListener(new MyOnClickListener(ConstantUtil.PAY_TYPE_NEWCARD, 0));
			ImageView bankIcon = (ImageView) includeView.findViewById(R.id.iv_bank_icon);
			TextView bankName = (TextView) includeView.findViewById(R.id.tv_bank_name);
			bankIcon.setBackgroundResource(R.drawable.jrmf_rp_ic_card);
			bankName.setText(context.getString(R.string.jrmf_rp_add_card_pay));
			layout_paytype.addView(includeView);

		}
	}

	private OnClickListener onClickListener = null;

	private class MyOnClickListener implements View.OnClickListener {

		private int paytype = -1, tag = -1;

		private MyOnClickListener(int paytype, int tag) {
			this.paytype = paytype;
			this.tag = tag;
		}

		@Override public void onClick(View v) {
			dismiss();
			if (onClickListener != null) {
				onClickListener.onClick(paytype, tag);
			}
		}
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public interface OnClickListener {

		void onClick(int paytype, int tag);

		void onDismisss();
	}

}
