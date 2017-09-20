package com.jrmf360.neteaselib.wallet.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TradeDetailAdapter extends FragmentPagerAdapter{
	List<Fragment> fragmentList;

	public TradeDetailAdapter(FragmentManager fm,List<Fragment> datas) {
		super(fm);
		fragmentList = datas;
	}

	@Override
	public Fragment getItem(int position) {
		return fragmentList != null && fragmentList.size() > 0 ? fragmentList.get(position) : null;
	}

	@Override
	public int getCount() {
		return fragmentList != null && fragmentList.size() > 0 ? fragmentList.size() : 0;
	}
}
