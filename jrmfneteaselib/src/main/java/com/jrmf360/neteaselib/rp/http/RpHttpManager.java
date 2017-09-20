package com.jrmf360.neteaselib.rp.http;

import android.content.Context;

import com.jrmf360.neteaselib.JrmfClient;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.http.OkHttpWork;
import com.jrmf360.neteaselib.base.model.BaseModel;
import com.jrmf360.neteaselib.base.utils.Base64;
import com.jrmf360.neteaselib.base.utils.DesUtil;
import com.jrmf360.neteaselib.base.utils.DeviceUUIDUtil;
import com.jrmf360.neteaselib.base.utils.SPManager;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.utils.ThreadUtil;
import com.jrmf360.neteaselib.rp.constants.ConstantUtil;
import com.jrmf360.neteaselib.rp.http.model.BankResModel;
import com.jrmf360.neteaselib.rp.http.model.BindCardModel;
import com.jrmf360.neteaselib.rp.http.model.CodeModel;
import com.jrmf360.neteaselib.rp.http.model.InitRpInfo;
import com.jrmf360.neteaselib.rp.http.model.PayResModel;
import com.jrmf360.neteaselib.rp.http.model.ReceiveRpModel;
import com.jrmf360.neteaselib.rp.http.model.RedEnvelopeModel;
import com.jrmf360.neteaselib.rp.http.model.RpInfoModel;
import com.jrmf360.neteaselib.rp.http.model.SendRpModel;
import com.jrmf360.neteaselib.rp.http.model.SubmitCardResModel;
import com.jrmf360.neteaselib.rp.http.model.TradeDetailModel;
import com.jrmf360.neteaselib.rp.http.model.TransDetailModel;
import com.jrmf360.neteaselib.rp.http.model.TransInfoModel;
import com.jrmf360.neteaselib.rp.http.model.TransModel;
import com.jrmf360.neteaselib.rp.http.model.UrlModel;
import com.jrmf360.neteaselib.rp.utils.RequireInfoUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/1.
 */
public class RpHttpManager {

	/**
	 * 获得红包信息
	 *
	 * @param fromkey
	 * @param custUid
	 * @param thirdToken
	 * @param envelopeId
	 * @param username
	 * @param usericon
	 * @param callback
	 */
	public static void groupRpInfo(Context context, final int fromkey, String custUid, String thirdToken, String envelopeId, String username, String usericon,
			final OkHttpModelCallBack<RpInfoModel> callback) {

		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("envelopeId", envelopeId);
		if (StringUtil.isNotEmpty(username)) {
			requestParamsMap.put("nickName", username);
		}
		if (StringUtil.isNotEmpty(usericon) && usericon.startsWith("http")) {
			String enAvatar = Base64.encode(usericon.getBytes());
			requestParamsMap.put("avatarUrl", enAvatar);
		}
		String url = fromkey == 0 ? ConstantUtil.GROUP_RP_INFO : ConstantUtil.SINGLE_RP_INFO;
		OkHttpWork.getInstance().post(context, url, requestParamsMap, callback);

	}

	/**
	 * 抢群红包
	 *
	 * @param custUid
	 * @param thirdToken
	 * @param envelopeId
	 * @param username
	 * @param usericon
	 * @param callback
	 */
	public static void grabGroupRp(Context context, String custUid, String thirdToken, final int key, String envelopeId, String username, String usericon,
			final OkHttpModelCallBack<RpInfoModel> callback) {

		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("envelopeId", envelopeId);
		if (StringUtil.isNotEmpty(username)) {
			requestParamsMap.put("nickName", username);
		}
		if (StringUtil.isNotEmpty(usericon) && usericon.startsWith("http")) {
			String enAvatar = Base64.encode(usericon.getBytes());
			requestParamsMap.put("avatar", enAvatar);
		}
		String url = key == 0 ? ConstantUtil.GRAB_GROUP_RP : ConstantUtil.GRAB_SINGLE_RP;
		OkHttpWork.getInstance().post(context, url, requestParamsMap, callback);
	}

	/**
	 * 红包详情
	 *
	 * @param custUid
	 * @param thirdToken
	 * @param envelopeId
	 * @param username
	 * @param usericon
	 * @param callback
	 */
	public static void rpDetail(Context context, String custUid, String thirdToken, String envelopeId, String username, String usericon, final OkHttpModelCallBack<RpInfoModel> callback) {

		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("envelopeId", envelopeId);
		if (StringUtil.isNotEmpty(username)) {
			requestParamsMap.put("nickName", username);
		}
		if (StringUtil.isNotEmpty(usericon) && usericon.startsWith("http")) {
			String enAvatar = Base64.encode(usericon.getBytes());
			requestParamsMap.put("avatar", enAvatar);
		}
		OkHttpWork.getInstance().post(context, ConstantUtil.GRAP_DETAIL, requestParamsMap, callback);
	}

	/**
	 * 红包详情
	 *
	 * @param custUid
	 * @param thirdToken
	 * @param envelopeId
	 * @param username
	 * @param usericon
	 * @param callback
	 */
	public static void loadNextPage(Context context, String custUid, String thirdToken, String envelopeId, String username, String usericon, int page,
			final OkHttpModelCallBack<RpInfoModel> callback) {

		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("envelopeId", envelopeId);
		requestParamsMap.put("page", page);
		if (StringUtil.isNotEmpty(username)) {
			requestParamsMap.put("nickName", username);
		}
		if (StringUtil.isNotEmpty(usericon) && usericon.startsWith("http")) {
			String enAvatar = Base64.encode(usericon.getBytes());
			requestParamsMap.put("avatar", enAvatar);
		}
		OkHttpWork.getInstance().post(context, ConstantUtil.GRAP_DETAIL_BY_PAGE, requestParamsMap, callback);
	}

	/**
	 * 加载发出的红包
	 *
	 * @param custUid
	 * @param thirdToken
	 * @param page
	 * @param pageSize
	 * @param callBack
	 */
	public static void loadSendRpInfo(Context context, String custUid, String thirdToken, int page, int pageSize, final OkHttpModelCallBack<SendRpModel> callBack) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("page", page);
		requestParamsMap.put("pageSize", pageSize);
		OkHttpWork.getInstance().post(context, ConstantUtil.SEND_RP, requestParamsMap, callBack);
	}

	/**
	 * 加载收到的红包
	 *
	 * @param custUid
	 * @param thirdToken
	 * @param page
	 * @param pageSize
	 * @param callBack
	 */
	public static void loadReceiveRpInfo(Context context, String custUid, String thirdToken, int page, int pageSize, final OkHttpModelCallBack<ReceiveRpModel> callBack) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("page", page);
		requestParamsMap.put("pageSize", pageSize);
		OkHttpWork.getInstance().post(context, ConstantUtil.RECEIVE_RP, requestParamsMap, callBack);
	}

	/**
	 * 交易记录
	 *
	 * @param custUid
	 * @param thirdToken
	 * @param dateType
	 * @param page
	 * @param callBack
	 */
	public static void tradeHistory(Context context, String custUid, String thirdToken, int dateType, int page, final OkHttpModelCallBack<TradeDetailModel> callBack) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("dateType", dateType);
		requestParamsMap.put("page", page);
		OkHttpWork.getInstance().post(context, ConstantUtil.TRADE_HISTORY, requestParamsMap, callBack);
	}

	/**
	 * 9 单聊发送红包
	 *
	 * @param thirdToken
	 * @param money
	 *            红包金额
	 * @param summary
	 *            红包描述
	 * @param receiveUserid
	 *            接受者ID
	 * @return
	 */
	public static void sendSinglePeak(Context context, String custUid, String thirdToken,
									  String money, String summary, String receiveUserid, String envelopeName,
									  final OkHttpModelCallBack<RedEnvelopeModel> callback) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("envelopeName",envelopeName);
		requestParamsMap.put("receiveUserid", receiveUserid);
		requestParamsMap.put("summary", summary);
		BigDecimal bd = new BigDecimal(money);
		bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP); // 设置精度，以及舍入规则
		int moneyInt = (int) (bd.floatValue() * 100);
		requestParamsMap.put("money", String.valueOf(moneyInt));
		OkHttpWork.getInstance().post(context, ConstantUtil.JRMF_SEND_SINGLE_ENVELOPES, requestParamsMap, callback);
	}

	/**
	 * 群聊发送红包
	 *
	 * @param thirdToken
	 *            用户令牌
	 * @param number
	 *            红包个数
	 * @param money
	 *            红包金额（输入金额）
	 * @param summary
	 *            红包描述
	 * @param redEnvelopeType
	 *            红包类型： 1-拼手气，0-普通红包
	 * @param groupId
	 *            接收者ID
	 * @return
	 */
	public static void sendGroupPeak(final Context context, String custUid, String thirdToken,
									 String number, String money, String summary, int redEnvelopeType,
									 String envelopeName, String groupId,
									 final OkHttpModelCallBack<RedEnvelopeModel> callback) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("envelopeName",envelopeName);
		requestParamsMap.put("number", number);
		requestParamsMap.put("groupId", groupId);
		requestParamsMap.put("summary", summary);
		requestParamsMap.put("redEnvelopeType", String.valueOf(redEnvelopeType));
		BigDecimal bd = new BigDecimal(money);
		bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP); // 设置精度，以及舍入规则
		int moneyInt = (int) (bd.floatValue() * 100);
		requestParamsMap.put("money", String.valueOf(moneyInt));
		OkHttpWork.getInstance().post(context, ConstantUtil.JRMF_SEND_GROUP_ENVELOPES, requestParamsMap, callback);

	}

	/**
	 * 发红包之前获得红包信息
	 *
	 * @param custUid
	 * @param thirdToken
	 * @param nickName
	 * @param avatarUrl
	 * @param callback
	 */
	public static void getRpInfo(final Context context, String custUid, String thirdToken, String nickName, String avatarUrl, final OkHttpModelCallBack<InitRpInfo> callback) {

		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		if (StringUtil.isNotEmpty(nickName)) {
			requestParamsMap.put("nickName", nickName);
		}

		if (StringUtil.isNotEmpty(avatarUrl) && avatarUrl.startsWith("http")) {
			String enAvatar = Base64.encode(avatarUrl.getBytes());
			requestParamsMap.put("avatarUrl", enAvatar);
		}

		OkHttpWork.getInstance().post(context, ConstantUtil.GET_RP_INFO, requestParamsMap, callback);
	}

	/**
	 * 实名认证
	 * @param context
	 * @param custUid
	 * @param thirdToken
	 * @param identityNo
	 * @param realName
	 * @param callback
	 */
//	public static void certify(final Context context, String custUid, String thirdToken, String identityNo, String realName, final OkHttpModelCallBack<BaseModel> callback) {
//		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
//		requestParamsMap.put("custUid", custUid);
//		requestParamsMap.put("thirdToken", thirdToken);
//		requestParamsMap.put("identityNo", identityNo);
//		requestParamsMap.put("realName", realName);
//		OkHttpWork.getInstance().post(context, ConstantUtil.CERTIFY, requestParamsMap, callback);
//	}


	/**
	 * 实名认证发送验证码
	 * @param context
	 * @param custUid
	 * @param thirdToken
	 * @param identityNo
	 * @param realName
	 * @param mobileNo
	 * @param callback
	 */
	public static void certifySendCode(final Context context, String custUid, String thirdToken,
									   String identityNo, String realName,String mobileNo, final OkHttpModelCallBack<CodeModel> callback) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("identityNo", identityNo);
		requestParamsMap.put("realName", realName);
		requestParamsMap.put("mobileNo", mobileNo);
		OkHttpWork.getInstance().post(context, ConstantUtil.CERTIFY_SEND_CODE, requestParamsMap, callback);
	}


	/**
	 * 实名认证校验验证码
	 * @param context
	 * @param custUid
	 * @param thirdToken
	 * @param mobileToken
	 * @param callback
	 */
	public static void certifyValiCode(final Context context, String custUid, String thirdToken,
									   String mobileToken,String code,final OkHttpModelCallBack<BaseModel> callback) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("mobileToken", mobileToken);
		requestParamsMap.put("code", code);
		OkHttpWork.getInstance().post(context, ConstantUtil.CERTIFY_VALI_CODE, requestParamsMap, callback);
	}

	/**
	 * 红包支付接口
	 *
	 * @param thirdToken
	 * @param paytype
	 *            支付方式
	 * @param envelopeId
	 *            红包ID
	 * @param bankCardNo
	 *            用户ID
	 * @param pswd_or_tradeCode
	 *            零钱支付表示密码，京东支付表示手机验证码
	 * @return
	 */
	public static void exePay(Context context, String custUid, String thirdToken, int paytype, String envelopeId, String bankCardNo, String pswd_or_tradeCode, String trade_id,
			OkHttpModelCallBack<PayResModel> callBack) {
		Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("envelopeId", envelopeId);
		requestParamsMap.put("bankCardNo", bankCardNo);
		if (paytype == ConstantUtil.PAY_TYPE_OLDCARD) {
			// 京东支付
			requestParamsMap.put("paytype", String.valueOf(ConstantUtil.PAY_TYPE_JD));
			requestParamsMap.put("trade_id", trade_id);
			requestParamsMap.put("trade_code", pswd_or_tradeCode);
		} else {
			requestParamsMap.put("paytype", String.valueOf(paytype));
			requestParamsMap.put("aesTranPwd", DesUtil.encrypt(pswd_or_tradeCode, ConstantUtil.DES_KEY));
		}
		OkHttpWork.getInstance().post(context, ConstantUtil.JRMF_PAY, requestParamsMap, callBack);
	}

	/**
	 * 刷新红包状态
	 * @param context
	 * @param custUid
	 * @param thirdToken
	 * @param envelopeId
	 * @param callBack
	 */
	public static void refreshRedStatus(Context context, String custUid, String thirdToken,
										String envelopeId, OkHttpModelCallBack<BaseModel> callBack) {
		Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("envelopeId", envelopeId);
		OkHttpWork.getInstance().post(context, ConstantUtil.JRMF_PAY, requestParamsMap, callBack);
	}

	/**
	 * 转账支付接口和红包支付接口地址一样
	 *
	 * @param thirdToken
	 * @param paytype
	 *            支付方式
	 * @param transferOrderNo
	 *            红包ID
	 * @param bankCardNo
	 *            用户ID
	 * @param pswd_or_tradeCode
	 *            零钱支付表示密码，京东支付表示手机验证码
	 * @return
	 */
	public static void transPay(Context context, String custUid, String thirdToken, int paytype, String transferOrderNo, String bankCardNo, String pswd_or_tradeCode, String trade_id,
			OkHttpModelCallBack<PayResModel> callBack) {
		Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("transferOrderNo", transferOrderNo);
		requestParamsMap.put("bankCardNo", bankCardNo);
		String res;
		if (paytype == ConstantUtil.PAY_TYPE_OLDCARD) {
			// 京东支付
			requestParamsMap.put("paytype", String.valueOf(ConstantUtil.PAY_TYPE_JD));
			requestParamsMap.put("trade_id", trade_id);
			requestParamsMap.put("trade_code", pswd_or_tradeCode);
		} else {
			requestParamsMap.put("paytype", String.valueOf(paytype));
			requestParamsMap.put("aesTranPwd", DesUtil.encrypt(pswd_or_tradeCode, ConstantUtil.DES_KEY));
		}
		OkHttpWork.getInstance().post(context, ConstantUtil.JRMF_TRANS_PAY, requestParamsMap, callBack);
	}

	/**
	 * 获得转账信息
	 * @param context
	 * @param custUid
	 * @param nickName
	 * @param avatarUrl
	 * @param thirdToken
	 * @param recCustUid
	 * @param recCustName
	 * @param recCustAvatar
	 * @param callback
	 */
	public static void getTransInfo(Context context, String custUid, String nickName,String avatarUrl,String thirdToken,
									String recCustUid, String recCustName,String recCustAvatar,final OkHttpModelCallBack<TransInfoModel> callback) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("custUid", custUid);
		if (StringUtil.isNotEmpty(nickName)) {
			requestParamsMap.put("nickName", nickName);
		}
		if (StringUtil.isNotEmpty(avatarUrl) && avatarUrl.startsWith("http")) {
			String enAvatar = Base64.encode(avatarUrl.getBytes());
			requestParamsMap.put("avatarUrl", enAvatar);
		}

		requestParamsMap.put("recCustUid", recCustUid);
		if (StringUtil.isNotEmpty(recCustName)) {
			requestParamsMap.put("recCustName", recCustName);
		}
		if (StringUtil.isNotEmpty(recCustAvatar) && recCustAvatar.startsWith("http")) {
			String enAvatar = Base64.encode(recCustAvatar.getBytes());
			requestParamsMap.put("recCustAvatar", enAvatar);
		}
		OkHttpWork.getInstance().post(context, ConstantUtil.GET_TRANS_INFO, requestParamsMap, callback);
	}

	/**
	 * 点击转账获得信息
	 * 
	 * @param custUid
	 * @param thirdToken
	 * @param callback
	 */
	public static void confirmTransInfo(Context context, String custUid, String thirdToken, String recCustUid, String amount, String transferDesc, final OkHttpModelCallBack<TransModel> callback) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("recCustUid", recCustUid);
		requestParamsMap.put("transferDesc", transferDesc);
		float v = StringUtil.formatMoneyFloat(amount);
		requestParamsMap.put("amount", (int) (v * 100));
		OkHttpWork.getInstance().post(context, ConstantUtil.CONFIRM_TRANS_INFO, requestParamsMap, callback);

	}

	/**
	 * 获得转账详情
	 * 
	 * @param custUid
	 * @param thirdToken
	 * @param callback
	 */
	public static void getTransDetail(Context context, String custUid, String thirdToken, String transferOrderNo, final OkHttpModelCallBack<TransDetailModel> callback) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("transferOrderNo", transferOrderNo);
		OkHttpWork.getInstance().post(context, ConstantUtil.TRANS_DETAIL, requestParamsMap, callback);
	}

	/**
	 * 转账-确认收钱
	 * 
	 * @param custUid
	 * @param thirdToken
	 * @param callback
	 */
	public static void transReceiveMoney(Context context, String custUid, String thirdToken, String transferOrderNo, final OkHttpModelCallBack<BaseModel> callback) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("transferOrderNo", transferOrderNo);
		OkHttpWork.getInstance().post(context, ConstantUtil.TRANS_RECEIVE_MONEY, requestParamsMap, callback);
	}

	/**
	 * 转账-退钱
	 * 
	 * @param custUid
	 * @param thirdToken
	 * @param callback
	 */
	public static void transBack(Context context, String custUid, String thirdToken, String transferOrderNo, final OkHttpModelCallBack<BaseModel> callback) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("transferOrderNo", transferOrderNo);
		OkHttpWork.getInstance().post(context, ConstantUtil.TRANS_BACK, requestParamsMap, callback);

	}

	/**
	 * 绑定银行卡-发送验证码
	 *
	 * @param thirdToken
	 * @param realName
	 *            真实姓名
	 * @param identityNo
	 *            身份证号
	 * @param bankNo
	 *            银行ID
	 * @param bankCardNo
	 *            银行卡号
	 * @param mobileNo
	 *            预留手机号
	 * @return
	 */
	public static void sendCode(Context context, String custUid, String thirdToken, String realName, String identityNo, String bankNo, String bankCardNo, String mobileNo,
			OkHttpModelCallBack<CodeModel> callBack) {


		Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("realName", realName);
		requestParamsMap.put("identityNo", identityNo);
		requestParamsMap.put("bankNo", bankNo);
		requestParamsMap.put("bankCardNo", bankCardNo);
		requestParamsMap.put("mobileNo", mobileNo);
		OkHttpWork.getInstance().post(context, ConstantUtil.JRMF_SEND_CODE, requestParamsMap, callBack);
	}


	/**
	 * 绑定银行卡
	 *
	 * @param custUid
	 * @param thirdToken
	 * @param mobileToken
	 * @param callBack
	 */
	public static void bindCard(Context context,String custUid, String thirdToken, String mobileToken, String phoneCode, final OkHttpModelCallBack<BindCardModel> callBack) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();

		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("mobileToken", mobileToken);
		requestParamsMap.put("phoneCode", phoneCode);
		OkHttpWork.getInstance().post(context, ConstantUtil.JRMF_BIND_CARD,requestParamsMap,callBack);
	}

	/**
	 * 获取银行列表
	 *
	 * @return
	 */
	public static void getBankList(Context context, String custUid, String thirdToken, OkHttpModelCallBack<BankResModel> callBack) {
		Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		OkHttpWork.getInstance().post(context, ConstantUtil.JRMF_BANK_LIST, requestParamsMap, callBack);
	}


	/**
	 * 绑卡-第一步-提交银行卡号(根据卡号获取银行卡信息)
	 *
	 * @param thirdToken
	 * @param cardno
	 * @return
	 */
	public static void submitCardno(Context context, String custUid, String thirdToken, String cardno, OkHttpModelCallBack<SubmitCardResModel> callBack) {
		Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("bankCardNo", cardno);
		OkHttpWork.getInstance().post(context, ConstantUtil.JRMF_VAIL_BANK_INFO, requestParamsMap, callBack);
	}


	/**
	 * @param thirdToken
	 * @param orderNo
	 * @param bankCardNo
	 * @param trade_id
	 *            重新获取验证码时传入
	 * @return
	 */
	public static void jdGetCode(Context context, boolean isTrans,String custUid, String thirdToken, String orderNo, String bankCardNo,
								 String trade_id, OkHttpModelCallBack<CodeModel> callBack) {
		Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("paytype", String.valueOf(ConstantUtil.PAY_TYPE_JD));
		requestParamsMap.put("bankCardNo", bankCardNo);
		if (trade_id != null && trade_id.length() > 0) {
			requestParamsMap.put("trade_id", trade_id);
		}
		String url;
		if (isTrans){
			requestParamsMap.put("transferOrderNo", orderNo);
			url = ConstantUtil.JRMF_TRANS_JD_GET_CODE;
		}else{
			requestParamsMap.put("envelopeId", orderNo);
			url = ConstantUtil.JRMF_JD_GET_CODE;
		}
		OkHttpWork.getInstance().post(context,url, requestParamsMap, callBack);

	}

	/**
	 * 用户信息更新接口
	 *
	 * @param custUid
	 * @param nickName
	 * @param avatar
	 * @return
	 */
	public static void updateUserInfo(String custUid, String thirdToken, String nickName, String avatar, OkHttpModelCallBack<BaseModel> callBack) {
		Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		if (StringUtil.isNotEmpty(nickName)) {
			requestParamsMap.put("nickName", nickName);
		}
		if (StringUtil.isNotEmpty(avatar) && avatar.startsWith("http")) {
			 String enAvatar = Base64.encode(avatar.getBytes());
			requestParamsMap.put("avatar", enAvatar);
		}
		OkHttpWork.getInstance().post(ConstantUtil.JRMF_UPDATE_USER_INFO, requestParamsMap, callBack);
	}


	/**
	 *获得乌行H5密码页面url的接口
	 * @param context
	 * @param custUid
	 * @param thirdToken
	 * @param pwdOperationType
	 * @param callBack
	 */
	public static void getUrl(Context context,String custUid,String thirdToken,int pwdOperationType, OkHttpModelCallBack<UrlModel> callBack){
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("pwdOperationType", pwdOperationType);
		OkHttpWork.getInstance().post(context,ConstantUtil.GET_H5_URL,requestParamsMap,callBack);
	}

	public static void getBalancePayRedPacketUrl(Context context,String custUid,String thirdToken,String envelopeId,OkHttpModelCallBack<UrlModel> callBack){
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("custUid", custUid);
		requestParamsMap.put("thirdToken", thirdToken);
		requestParamsMap.put("envelopeId", envelopeId);
		OkHttpWork.getInstance().post(context,ConstantUtil.GET_H5_SEND_RP,requestParamsMap,callBack);
	}

	/**
	 * 得到包含合作伙伴ID的map
	 *
	 * @return
	 */
	private static Map<String, Object> getWithPartnerIdMap() {
		String partnerId = SPManager.getInstance().getString(JrmfClient.getAppContext(), "partner_id", "");
		Map<String, Object> requestParamsMap = new HashMap<String, Object>();
		requestParamsMap.put("partnerId", partnerId);
		requestParamsMap.put("deviceType", "android");
		return requestParamsMap;
	}

	/**
	 * 初始化接口
	 *
	 * @param context
	 */
	public static void init(final Context context) {
		ThreadUtil.getInstance().execute(new Runnable() {

			@Override public void run() {
				String deviceIMEI = RequireInfoUtil.getMobileIMEI(context);
				String deviceType = RequireInfoUtil.getMobileModel();
				String sysVersion = RequireInfoUtil.getMobileSystemVersion(context);
				String appName = RequireInfoUtil.getApplicationName(context);
				String location = RequireInfoUtil.getLocation(context);
				String partnerId = RequireInfoUtil.getPartnerId();
				String custUid = RequireInfoUtil.getUserId(context);
				DeviceUUIDUtil dManager = new DeviceUUIDUtil(context);
				String deviceuuid = dManager.getDeviceUUID();
				String sdkName = "红包SDK";
				String sdkVersion = "2.1.0";
				Map<String, Object> requestParamsMap = new HashMap<>();
				requestParamsMap.put("deviceIMEI", deviceIMEI);
				requestParamsMap.put("deviceType", deviceType);
				requestParamsMap.put("sysVersion", sysVersion);
				requestParamsMap.put("sdkVersion", sdkVersion);
				requestParamsMap.put("appName", appName);
				requestParamsMap.put("location", location);
				requestParamsMap.put("partnerId", partnerId);
				requestParamsMap.put("custUid", custUid);
				requestParamsMap.put("deviceuuid", deviceuuid);
				requestParamsMap.put("sdkName", sdkName);
				String url = "https://api-collection.jrmf360.com/api/v1/mobileDate/collectData.shtml";
				OkHttpWork.getInstance().uploadConent(url, requestParamsMap);
			}
		});
	}
}
