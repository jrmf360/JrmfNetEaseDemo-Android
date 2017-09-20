package com.jrmf360.neteaselib.base.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jrmf360.neteaselib.base.interfaces.UIInterface;

/**
 * Fragment的基类
 * @author honglin
 *
 */
public abstract class BaseFragment extends Fragment implements UIInterface {
	protected View rootView;
	protected FragmentActivity fromActivity;
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(getLayoutId(), container, false);
			fromActivity = getActivity();
			initView();
			initListener();
			initData(getArguments());
		}
		return rootView;
	}
	
	@Override
	public void onClick(View view) {}

	@Override
	public void initView() {}

	@Override
	public void initListener() {}
	
	protected void initData(Bundle bundle){};

}
