package com.jrmf360.neteaselib.rp.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.utils.ImageLoadUtil;
import com.jrmf360.neteaselib.base.utils.ScreenUtil;
import com.jrmf360.neteaselib.rp.constants.ConstantUtil;
import com.jrmf360.neteaselib.rp.http.model.AccountModel;
import com.jrmf360.neteaselib.rp.http.model.RedEnvelopeModel;
import com.jrmf360.neteaselib.rp.http.model.TransModel;

import java.util.HashMap;
import java.util.List;

/**
 * 支付方式父类 钱包充值父类 Created by Administrator on 2016/2/26.
 */
public class TransPayTypeCheckPopWindow extends PopupWindow {

    private View			mMenuView;

    private TextView		tv_exit;		// 取消

    private LinearLayout	layout_paytype;	// 旧卡

    private Context			context;

    /**
     * 发送红包时使用
     *
     * @param context
     * @param transModel
     * @param blance
     *            余额
     * @param flag
     *            零钱是否足够发送此次红包
     */
    public TransPayTypeCheckPopWindow(Activity context, TransModel transModel, String blance, boolean flag) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.jrmf_rp_dialog_paytype, null);

        tv_exit = (TextView) mMenuView.findViewById(R.id.tv_exit);
        tv_exit.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                dismiss();
                onClickListener.onDismisss();
            }
        });
        layout_paytype = (LinearLayout) mMenuView.findViewById(R.id.layout_paytype);

        //零钱
        addChangeView(inflater, flag, blance);
        //老卡
        addOldCardView(inflater, transModel);
        //支付宝
        addAlipayView(inflater, transModel);
        //添加新卡
        addMulCardView(inflater, transModel);
        //微信-暂时不支持
        //addWechatView(inflater, redEnvelopeModel);

        // 设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ScreenUtil.getScreenWidth(context) / 6 * 5);
        // 设置SelectPicPopupWindow弹出窗体的高
        if (transModel.myBankcards != null && transModel.myBankcards.size() > 0) {
            //有银行卡
            this.setHeight(ScreenUtil.getScreenHeight(context) / 5 * 2);
        } else {
            this.setHeight(ScreenUtil.getScreenHeight(context) / 3);
        }
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(false);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.Jrmf_Rp_AnimBottom);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(00000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        // 点击的是窗口之外的区域
                        // dismiss();
                    }
                }
                return true;
            }
        });
    }

    private void addChangeView(LayoutInflater inflater, boolean flag, String blance) {
        LinearLayout includeView = (LinearLayout) inflater.inflate(R.layout.jrmf_rp_dialog_paytype_balance_item, null);
        LinearLayout layout = (LinearLayout) includeView.findViewById(R.id.bank_layout);
        layout.setOnClickListener(new MyOnClickListener(ConstantUtil.PAY_TYPE_CHANGE, 0));
        ImageView bankIcon = (ImageView) includeView.findViewById(R.id.iv_bank_icon);
        ImageView iv_paytype_rmb = (ImageView) includeView.findViewById(R.id.iv_paytype_rmb);
        TextView bankName = (TextView) includeView.findViewById(R.id.tv_bank_name);
        TextView tv_paytype_balance = (TextView) includeView.findViewById(R.id.tv_paytype_balance);
        bankIcon.setBackgroundResource(R.drawable.jrmf_rp_ic_charge);
        if (flag) {
//            bankName.setText("零钱支付（ 余额 ¥  " + blance + " ）");
            tv_paytype_balance.setText(" " + blance + " )");
            bankIcon.getBackground().mutate().setAlpha(255);
            iv_paytype_rmb.getBackground().mutate().setAlpha(255);
            tv_paytype_balance.setTextColor(context.getResources().getColor(R.color.jrmf_rp_black));
            bankName.setTextColor(context.getResources().getColor(R.color.jrmf_rp_black));
        } else {
            bankIcon.getBackground().mutate().setAlpha(100);
            iv_paytype_rmb.getBackground().mutate().setAlpha(100);
//            bankName.setText("零钱不足（ 余额 ¥  " + blance + " ）");
            tv_paytype_balance.setText(" " + blance + " )");
            bankName.setTextColor(context.getResources().getColor(R.color.jrmf_rp_gray));
            tv_paytype_balance.setTextColor(context.getResources().getColor(R.color.jrmf_rp_gray));

        }
        layout.setEnabled(flag);
        layout_paytype.addView(includeView);
    }

    HashMap<String, Drawable>	iconCache	= new HashMap<>();

    private void addOldCardView(LayoutInflater inflater, TransModel sendSinglePeakVO) {
        if (sendSinglePeakVO != null) {
            List<AccountModel> accountModels = sendSinglePeakVO.myBankcards;
            if (accountModels != null && accountModels.size() > 0) {
                for (int i = 0; i < accountModels.size(); i++) {
                    LinearLayout includeView = (LinearLayout) inflater.inflate(R.layout.jrmf_rp_dialog_paytype_old_card_item, null);
                    LinearLayout layout = (LinearLayout) includeView.findViewById(R.id.bank_layout);
                    layout.setOnClickListener(new MyOnClickListener(ConstantUtil.PAY_TYPE_OLDCARD, i));
                    ImageView bankIcon = (ImageView) includeView.findViewById(R.id.iv_bank_icon);
                    TextView bankName = (TextView) includeView.findViewById(R.id.tv_bank_name);
                    bankName.setText(accountModels.get(i).bankName + "（" + accountModels.get(i).bankCardNoDesc + "）");
                    ImageLoadUtil.getInstance().loadImage(bankIcon, accountModels.get(i).logo_url);
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
            bankName.setText("微信支付");
            layout_paytype.addView(includeView);
        }
    }

    private void addAlipayView(LayoutInflater inflater, TransModel sendSinglePeakVO) {

        if (sendSinglePeakVO.isSupportAliPay != null && sendSinglePeakVO.isSupportAliPay.equals("1")) {
            LinearLayout includeView = (LinearLayout) inflater.inflate(R.layout.jrmf_rp_dialog_paytype_old_card_item, null);
            LinearLayout layout = (LinearLayout) includeView.findViewById(R.id.bank_layout);
            layout.setOnClickListener(new MyOnClickListener(ConstantUtil.PAY_TYPE_ALIPAY, 0));
            ImageView bankIcon = (ImageView) includeView.findViewById(R.id.iv_bank_icon);
            TextView bankName = (TextView) includeView.findViewById(R.id.tv_bank_name);
            bankIcon.setBackgroundResource(R.drawable.jrmf_rp_ic_alipay);
            bankName.setText("支付宝支付");
            layout_paytype.addView(includeView);
        }
    }

    // 添加新卡支付
    private void addMulCardView(LayoutInflater inflater, TransModel sendSinglePeakVO) {
        boolean isSupportMulCard = sendSinglePeakVO.isSupportMulCard != null && sendSinglePeakVO.isSupportMulCard.equals("1");// 支持绑多卡
        if (isSupportMulCard) {
            LinearLayout includeView = (LinearLayout) inflater.inflate(R.layout.jrmf_rp_dialog_paytype_old_card_item, null);
            LinearLayout layout = (LinearLayout) includeView.findViewById(R.id.bank_layout);
            layout.setOnClickListener(new MyOnClickListener(ConstantUtil.PAY_TYPE_NEWCARD, 0));
            ImageView bankIcon = (ImageView) includeView.findViewById(R.id.iv_bank_icon);
            TextView bankName = (TextView) includeView.findViewById(R.id.tv_bank_name);
            bankIcon.setBackgroundResource(R.drawable.jrmf_rp_ic_card);
            bankName.setText("添加银行卡支付");
            layout_paytype.addView(includeView);
        }
    }

    private OnClickListener	onClickListener	= null;

    private class MyOnClickListener implements View.OnClickListener {

        private int	paytype	= -1, tag = -1;

        private MyOnClickListener(int paytype) {
            this.paytype = paytype;
        }

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

    public Drawable getIcon(String bankNo) {
        return iconCache.get(bankNo);
    }
}
