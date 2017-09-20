package com.jrmf360.neteaselib.wallet.ui;

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
import com.jrmf360.neteaselib.base.utils.SystemBarTintManager;
import com.jrmf360.neteaselib.base.view.TitleBar;

public abstract class BaseActivity extends FragmentActivity implements UIInterface,LoadingDialogFragment.LoadingDialogListener{

	protected FragmentActivity context;
	protected TitleBar titleBar;
	/**
	 * 该方法定义为final，以免子类覆盖此方法
	 *
	 * @param savedInstanceState
	 */
	@Override protected final void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(savedInstanceState);
		CusActivityManager.getInstance().addActivity(this);
		setContentView(getLayoutId());
		context = this;
		// 状态栏着色-付款页面不着色
		String simpleName = getClass().getSimpleName();
		if (JrmfClient.getIsTintStatusBar()) {
			tintStatusBar(simpleName);
		}
		initView();
		initListener();
		Bundle bundle = getIntent().getExtras();
		initData(bundle);
	}

	protected void tintStatusBar(String className) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);
			if ("MyRpActivity".equals(className)){
				tintManager.setStatusBarTintColor(getResources().getColor(R.color.jrmf_rp_status_bar));
			}else{
				tintManager.setStatusBarTintColor(getResources().getColor(R.color.jrmf_w_status_bar_color));
			}
		}
	}

	@Override
	public void initView() {}

	@Override 
	public void initListener() {}

	/**
	 * 初始化数据
	 *
	 * @param bundle
	 */
	protected void initData(Bundle bundle){};

	@Override 
	public void onClick(View view) {}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//关闭当前页面的网络请求
		OkHttpWork.getInstance().cancel(context);
		//关闭当前的activity
		CusActivityManager.getInstance().finishActivity(this);
	}

	@Override
	public void onCancel() {
		OkHttpWork.getInstance().cancel(context);
	}
}
