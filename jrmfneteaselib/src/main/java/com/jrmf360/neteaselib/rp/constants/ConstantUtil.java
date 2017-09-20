package com.jrmf360.neteaselib.rp.constants;

import com.jrmf360.neteaselib.JrmfClient;

/**
 * 网络接口和一些常量配置
 */
public interface ConstantUtil {

	String	JRMF_BASE_URL				= JrmfClient.getBaseUrl();												// 网络路径

	// String JRMF_PARTNER_URL = "api/v2/redEnvelope/"; // 原生改版

//	String	JRMF_PARTNER_URL			= "api/v4/redEnvelope/";												// 乌行H5

	String JRMF_PARTNER_URL = "app/v2/redEnvelope/yunxin/";                                        // 网易云信版


	String	H5_PROTOCOL_URL				= JRMF_BASE_URL + "static/protocol/agreement.html";						// 支付协议

	String	JRMF_OPEN_ENVELOPE_DETAIL	= JRMF_BASE_URL + "h5/v1/redEnvelope/briberyMoneyDetail.shtml";			// 打开红包详情页

	String	JRMF_JD_GET_CODE			= JRMF_BASE_URL + JRMF_PARTNER_URL + "jdpaySign.shtml";					// 京东支付获取验证码

	String	JRMF_SEND_SINGLE_ENVELOPES	= JRMF_BASE_URL + JRMF_PARTNER_URL + "single.shtml";					// 单聊发送红包

	String	JRMF_SEND_GROUP_ENVELOPES	= JRMF_BASE_URL + JRMF_PARTNER_URL + "group.shtml";						// 群聊发送红包

	String	JRMF_PAY					= JRMF_BASE_URL + JRMF_PARTNER_URL + "pay.shtml";						// 发送红包支付接口

	String	JRMF_SEND_CODE				= JRMF_BASE_URL + JRMF_PARTNER_URL + "vailCardSendCode.shtml";			// 绑定银行卡-发送验证码

	String	JRMF_BIND_CARD				= JRMF_BASE_URL + JRMF_PARTNER_URL + "bindCardVailCode.shtml";			// 绑定银行卡-提交
																												// （验证手机验证码）

	String	JRMF_BANK_LIST				= JRMF_BASE_URL + JRMF_PARTNER_URL + "banklist.shtml";					// 获得银行列表

	String	JRMF_VAIL_BANK_INFO			= JRMF_BASE_URL + JRMF_PARTNER_URL + "vailBankInfo.shtml";				// 根据卡号获取银行卡信息

	String	JRMF_UPDATE_USER_INFO		= JRMF_BASE_URL + JRMF_PARTNER_URL + "updateUserInfo.shtml";			// 修改交易密码发送验证码

	// 原生拆红包接口

	// 获得红包信息
	String	GET_RP_INFO					= JRMF_BASE_URL + JRMF_PARTNER_URL + "getRedInfo.shtml";

	// 实名认证
	String	CERTIFY_SEND_CODE						= JRMF_BASE_URL + JRMF_PARTNER_URL + "certifySendCode.shtml";
	//实名认证校验验证码
	String	CERTIFY_VALI_CODE						= JRMF_BASE_URL + JRMF_PARTNER_URL + "certifyValiCode.shtml";

	// 获得群红包信息
	String	GROUP_RP_INFO				= JRMF_BASE_URL + JRMF_PARTNER_URL + "groupGrab/index.shtml";

	// 获得单聊红包信息
	String	SINGLE_RP_INFO				= JRMF_BASE_URL + JRMF_PARTNER_URL + "singleGrab/index.shtml";

	// 抢群红包
	String	GRAB_GROUP_RP				= JRMF_BASE_URL + JRMF_PARTNER_URL + "groupGrab/grab.shtml";

	// 抢单聊红包
	String	GRAB_SINGLE_RP				= JRMF_BASE_URL + JRMF_PARTNER_URL + "singleGrab/grab.shtml";

	// 红包详情
	String	GRAP_DETAIL					= JRMF_BASE_URL + JRMF_PARTNER_URL + "grab/detail.shtml";

	// 红包详情详情加载下一页
	String	GRAP_DETAIL_BY_PAGE			= JRMF_BASE_URL + JRMF_PARTNER_URL + "grab/detailByPage.shtml";

	// 发出的红包
	String	SEND_RP						= JRMF_BASE_URL + JRMF_PARTNER_URL + "sendHistory.shtml";

	// 收到的红包
	String	RECEIVE_RP					= JRMF_BASE_URL + JRMF_PARTNER_URL + "receiveHistory.shtml";

	// 交易记录
	String	TRADE_HISTORY				= JRMF_BASE_URL + JRMF_PARTNER_URL + "tradeHistory.shtml";

	// ---------------------------------转账-----------------------------------------------------------------------------------------

	// 获得转账信息
	String	GET_TRANS_INFO				= JRMF_BASE_URL + "api/v2/transfer/" + "getTransferInfo.shtml";

	// 点击转账
	String	CONFIRM_TRANS_INFO			= JRMF_BASE_URL + "api/v2/transfer/" + "confirmTransferInfo.shtml";

	// 转账详情
	String	TRANS_DETAIL				= JRMF_BASE_URL + "api/v2/transfer/" + "transferDetail.shtml";

	// 转账退款
	String	TRANS_BACK					= JRMF_BASE_URL + "api/v2/transfer/" + "transferBack.shtml";

	// 转账-确认收钱
	String	TRANS_RECEIVE_MONEY			= JRMF_BASE_URL + "api/v2/transfer/" + "transferConfirm.shtml";

	// 转账-京东支付获取验证码
	String	JRMF_TRANS_JD_GET_CODE		= JRMF_BASE_URL + "api/v2/transfer/" + "jdpaySign.shtml";

	// 转账-付款
	String	JRMF_TRANS_PAY				= JRMF_BASE_URL + "api/v2/transfer/" + "pay.shtml";

	// ---------------------------------H5密码-----------------------------------------------------------------------------------------

	String	GET_H5_URL					= JRMF_BASE_URL + JRMF_PARTNER_URL + "getUccbPwdOperationUrl.shtml";	// 获得密码H5链接

	String	GET_H5_SEND_RP				= JRMF_BASE_URL + JRMF_PARTNER_URL + "getBalancePayRedPacketUrl.shtml";	// 获得发红包H5链接

	/**
	 * 支付方式
	 */
	int		PAY_TYPE_CHANGE				= 0;																	// 零钱支付

	int		PAY_TYPE_OLDCARD			= 1;																	// 银行卡支付

	int		PAY_TYPE_ALIPAY				= 2;																	// 支付宝支付

	int		PAY_TYPE_WECHAT				= 3;																	// 微信支付

	int		PAY_TYPE_NEWCARD			= 4;																	// 新卡支付

	int		PAY_TYPE_JD					= 5;																	// 京东支付，；理论上来讲以后不会出现paytype==1了

	String	A							= "caifu";

	/**
	 * 红包类型
	 */
	int		ENVELOPES_TYPE_SINGLE		= 2;																	// 个人红包

	int		ENVELOPES_TYPE_PIN			= 1;																	// 群拼手气红包

	int		ENVELOPES_TYPE_NORMAL		= 0;																	// 群普通红包

	/**
	 * 其他
	 */
	String	R_TargetId					= "TargetId";															// 融云-回话ID

	String	DES_KEY						= "yilu" + A;															// 加密

	String	ENVELOPES_TYPE				= "envelopestype";														// 红包类型

	String	AMOUNT						= "amount";																// 每个红包的金额

	String	NUMBER						= "number";																// 红包个数

	String	HAS_PWD						= "hasPwd";																// 是否需要校验交易密码

	String	RED_PACKET_NAME						= "redPacketName";																// 红包名字

	// 界面跳转
	int		FROM_SEND_SINGLE_RP			= 200;

	int		FROM_SEND_GROUP_RP			= 201;

	int		FROM_ADD_CARD_SEC			= 203;																	// 添加银行卡第二步

	// 通用传递参数
	String	JRMF_USER_ID				= "userId";

	String	JRMF_THIRD_TOKEN			= "thirdToken";

	String	JRMF_USER_NAME				= "userName";

	String	JRMF_REC_USER_NAME			= "recUserName";

	String	JRMF_USER_ICON				= "userIcon";

	String	JRMF_REC_USER_ICON			= "recUserIcon";

	String	GNUM						= "group_num";

}
