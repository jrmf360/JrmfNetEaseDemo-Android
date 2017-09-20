package com.jrmf360.neteaselib.wallet;

import android.app.Activity;

import com.jrmf360.neteaselib.base.utils.SPManager;
import com.jrmf360.neteaselib.wallet.ui.MyWalletActivity;

/**
 * @创建人 honglin
 * @创建时间 16/11/15 上午9:23
 * @类描述 用来给用户初始化调用
 */
public class JrmfWalletClient {

	/**
	 * 跳转到我的钱包页面
	 * 
	 * @param fromActivity
	 *            [必传]从那个activity跳过来
	 * @param userId
	 *            [必传]用户id
	 * @param thirdToken
	 *            [必传]第三方的签名,需要用户自己传递过来
	 * @param username
	 *            [可为空]用户名字
	 * @param usericon
	 *            [可为空]用户头像地址(网络地址)
	 */
	public static void intentWallet(Activity fromActivity, String userId, String thirdToken, String username, String usericon) {
		MyWalletActivity.intent(fromActivity, userId, thirdToken, username, usericon);
		SPManager.getInstance().putString(fromActivity, "userId", userId);
	}
}
