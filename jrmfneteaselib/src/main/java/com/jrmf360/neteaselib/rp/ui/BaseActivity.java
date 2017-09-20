package com.jrmf360.neteaselib.rp.ui;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.jrmf360.neteaselib.JrmfClient;
import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.fragment.LoadingDialogFragment;
import com.jrmf360.neteaselib.base.http.OkHttpWork;
import com.jrmf360.neteaselib.base.interfaces.UIInterface;
import com.jrmf360.neteaselib.base.manager.CusActivityManager;
import com.jrmf360.neteaselib.base.utils.LanguageUtil;
import com.jrmf360.neteaselib.base.utils.SPManager;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.utils.SystemBarTintManager;
import com.jrmf360.neteaselib.rp.constants.ConstantUtil;
import com.jrmf360.neteaselib.rp.widget.ActionBarView;


public abstract class BaseActivity extends FragmentActivity implements UIInterface, LoadingDialogFragment.LoadingDialogListener {

	public Activity			context;

	public ActionBarView	actionBarView;

	protected static String	userid, thirdToken;

	@Override protected final void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//设置竖屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(savedInstanceState);
		LanguageUtil.setLocale(this);
		setContentView(getLayoutId());
		CusActivityManager.getInstance().addActivity(this);
		context = this;

		// 获取通用参数
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			if (StringUtil.isNotEmpty(bundle.getString(ConstantUtil.JRMF_USER_ID))) {
				userid = bundle.getString(ConstantUtil.JRMF_USER_ID);
			}

			if (StringUtil.isNotEmpty(bundle.getString(ConstantUtil.JRMF_THIRD_TOKEN))) {
				thirdToken = bundle.getString(ConstantUtil.JRMF_THIRD_TOKEN);
			}
		}

		// 设置actionBar的颜色
		actionBarView = (ActionBarView) findViewById(R.id.actionbar);
		if (actionBarView != null){
			actionBarView.getIvBack().setImageDrawable(getResources().getDrawable(R.drawable.jrmf_rp_top_back));
		}

		// 状态栏着色-付款页面不着色
		if (JrmfClient.getIsTintStatusBar() && !(getClass().getSimpleName().equals("PActivity")) && !(getClass().getSimpleName().equals("OpenRpActivity"))
				&& !(getClass().getSimpleName().equals("TransPayActivity"))) {
			tintStatusBar();
		}
		initView();
		initListener();
		initData(bundle);
	}


	/**
	 * 保存用户id
	 */
	protected void saveUserId() {
		if (StringUtil.isNotEmpty(userid)) {
			SPManager.getInstance().putString(context, "userId", userid);
		}
	}

	protected void tintStatusBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintColor(getResources().getColor(R.color.jrmf_rp_status_bar));
		}
	}


	@Override public final void onClick(View v) {
		int id = v.getId();
		onClick(id);
	}

	@Override public void initView() {}

	@Override public void initListener() {}

	/**
	 * 初始化数据
	 * 
	 * @param bundle
	 */
	protected void initData(Bundle bundle) {}

	public void onClick(int id) {}

	/**
	 * 是否可点击
	 * 
	 * @param btn
	 * @param flag
	 */
	@TargetApi(Build.VERSION_CODES.CUPCAKE) public void setClickable(View btn, boolean flag) {
		if (flag) {
			btn.getBackground().mutate().setAlpha(255);// 0~255透明度值
			btn.setEnabled(true);
		} else {
			btn.getBackground().mutate().setAlpha(100);// 0~255透明度值
			btn.setEnabled(false);
		}
	}

	@Override protected void onDestroy() {
		super.onDestroy();
		// 关闭当前页面的网络请求
		OkHttpWork.getInstance().cancel(context);
		// 关闭当前的activity
		CusActivityManager.getInstance().finishActivity(this);
	}

	@Override public void onCancel() {
		OkHttpWork.getInstance().cancel(context);
	}
}
