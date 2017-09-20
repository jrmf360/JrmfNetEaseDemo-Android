package com.jrmf360.neteaselib.rp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.rp.fragment.RedPacketHistoryFragment;


/**
 * 我的红包页面
 * 
 * @author honglin
 */
public class MyRpActivity extends BaseActivity {

	/**
	 * Tab的那个引导线
	 */
	private ImageView					id_tab_right_line, id_tab_left_line;

	private LinearLayout				ll_in, ll_out;

	private TextView					tv_in, tv_out;

	private Fragment					currFragment;

	private RedPacketHistoryFragment inFragment;

	private RedPacketHistoryFragment	outFragment;

	private String userId,thirdToken;

	public static void intent(Activity fromActivity, String userId, String thirdToken) {
		Intent intent = new Intent(fromActivity, MyRpActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("userId", userId);
		bundle.putString("thirdToken", thirdToken);
		intent.putExtras(bundle);
		fromActivity.startActivity(intent);
	}

	@Override public int getLayoutId() {
		return R.layout.jrmf_rp_activity_my_rp;
	}

	@Override public void initView() {
		ll_in = (LinearLayout) findViewById(R.id.ll_in);
		ll_out = (LinearLayout) findViewById(R.id.ll_out);
		tv_in = (TextView) findViewById(R.id.tv_in);
		tv_out = (TextView) findViewById(R.id.tv_out);
		id_tab_left_line = (ImageView) findViewById(R.id.id_tab_left_line);
		id_tab_right_line = (ImageView) findViewById(R.id.id_tab_right_line);
	}

	@Override public void initListener() {
		actionBarView.getIvBack().setOnClickListener(this);
		ll_in.setOnClickListener(this);
		ll_out.setOnClickListener(this);
	}

	@Override protected void initData(Bundle bundle) {
		actionBarView.setTitle(getString(R.string.jrmf_rp_my_rp));

		if (bundle != null) {
			thirdToken = bundle.getString("thirdToken");
			userId = bundle.getString("userId");
		}
		initContent(inFragment = getFragment(0));
	}

	@Override public void onClick(int id) {
		if (id == R.id.ll_in) {
			// 收到的红包
			if (inFragment == null) {
				inFragment = getFragment(0);
			}
			switchContent(inFragment);
			setUi(0);
		} else if (id == R.id.ll_out) {
			// 发出的红包
			if (outFragment == null) {
				outFragment = getFragment(1);
			}
			switchContent(outFragment);
			setUi(1);
		} else if (id == R.id.iv_back) {
			finish();
		}
	}

	private void setUi(int index) {
		if (index == 0) {
			// 收到的红包
			tv_in.setTextColor(getResources().getColor(R.color.jrmf_rp_title_bar));
			tv_out.setTextColor(getResources().getColor(R.color.color_959595));
			id_tab_left_line.setVisibility(View.VISIBLE);
			id_tab_right_line.setVisibility(View.INVISIBLE);
		} else {
			tv_in.setTextColor(getResources().getColor(R.color.color_959595));
			tv_out.setTextColor(getResources().getColor(R.color.jrmf_rp_title_bar));
			id_tab_left_line.setVisibility(View.INVISIBLE);
			id_tab_right_line.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 获得fragment
	 * 
	 * @param index
	 * @return
	 */
	private RedPacketHistoryFragment getFragment(int index) {
		RedPacketHistoryFragment fragment = new RedPacketHistoryFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("index", index);
		bundle.putString("thirdToken", thirdToken);
		bundle.putString("userId", userId);
		fragment.setArguments(bundle);
		return fragment;
	}

	/**
	 * 初始化要显示的fragment
	 * 
	 * @param fragment
	 */
	private void initContent(Fragment fragment) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.fl_container, fragment).commit();
		currFragment = fragment;
		setUi(0);
	}

	/** 修改显示的内容 不会重新加载 **/
	private void switchContent(Fragment to) {
		if (currFragment != to) {
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			if (!to.isAdded()) { // 先判断是否被add过
				transaction.hide(currFragment).add(R.id.fl_container, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
			} else {
				transaction.hide(currFragment).show(to).commit(); // 隐藏当前的fragment，显示下一个
			}
			currFragment = to;
		}
	}
}
