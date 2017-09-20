package com.jrmf360.neteaselib.wallet.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.adapter.ListViewAdapter;
import com.jrmf360.neteaselib.base.adapter.ViewHolder;
import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.utils.ImageLoadUtil;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.utils.ToastUtil;
import com.jrmf360.neteaselib.base.view.TitleBar;
import com.jrmf360.neteaselib.wallet.http.WalletHttpManager;
import com.jrmf360.neteaselib.wallet.http.model.AccountModel;
import com.jrmf360.neteaselib.wallet.http.model.SupportBankModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 支持的银行卡列表页面
 * @author honglin
 *
 */
public class SupportBankCardActivity extends BaseActivity{

	private ListView listView;
	private List<AccountModel> datas;
	private BankCardAdapter adapterBankCard;
	
	public static void intent(Activity fromActivity){
		Intent intent = new Intent(fromActivity,SupportBankCardActivity.class);
		fromActivity.startActivity(intent);
	}
	@Override
	public int getLayoutId() {
		return R.layout.jrmf_w_activity_support_bank_card;
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
		titleBar.setTitle(getString(R.string.jrmf_w_bank_list));
		loadHttpData();
		
		datas = new ArrayList<AccountModel>();
		adapterBankCard = new BankCardAdapter(context, datas, R.layout.jrmf_w_item_bank_cark_list);
		listView.setAdapter(adapterBankCard);
	}
	
	
	private void loadHttpData() {
		DialogDisplay.getInstance().dialogLoading(this, getString(R.string.jrmf_w_loading),this);
		WalletHttpManager.supportCardList(context,new OkHttpModelCallBack<SupportBankModel>() {
			@Override
			public void onSuccess(SupportBankModel accountModel) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				if (accountModel == null) {
					ToastUtil.showToast(context, getString(R.string.jrmf_w_net_error_l));
					return;
				}
				
				if (accountModel.bankList != null && accountModel.bankList.size() > 0){
					datas.addAll(accountModel.bankList);
					adapterBankCard.notifyDataSetChanged();
				}
			}

			@Override
			public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
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
	 * adapter
	 */
	class BankCardAdapter extends ListViewAdapter<AccountModel> {

		public BankCardAdapter(Context context, List<AccountModel> datas,int layoutId) {
			super(context, datas, layoutId);
		}

		@Override
		public void convert(ViewHolder holder, AccountModel t) {
			ImageView iv_bankCardLogo = holder.getView(R.id.iv_bankCardLogo);
			TextView tv_cardName = holder.getView(R.id.tv_cardName);

			if (StringUtil.isNotEmpty(t.logo_url)){
				iv_bankCardLogo.setTag(t.logo_url);
				ImageLoadUtil.getInstance().loadImage(iv_bankCardLogo,t.logo_url);
			}
			tv_cardName.setText(t.bankName);
		}
	}
}











