package com.jrmf360.neteaselib.wallet.constants;

public interface Constants {

	// ---------------页面跳转的key--------------------
	int		FROM_ACCOUNT_INFO		= 0x01;			// 账户信息页面

	int		FROM_SECURE_SETTING		= 0x02;			// 安全设置页面

	int		FROM_PWD_CHECK			= 0x03;			// 密码校验页面

	int		FROM_IDENTITY_AUTHEN	= 0x04;			// 实名认证页面

	int		FROM_ADD_CARD_SEC		= 0x05;			// 添加银行卡第二步页面

	int		FROM_GET_PWD			= 0x06;			// 找回支付密码页面

	int		FROM_GET_DEPOSIT		= 0x07;			// 提现页面

	int		FROM_BANK_SETTING		= 0x08;			// 银行卡设置页面

	// --------------跳转到webView的key-------------------
	int		USER_PROTOCOL			= 0X110;

	/******************** 时间相关常量 ********************/
	/**
	 * 毫秒与毫秒的倍数
	 */
	int		MSEC					= 1;

	/**
	 * 秒与毫秒的倍数
	 */
	int		SEC						= 1000;

	/**
	 * 分与毫秒的倍数
	 */
	int		MIN						= 60000;

	/**
	 * 时与毫秒的倍数
	 */
	int		HOUR					= 3600000;

	/**
	 * 天与毫秒的倍数
	 */
	int		DAY						= 86400000;

	/**
	 * 支付方式
	 */
	int		PAY_TYPE_CHANGE			= 0;			// 零钱支付

	// int PAY_TYPE_OLDCARD = 1;// 银行卡支付

	int		PAY_TYPE_ALIPAY			= 2;			// 支付宝支付

	int		PAY_TYPE_WECHAT			= 3;			// 微信支付

	int		PAY_TYPE_NEWCARD		= 4;			// 新卡支付

	int		PAY_TYPE_JD				= 5;			// 京东支付，；理论上来讲以后不会出现paytype==1了

	String	IS_HSA_PWD				= "isHasPwd";
}
