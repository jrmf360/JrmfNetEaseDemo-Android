package com.jrmf360.neteaselib.rp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.utils.ImageLoadUtil;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.utils.ToastUtil;
import com.jrmf360.neteaselib.rp.http.RpHttpManager;
import com.jrmf360.neteaselib.rp.http.model.BankResModel;

import java.util.List;

/**
 * 支持的银行卡列表 选择银行
 */
public class BankCardListActivity extends BaseActivity {

	private ListView					recyclerview;

	private TextView					refresh;

	private List<BankResModel.BankVo> mDatas;

	private MyAdapter					adapter;

	@Override public int getLayoutId() {
		return R.layout.jrmf_rp_activity_bank_card_list;
	}

	@Override public void initView() {
		refresh = (TextView) findViewById(R.id.refresh);
		refresh.setVisibility(View.GONE);
		recyclerview = (ListView) findViewById(R.id.recyclerview);
		adapter = new MyAdapter();
		recyclerview.setAdapter(adapter);// 设置adapter
	}

	@Override protected void initData(Bundle bundle) {
		super.initData(bundle);
		getBankList();
	}

	@Override public void initListener() {
		actionBarView.getIvBack().setOnClickListener(this);
		refresh.setOnClickListener(new View.OnClickListener() {

			@Override public void onClick(View v) {
				getBankList();
			}
		});
	}

	@Override public void onClick(int id) {
		if (id == R.id.iv_back) {
			finish();
		}
	}

	private void getBankList() {
		DialogDisplay.getInstance().dialogLoading(context, getString(R.string.jrmf_rp_loading),this);
		RpHttpManager.getBankList(context, userid, thirdToken, new OkHttpModelCallBack<BankResModel>() {

			@Override public void onSuccess(BankResModel bankResModel) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				if (bankResModel.isSuccess()) {
					if (bankResModel.bankList != null && bankResModel.bankList.size() > 0) {
						refresh.setVisibility(View.GONE);
						mDatas = bankResModel.bankList;
						adapter.notifyDataSetChanged();
					} else {
						refresh.setVisibility(View.VISIBLE);
					}
				}
			}

			@Override public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				ToastUtil.showToast(context, getString(R.string.jrmf_rp_network_error));
			}
		});
	}

	class MyAdapter extends BaseAdapter {

		@Override public int getCount() {
			return mDatas != null ? mDatas.size() : 0;
		}

		class MyViewHolder {

			ImageView	iv_bank_icon;

			TextView	tv_bank_name;
		}

		@Override public BankResModel.BankVo getItem(int arg0) {

			return mDatas.get(arg0);
		}

		@Override public long getItemId(int position) {

			return position;
		}

		@Override public View getView(int position, View view, ViewGroup parent) {
			BankResModel.BankVo item = mDatas.get(position);
			MyViewHolder holder;
			if (view == null) {
				view = LayoutInflater.from(BankCardListActivity.this).inflate(R.layout.jrmf_rp_adapter_banklist_item, parent, false);
				holder = new MyViewHolder();
				holder.iv_bank_icon = (ImageView) view.findViewById(R.id.iv_bank_icon);
				holder.tv_bank_name = (TextView) view.findViewById(R.id.tv_bank_name);
				view.setTag(holder);
			} else {
				holder = (MyViewHolder) view.getTag();
			}
			holder.tv_bank_name.setText(item.bankname);
			if (StringUtil.isNotEmpty(item.logo_url)){
				holder.iv_bank_icon.setTag(item.logo_url);
				ImageLoadUtil.getInstance().loadImage(holder.iv_bank_icon, item.logo_url);
			}
			return view;
		}
	}
}
