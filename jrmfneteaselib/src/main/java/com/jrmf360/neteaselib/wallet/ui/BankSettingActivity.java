package com.jrmf360.neteaselib.wallet.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.adapter.ViewHolder;
import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.fragment.LARDialogFragment;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.manager.CusActivityManager;
import com.jrmf360.neteaselib.base.model.BaseModel;
import com.jrmf360.neteaselib.base.utils.DrawableUtil;
import com.jrmf360.neteaselib.base.utils.ImageLoadUtil;
import com.jrmf360.neteaselib.base.utils.KeyboardUtil;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.utils.ToastUtil;
import com.jrmf360.neteaselib.base.view.TitleBar;
import com.jrmf360.neteaselib.base.view.passwordview.GridPasswordView;
import com.jrmf360.neteaselib.wallet.constants.Constants;
import com.jrmf360.neteaselib.wallet.fragment.InputPwdDialogFragment;
import com.jrmf360.neteaselib.wallet.http.WalletHttpManager;
import com.jrmf360.neteaselib.wallet.http.model.AccountModel;
import com.jrmf360.neteaselib.wallet.http.model.ChargeInfoModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 银行卡设置页面
 * @author honglin
 *
 */
public class BankSettingActivity extends BaseActivity{

	private InputPwdDialogFragment dialogFragment;
	private LARDialogFragment errorDialogFragment;
	private ListView listView;
	private List<AccountModel> datas;
	private BankCardAdapter adapterBankCard;
	private int clickIndex;
	private String mBankDes;//银行卡后四位

	public static void intent(Activity fromActivity){
		Intent intent = new Intent(fromActivity,BankSettingActivity.class);
		fromActivity.startActivity(intent);
	}
	@Override
	public int getLayoutId() {
		return R.layout.jrmf_w_activity_bank_setting;
	}
	
	@Override
	public void initView() {
		titleBar = (TitleBar) findViewById(R.id.jrmf_w_titleBar);
		listView = (ListView) findViewById(R.id.listView);
	}
	@Override
	public void initListener() {
		titleBar.getIvBack().setOnClickListener(this);
	}
	@Override
	protected void initData(Bundle bundle) {
		titleBar.setTitle(getString(R.string.jrmf_w_my_bank_title));
		datas = new ArrayList<AccountModel>();
		datas.add(new AccountModel());
		adapterBankCard = new BankCardAdapter();
		listView.setAdapter(adapterBankCard);
		DialogDisplay.getInstance().dialogLoading(this, getString(R.string.jrmf_w_loading),this);
		loadHttpData();
	}
	
	public void refresh() {
		datas.clear();
		datas.add(new AccountModel());
		loadHttpData();
	}

	private void loadHttpData() {
		WalletHttpManager.myCardList(context,new OkHttpModelCallBack<ChargeInfoModel>() {
			@Override
			public void onSuccess(ChargeInfoModel accountModel) {
				DialogDisplay.getInstance().dialogCloseLoading(BankSettingActivity.this);
				if (accountModel == null) {
					ToastUtil.showToast(context, getString(R.string.jrmf_w_net_error_l));
					return;
				}
				
				if (accountModel.isSuccess()) {
					if (accountModel.accountList != null && accountModel.accountList.size() > 0) {
						datas.addAll(0, accountModel.accountList);
						adapterBankCard.notifyDataSetChanged();
					}
				}else {
					ToastUtil.showToast(context, accountModel.respmsg);
				}
			}

			@Override
			public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(BankSettingActivity.this);
				ToastUtil.showToast(context, getString(R.string.jrmf_w_net_error_l));
			}
		});
	}
	
	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.iv_back) {
			finish();
		}
	}
	
	/**
	 * 显示输入密码弹框
	 */
	private void showDialog(String bankDes) {
		mBankDes = bankDes;
		if (dialogFragment == null) {
			dialogFragment = new InputPwdDialogFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("fromKey", 2);
			bundle.putString("bankDes", bankDes);
			dialogFragment.setArguments(bundle);
			dialogFragment.setListener(new InputPwdDialogFragment.InputPwdListener() {
				
				@Override
				public void onFinish(GridPasswordView passwordView) {
					//密码输入完成
					KeyboardUtil.hideKeyboard(BankSettingActivity.this);
					reqUnbindCard(passwordView);
				}

				@Override
				public void forgetPwd() {
					//忘记支付密码-跳到找回密码页面呢
					PwdH5Activity.intent(context,2, Constants.FROM_BANK_SETTING);
				}
			});
		}else{
			Bundle bundle = new Bundle();
			bundle.putInt("fromKey", 2);
			bundle.putString("bankDes", bankDes);
			dialogFragment.setArguments(bundle);
		}
		dialogFragment.showAllowingStateLoss(getFragmentManager());
	}
	
	/**
	 * 请求服务器，解绑银行卡
	 */
	protected void reqUnbindCard(final GridPasswordView passwordView) {
		//请求服务器解绑银行卡
		if (datas == null || datas.size() <= 0) {
			return;
		}
		DialogDisplay.getInstance().dialogLoading(this, getString(R.string.jrmf_w_unbinding),this);
		AccountModel accountModel = datas.get(clickIndex);
		WalletHttpManager.unbindCard(context,passwordView.getPassWord(),accountModel.bankCardNo,new OkHttpModelCallBack<BaseModel>() {

			@Override
			public void onSuccess(BaseModel baseModel) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				if (baseModel == null) {
					ToastUtil.showToast(context, getString(R.string.jrmf_w_net_error_l));
					return;
				}
				
				if (baseModel.isSuccess()) {
					//解绑成功，刷新页面
					ToastUtil.showToast(context, getString(R.string.jrmf_w_unbind_suc));
					datas.remove(clickIndex);
					adapterBankCard.notifyDataSetChanged();
					dialogFragment.dismiss();
					//刷新首页
					MyWalletActivity myWalletActivity = CusActivityManager.getInstance().findActivity(MyWalletActivity.class);
					if (myWalletActivity != null) {
						myWalletActivity.refresh();
					}
				}else if ("R0012".equals(baseModel.respstat)) {
					//密码错误
					ToastUtil.showLongToast(context,baseModel.respmsg);
					passwordView.clearPassword();
				}else{
					ToastUtil.showToast(context, baseModel.respmsg);
				}
			}
			@Override
			public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				ToastUtil.showToast(context, getString(R.string.jrmf_w_net_error_l));
			}
		});
	}


	/**
	 * 显示输入密码错误弹框
	 */
	private void showErrorDialog() {
		if (errorDialogFragment == null) {
			errorDialogFragment = DialogDisplay.getInstance().dialogLeftAndRight(context, new LARDialogFragment.InputPwdErrorListener() {
				
				@Override
				public void onRight() {
					//忘记密码
					errorDialogFragment.dismiss();
					SecureSettingActivity.intent(BankSettingActivity.this);
				}
				
				@Override
				public void onLeft() {
					//重新输入
					errorDialogFragment.dismiss();
					showDialog(mBankDes);
				}
			});
		}
		errorDialogFragment.showAllowingStateLoss(getFragmentManager(),this.getClass().getSimpleName());
	}


//-------------------------------adapter-----------------------------------------------------------------------------------
	class BankCardAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return datas == null ? 0 : datas.size();
		}

		@Override
		public Object getItem(int position) {
			return datas == null ? null : datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public int getItemViewType(int position) {
			if (position == datas.size() - 1) {
				return 1;
			}else{
				return 0;
			}
		}
		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			if (type == 0) {
				final AccountModel accountModel = datas.get(position);
				ViewHolder holderCardList = ViewHolder.get(context, convertView, parent, R.layout.jrmf_w_item_setting_bank_cark_list, position);
				LinearLayout ll_item_root = holderCardList.getView(R.id.ll_item_root);

				ImageView iv_bankCardLogo = holderCardList.getView(R.id.iv_bankCardLogo);
				iv_bankCardLogo.setImageDrawable(getResources().getDrawable(R.drawable.jrmf_w_ic_card));

				TextView tv_bankCardName = holderCardList.getView(R.id.tv_bankCardName);
				TextView tv_bankCardNum = holderCardList.getView(R.id.tv_bankCardNum);
//				TextView tv_unbind = holderCardList.getView(R.id.tv_unbind);
				if (StringUtil.isNotEmpty(accountModel.logo_url)) {
					ImageLoadUtil.getInstance().loadImage(iv_bankCardLogo, accountModel.logo_url);
				}
				tv_bankCardName.setText(accountModel.bankName);
				tv_bankCardNum.setText("**** ***** ***** "+accountModel.bankCardNoDesc);
				
				//模拟颜色
				Drawable drawable = DrawableUtil.generateDrawable(context,"#157efb", 6);
				ll_item_root.setBackgroundDrawable(drawable);
				
//				tv_unbind.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						//解绑-先输入密码
//						clickIndex = position;
//						if (SPManager.getInstance().getInt(context,Constants.IS_HSA_PWD,-1) == 0) {
//							//用户没有设置密码
//							PwdH5Activity.intent(context,0,Constants.FROM_BANK_SETTING);
//						} else {
//							//用户设置了密码
//							showDialog(accountModel.bankCardNoDesc);
//						}
//					}
//				});
				return holderCardList.getConvertView();
			}else{
				//添加银行卡
				ViewHolder holderAddCard = ViewHolder.get(context, convertView, parent, R.layout.jrmf_w_item_add_bank_card, position);
				LinearLayout ll_add_card = holderAddCard.getView(R.id.ll_add_card);
				ll_add_card.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						AddCardFirstActivity.intent(BankSettingActivity.this);
					}
				});
				return holderAddCard.getConvertView();
			}
		}
	}
}











