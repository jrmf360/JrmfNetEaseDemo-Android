package com.jrmf360.neteaselib.wallet.manager;

import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.manager.CusActivityManager;
import com.jrmf360.neteaselib.wallet.ui.AccountInfoActivity;
import com.jrmf360.neteaselib.wallet.ui.BankSettingActivity;
import com.jrmf360.neteaselib.wallet.ui.GetDepositActivity;
import com.jrmf360.neteaselib.wallet.ui.RechargeActivity;
import com.jrmf360.neteaselib.wallet.ui.SecureSettingActivity;

/**
 * 刷新页面
 * @author honglin
 *
 */
public class RefreshManager {
	private static RefreshManager instance = null;

	private RefreshManager() {
	}

	public static RefreshManager getInstance() {
		synchronized (DialogDisplay.class) {
			if (instance == null) {
				instance = new RefreshManager();
			}
		}

		return instance;
	}

	/**
	 * 刷新带有银行卡的页面
	 * 动态参数
	 */
	public void refreshBankPage(){
		//需要刷新充值页面，提现页面，主页面，银行卡设置页面,账户信息页面,安全设置页面
		RechargeActivity rechargeActivity = CusActivityManager.getInstance().findActivity(RechargeActivity.class);
		if (rechargeActivity != null) {
			rechargeActivity.refresh();
		}

		GetDepositActivity getDepositActivity = CusActivityManager.getInstance().findActivity(GetDepositActivity.class);
		if (getDepositActivity != null) {
			getDepositActivity.loadInfo();
		}

		BankSettingActivity bankSettingActivity = CusActivityManager.getInstance().findActivity(BankSettingActivity.class);
		if (bankSettingActivity != null) {
			bankSettingActivity.refresh();
		}

		AccountInfoActivity accountInfoActivity = CusActivityManager.getInstance().findActivity(AccountInfoActivity.class);
		if (accountInfoActivity != null){
			accountInfoActivity.refresh();
		}

		SecureSettingActivity secureSettingActivity = CusActivityManager.getInstance().findActivity(SecureSettingActivity.class);
		if (secureSettingActivity != null){
			secureSettingActivity.refresh();
		}
	}
}




















