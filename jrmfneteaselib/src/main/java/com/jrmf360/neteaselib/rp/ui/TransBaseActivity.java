package com.jrmf360.neteaselib.rp.ui;

import android.os.Build;
import android.view.WindowManager;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.utils.SystemBarTintManager;

/**
 * @创建人 honglin
 * @创建时间 2017/3/29 下午3:00
 * @类描述 一句话描述 你的UI
 */
public abstract class TransBaseActivity extends BaseActivity {

    @Override
	protected void tintStatusBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintColor(getResources().getColor(R.color.jrmf_rp_trans_status_bar));
		}
	}
}
