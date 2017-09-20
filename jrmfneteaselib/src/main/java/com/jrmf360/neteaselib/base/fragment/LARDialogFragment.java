package com.jrmf360.neteaselib.base.fragment;


import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.utils.StringUtil;

/**
 * title left right的dialog
 * 
 * @author honglin
 */
public class LARDialogFragment extends BaseDialogFragment {

	private TextView				tv_title, tv_left, tv_right;

	private InputPwdErrorListener	mListener;

	private int						fromkey;

	public static int				FROM_DEFAULT		= 0X000;

	public static int				GET_DEPOSIT_WH_ERROR	= 0X110;

	/**
	 * 自定义title，以及左右按钮的文字
	 * 
	 * @param fromkey
	 *            //从哪个地方显示的弹框
	 * @param title
	 * @param left
	 * @param right
	 * @return
	 */
	public static LARDialogFragment newInstance(int fromkey, String title, String left, String right, boolean touchOutside) {
		touchOutsides = touchOutside;
		LARDialogFragment fragment = new LARDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString("title", title);
		bundle.putString("left", left);
		bundle.putString("right", right);
		bundle.putInt("fromkey", fromkey);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override public int getLayoutId() {
		return R.layout.jrmf_b_dialog_pwd_error;
	}

	@Override public void initView() {
		tv_title = (TextView) rootView.findViewById(R.id.tv_title);
		tv_left = (TextView) rootView.findViewById(R.id.tv_left);
		tv_right = (TextView) rootView.findViewById(R.id.tv_right);
	}

	@Override public void initListener() {
		tv_left.setOnClickListener(this);
		tv_right.setOnClickListener(this);
	}

	@Override protected void initData(Bundle bundle) {
		if (bundle != null) {
			String title = bundle.getString("title");
			String left = bundle.getString("left");
			if (StringUtil.isEmptyOrNull(left)){
				tv_left.setVisibility(View.GONE);
				tv_right.setGravity(Gravity.RIGHT);
			}else{
				tv_left.setVisibility(View.VISIBLE);
				tv_left.setText(left);
			}

			String right = bundle.getString("right");
			if (StringUtil.isEmptyOrNull(right)){
				tv_right.setVisibility(View.GONE);
				tv_left.setGravity(Gravity.RIGHT);
			}else{
				tv_right.setVisibility(View.VISIBLE);
				tv_right.setText(right);
			}
			fromkey = bundle.getInt("fromkey");
			if (fromkey == GET_DEPOSIT_WH_ERROR){
				//提现乌行校验失败
				tv_title.setGravity(Gravity.LEFT);
			}
			tv_title.setText(title);
		}
	}

	/**
	 * 显示dialogFragment
	 * @param fragmentManager
	 * @return
	 */
	public DialogFragment showAllowingStateLoss(FragmentManager fragmentManager,String tag) {
		FragmentTransaction ft = fragmentManager.beginTransaction();
		ft.add(this, tag);
		ft.commitAllowingStateLoss();
		return this;
	}

	@Override public void onClick(View v) {
		int id = v.getId();
		dismissAllowingStateLoss();
		if (id == R.id.tv_left) {
			mListener.onLeft();
		} else if (id == R.id.tv_right) {
			// 忘记密码
			mListener.onRight();
		}
	}

	public interface InputPwdErrorListener {

		void onLeft();

		void onRight();
	}

	public void setListener(InputPwdErrorListener listener) {
		this.mListener = listener;
	}
}
