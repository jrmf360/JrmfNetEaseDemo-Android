package com.jrmf360.neteaselib.wallet.http;

import com.jrmf360.neteaselib.JrmfClient;

/**
 * 请求的网络路径
 * 
 * @author honglin
 */
public interface HttpUrl {

	String	JRMF_BASE_URL			= JrmfClient.getBaseUrl();										// 网络路径
	// 正式

//	String	BRANCH					= "api/v5/wallet/";

	String	BRANCH					= "app/v2/wallet/yunxin/";                                     //网易云信

	// 我的钱包页面
	String	MY_WALLET				= JRMF_BASE_URL + BRANCH + "index.shtml";

	// 充值
	String	CHARGE_INFO				= JRMF_BASE_URL + BRANCH + "account/chargeInfo.shtml";

	// 充值 请求验证码
	String	CHARGE_REQ_CODE			= JRMF_BASE_URL + BRANCH + "account/chargeSendCode.shtml";

	// 充值 验证用户输入的验证码
	String	CHARGE_VERIFY_CODE		= JRMF_BASE_URL + BRANCH + "account/chargeValiCode.shtml";

	String	W						= "caifu";

	String	H5_PROTOCOL_URL			= JRMF_BASE_URL + "static/protocol/agreement.html";				// 支付协议

	// 提现 获得信息
	String	REDEEN_INFO				= JRMF_BASE_URL + BRANCH + "account/redeemInfo.shtml";

	// 提现
	String	REDEEN					= JRMF_BASE_URL + BRANCH + "account/redeem.shtml";

	// 绑提现银行卡
	String	BIND_REDEEM_CARD		= JRMF_BASE_URL + BRANCH + "bindRedeemCard.shtml";

	// 绑卡－第一步 验证银行卡号码
	String	VERIFY_BANK_INFO		= JRMF_BASE_URL + BRANCH + "bindCard/vailBankInfo.shtml";

	// 银行卡设置－我的银行卡列表
	String	MY_CARD_LIST			= JRMF_BASE_URL + BRANCH + "account/myAccountList.shtml";

	// 银行卡设置－我的银行卡列表
	String	MY_SUPPORT_CARD_LIST	= JRMF_BASE_URL + BRANCH + "banklist.shtml";

	// 安全设置
	String	SECURE_SETTING			= JRMF_BASE_URL + BRANCH + "sc/index.shtml";

	// 绑定银行卡请求验证码
	String	BIND_CARD_REQ_CODE		= JRMF_BASE_URL + BRANCH + "bindCard/vailCardSendCode.shtml";

	// 绑定银行卡
	String	BIND_CARD				= JRMF_BASE_URL + BRANCH + "bindCard/vailCardVailCode.shtml";

	// 实名认证发送验证码
	String	CERTIFY_SEND_CODE		= JRMF_BASE_URL + BRANCH + "certifySendCode.shtml";

	// 实名认证校验验证码
	String	CERTIFY_VALI_CODE		= JRMF_BASE_URL + BRANCH + "certifyValiCode.shtml";

	// 验证身份信息
	String	UNBIND_CARD				= JRMF_BASE_URL + BRANCH + "account/unBinding.shtml";

	// 收到的红包
	String	RECEIVE_RP				= JRMF_BASE_URL + BRANCH + "redEnvelope/receiveHistory.shtml";

	// 发出的红包
	String	SEND_RP					= JRMF_BASE_URL + BRANCH + "redEnvelope/sendHistory.shtml";

	// 交易记录
	String	TRADE_HISTORY			= JRMF_BASE_URL + BRANCH + "account/tradeHistory.shtml";

	// 钱包-跳转到红包详情页
	String	RED_DETAIL				= JRMF_BASE_URL + BRANCH + "redDetai.shtml";

	// 获得账户信息
	String	GET_ACCOUNT_INFO		= JRMF_BASE_URL + BRANCH + "unCenter/index.shtml";

	// 验证身份信息
	String	CHECK_IDENTITY			= JRMF_BASE_URL + BRANCH + "sc/findTranPwdByInfo.shtml";

	// 设置交易支付密码
	String	SET_TRADE_PWD			= JRMF_BASE_URL + BRANCH + "bindCard/setTranPwd.shtml";

	// 通过valiToken设置交易密码
	String	SET_TRADE_BY_TOKEN		= JRMF_BASE_URL + BRANCH + "sc/setTranPwd.shtml";

	// 更新用户身份信息
	String	UPDATE_USER_INFO		= JRMF_BASE_URL + BRANCH + "unCenter/updateUserInfo.shtml";

	// 保存用户信息－完成假实名
	String	SAVE_USER_INFO			= JRMF_BASE_URL + BRANCH + "sc/saveUserInfo.shtml";

	// 找回密码－请求验证码
	String	GET_PWD_REQ_CODE		= JRMF_BASE_URL + BRANCH + "sc/findTranPwdSendCode.shtml";

	// 红包详情
	String	GET_PWD_BY_CODE			= JRMF_BASE_URL + BRANCH + "sc/findTranPwdvaliCode.shtml";

	// 验证交易密码
	String	CHECK_PWD				= JRMF_BASE_URL + BRANCH + "account/valiTranPwd.shtml";

	// 根据省获得市
	String	GET_CITIES				= JRMF_BASE_URL + BRANCH + "cityList.shtml";

	// 根据省市获得支行
	String	GET_BRANCH				= JRMF_BASE_URL + BRANCH + "subBranchInfo.shtml";

	// ---------------------------------H5密码-----------------------------------------------------------------------------------------

	String	GET_H5_URL				= JRMF_BASE_URL + BRANCH + "getUccbPwdOperationUrl.shtml";		// 获得密码H5链接

	String	A						= "yilu" + W;													// 加密

}
