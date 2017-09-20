package com.jrmf360.neteaselib.rp.fragment;//package com.jrmf360.rplib.fragment;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.app.DialogFragment;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.RelativeLayout;
//
//import com.jrmf360.tools.interfaces.UIInterface;
//import com.jrmf360.tools.utils.ScreenUtil;
//
///**
// * @创建人 honglin
// * @创建时间 16/12/20 上午11:58
// * @类描述 一句话描述 你的UI
// */
//public abstract class OpenRpBaseDialogFragment extends DialogFragment implements UIInterface {
//
//	protected View		rootView;
//
//	protected Activity	fromActivity;
//
//	@Override public Dialog onCreateDialog(Bundle savedInstanceState) {
//		LayoutInflater inflater = getActivity().getLayoutInflater();
//		rootView = inflater.inflate(getLayoutId(), null);
//		fromActivity = getActivity();
//		initView();
//		initListener();
//		initData(getArguments());
//
//		Dialog dialog = new Dialog(this.getActivity(), com.jrmf360.tools.R.style.Jrmf_b_DialogTheme);
//		ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ScreenUtil.dp2px(getActivity(), 300), RelativeLayout.LayoutParams.WRAP_CONTENT);
//		dialog.addContentView(rootView, layoutParams);
//		return dialog;
//	}
//
//	@Override public void onClick(View v) {}
//
//	@Override public void initView() {}
//
//	@Override public void initListener() {}
//
//	protected void initData(Bundle bundle) {}
//}
