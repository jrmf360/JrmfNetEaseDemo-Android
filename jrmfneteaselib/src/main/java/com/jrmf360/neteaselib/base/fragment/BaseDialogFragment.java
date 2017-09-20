package com.jrmf360.neteaselib.base.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.interfaces.UIInterface;
import com.jrmf360.neteaselib.base.utils.ScreenUtil;

public abstract class BaseDialogFragment extends DialogFragment implements UIInterface {

	protected View rootView;
	protected Activity fromActivity;
	public static boolean touchOutsides = true;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		rootView = inflater.inflate(getLayoutId(), null);
		fromActivity = getActivity();
		initView();
		initListener();
		initData(getArguments());
		
		Dialog dialog = new Dialog(this.getActivity(), R.style.Jrmf_b_DialogTheme);
		ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ScreenUtil.dp2px(getActivity(),300),LayoutParams.WRAP_CONTENT);

		dialog.addContentView(rootView, layoutParams);
		dialog.setCanceledOnTouchOutside(touchOutsides);// 设置点击屏幕Dialog不消失
		return dialog;
	}
	
	@Override
	public void onClick(View v) {}

	@Override
	public void initView() {}

	@Override
	public void initListener() {}
	
	protected void initData(Bundle bundle){};


}
