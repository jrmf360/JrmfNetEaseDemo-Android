package com.jrmf360.neteaselib.wallet.http;

import android.content.Context;

import com.jrmf360.neteaselib.JrmfClient;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.http.OkHttpWork;
import com.jrmf360.neteaselib.base.model.BaseModel;
import com.jrmf360.neteaselib.base.utils.Base64;
import com.jrmf360.neteaselib.base.utils.DesUtil;
import com.jrmf360.neteaselib.base.utils.DeviceUUIDUtil;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.utils.ThreadUtil;
import com.jrmf360.neteaselib.rp.utils.RequireInfoUtil;
import com.jrmf360.neteaselib.wallet.http.model.AccountInfoModel;
import com.jrmf360.neteaselib.wallet.http.model.BankInfoModel;
import com.jrmf360.neteaselib.wallet.http.model.BindCardModel;
import com.jrmf360.neteaselib.wallet.http.model.ChargeInfoModel;
import com.jrmf360.neteaselib.wallet.http.model.CodeModel;
import com.jrmf360.neteaselib.wallet.http.model.GetDepositModel;
import com.jrmf360.neteaselib.wallet.http.model.ReceiveRpModel;
import com.jrmf360.neteaselib.wallet.http.model.RedeemInfoModel;
import com.jrmf360.neteaselib.wallet.http.model.RpInfoModel;
import com.jrmf360.neteaselib.wallet.http.model.SecureSettingModel;
import com.jrmf360.neteaselib.wallet.http.model.SendRpModel;
import com.jrmf360.neteaselib.wallet.http.model.SupportBankModel;
import com.jrmf360.neteaselib.wallet.http.model.TradeDetailModel;
import com.jrmf360.neteaselib.wallet.http.model.UrlModel;
import com.jrmf360.neteaselib.wallet.manager.UserInfoManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author honglin
 */
public class WalletHttpManager {

	/**
	 * 获得钱包首页信息
	 * 
	 */
	public static void wallet(Context context, String nickName, String avatarUrl, final OkHttpModelCallBack callback) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		if (StringUtil.isNotEmpty(nickName)) {
			requestParamsMap.put("nickName", nickName);
		}
		if (StringUtil.isNotEmpty(avatarUrl) && avatarUrl.startsWith("http")) {
			String enAvatar = Base64.encode(avatarUrl.getBytes());
			requestParamsMap.put("avatarUrl", enAvatar);
		}
		OkHttpWork.getInstance().post(context,HttpUrl.MY_WALLET,requestParamsMap,callback);
	}

	/**
	 * 充值
	 * 
	 * @param callBack
	 */
	public static void chargeInfo(Context context, final OkHttpModelCallBack callBack) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		OkHttpWork.getInstance().post(context,HttpUrl.CHARGE_INFO,requestParamsMap,callBack);
	}

	/**
	 * 充值请求验证码
	 * 
	 * @param bankCardNo
	 *            银行卡号
	 * @param amount
	 *            金额
	 * @param callBack
	 *            回调接口
	 */
	public static void chargeReqCode(Context context, String bankCardNo, String amount, final OkHttpModelCallBack callBack) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("bankCardNo", bankCardNo);
		requestParamsMap.put("amount", amount);
		OkHttpWork.getInstance().post(context,HttpUrl.CHARGE_REQ_CODE,requestParamsMap,callBack);
	}

	/**
	 * 充值验证用户输入的短信验证码是否正确
	 * 
	 * @param tradeId
	 *            交易id
	 * @param tradeCode
	 *            用户输入的验证码
	 * @param callBack
	 */
	public static void chargeVerifyCode(Context context, String tradeId, String tradeCode, final OkHttpModelCallBack<BaseModel> callBack) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("tradeId", tradeId);
		requestParamsMap.put("tradeCode", tradeCode);
		OkHttpWork.getInstance().post(context,HttpUrl.CHARGE_VERIFY_CODE,requestParamsMap,callBack);
	}

	/**
	 * 绑卡第一步，验证银行卡信息
	 * 
	 * @param bankCardNo
	 *            银行卡号码
	 * @param callBack
	 */
	public static void VerifyBankInfo(Context context, String bankCardNo, final OkHttpModelCallBack<BankInfoModel> callBack) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("bankCardNo", bankCardNo);
		OkHttpWork.getInstance().post(context,HttpUrl.VERIFY_BANK_INFO,requestParamsMap,callBack);
	}

	/**
	 * 银行卡设置－我的银行卡列表
	 * 
	 * @param callBack
	 */
	public static void myCardList(Context context, final OkHttpModelCallBack<ChargeInfoModel> callBack) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		OkHttpWork.getInstance().post(context,HttpUrl.MY_CARD_LIST,requestParamsMap,callBack);
	}

	/**
	 * 银行卡设置－我的银行卡列表
	 * 
	 * @param callBack
	 */
	public static void supportCardList(Context context, final OkHttpModelCallBack<SupportBankModel> callBack) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		OkHttpWork.getInstance().post(context,HttpUrl.MY_SUPPORT_CARD_LIST,requestParamsMap,callBack);
	}

	/**
	 * 安全设置
	 * 
	 * @param callBack
	 */
	public static void secureSetting(Context context,final OkHttpModelCallBack<SecureSettingModel> callBack) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		OkHttpWork.getInstance().post(context,HttpUrl.SECURE_SETTING,requestParamsMap,callBack);
	}


	/**
	 * 绑卡请求验证码
	 * 
	 * @param realName
	 *            姓名
	 * @param identityNo
	 *            身份证号
	 * @param bankCardNo
	 *            银行卡号
	 * @param bankNo
	 *            银行编码
	 * @param mobileNo
	 *            手机号
	 * @param callBack
	 */
	public static void bindCardReqCode(Context context,String realName, String identityNo, String bankCardNo, String bankNo, String mobileNo,
			final OkHttpModelCallBack<CodeModel> callBack) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("realName", realName);
		requestParamsMap.put("identityNo", identityNo);
		requestParamsMap.put("bankCardNo", bankCardNo);
		requestParamsMap.put("bankNo", bankNo);
		requestParamsMap.put("mobileNo", mobileNo);
		OkHttpWork.getInstance().post(context,HttpUrl.BIND_CARD_REQ_CODE,requestParamsMap,callBack);
	}


	/**
	 * 解绑银行卡
	 * 
	 * @param tranPwd
	 * @param bankcardno
	 * @param callBack
	 */
	public static void unbindCard(Context context, String tranPwd, String bankcardno, final OkHttpModelCallBack<BaseModel> callBack) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("tranPwd", DesUtil.encrypt(tranPwd, HttpUrl.A));
		requestParamsMap.put("bankcardno", bankcardno);
		OkHttpWork.getInstance().post(context,HttpUrl.UNBIND_CARD,requestParamsMap,callBack);

	}

	/**
	 * 实名认证发送验证码
	 * @param context
	 * @param identityNo
	 * @param realName
	 * @param mobileNo
	 * @param callback
	 */
	public static void certifySendCode(final Context context, String identityNo, String realName,
									   String mobileNo, final OkHttpModelCallBack<CodeModel> callback) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("identityNo", identityNo);
		requestParamsMap.put("realName", realName);
		requestParamsMap.put("mobileNo", mobileNo);
		OkHttpWork.getInstance().post(context, HttpUrl.CERTIFY_SEND_CODE, requestParamsMap, callback);
	}

	/**
	 * 实名认证校验验证码
	 * @param context
	 * @param mobileToken
	 * @param callback
	 */
	public static void certifyValiCode(final Context context,String mobileToken,String code,
									   final OkHttpModelCallBack<BaseModel> callback) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("mobileToken", mobileToken);
		requestParamsMap.put("code", code);
		OkHttpWork.getInstance().post(context, HttpUrl.CERTIFY_VALI_CODE, requestParamsMap, callback);
	}


	/**
	 * 交易记录
	 * 
	 * @param dateType
	 * @param page
	 * @param callBack
	 */
	public static void tradeHistory(Context context,int dateType, int page, final OkHttpModelCallBack<TradeDetailModel> callBack) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("dateType", dateType);
		requestParamsMap.put("page", page);
		OkHttpWork.getInstance().post(context,HttpUrl.TRADE_HISTORY,requestParamsMap,callBack);
	}

	/**
	 * 加载发出的红包
	 * 
	 * @param page
	 * @param pageSize
	 * @param callBack
	 */
	public static void loadSendRpInfo(Context context,int page, int pageSize, final OkHttpModelCallBack<SendRpModel> callBack) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("page", page);
		requestParamsMap.put("pageSize", pageSize);
		OkHttpWork.getInstance().post(context,HttpUrl.SEND_RP,requestParamsMap,callBack);
	}

	/**
	 * 加载收到的红包
	 * 
	 * @param page
	 * @param pageSize
	 * @param callBack
	 */
	public static void loadReceiveRpInfo(Context context, int page, int pageSize, final OkHttpModelCallBack<ReceiveRpModel> callBack) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("page", page);
		requestParamsMap.put("pageSize", pageSize);
		OkHttpWork.getInstance().post(context,HttpUrl.RECEIVE_RP,requestParamsMap,callBack);
	}


	/**
	 * 获得红包详情
	 * @param context
	 * @param envelopeId
	 * @param page
     * @param callBack
     */
	public static void rpDetail(Context context,String envelopeId, int page, OkHttpModelCallBack<RpInfoModel> callBack){
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("envelopeId", envelopeId);
		requestParamsMap.put("page", page);
		OkHttpWork.getInstance().post(context,HttpUrl.RED_DETAIL,requestParamsMap,callBack);

	}

	/**
	 * 获得账户信息
	 * 
	 * @param callBack
	 */
	public static void getAccountInfo(Context context, final OkHttpModelCallBack<AccountInfoModel> callBack) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		OkHttpWork.getInstance().post(context, HttpUrl.GET_ACCOUNT_INFO, requestParamsMap, callBack);
	}

	/**
	 * 绑定银行卡
	 * 
	 * @param mobileToken
	 * @param callBack
	 */
	public static void bindCard(Context context,String mobileToken, String phoneCode, final OkHttpModelCallBack<BindCardModel> callBack) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("mobileToken", mobileToken);
		requestParamsMap.put("phoneCode", phoneCode);
		OkHttpWork.getInstance().post(context,HttpUrl.BIND_CARD,requestParamsMap,callBack);
	}

	/**
	 * 提现获得信息
	 * 
	 * @param callBack
	 */
	public static void redeemInfo(Context context,final OkHttpModelCallBack<RedeemInfoModel> callBack) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		OkHttpWork.getInstance().post(context,HttpUrl.REDEEN_INFO,requestParamsMap,callBack);

	}

	/**
	 * 请求服务器－提现
	 * 
	 * @param bankCardNo
	 * @param balance
	 * @param callBack
	 */
	public static void reqDeposit(Context context, String bankCardNo, String balance,final OkHttpModelCallBack<GetDepositModel> callBack) {

		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("bankCardNo", bankCardNo);
		requestParamsMap.put("balance", StringUtil.formatMoney(balance));
		OkHttpWork.getInstance().post(context,HttpUrl.REDEEN,requestParamsMap,callBack);
	}

	/**
	 * 绑提现银行卡
	 * @param context
	 * @param bankCardNo
	 * @param callBack
	 */
	public static void bindRedeemCard(Context context, String bankCardNo,final OkHttpModelCallBack<BaseModel> callBack) {
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("bankCardNo", bankCardNo);
		OkHttpWork.getInstance().post(context,HttpUrl.BIND_REDEEM_CARD,requestParamsMap,callBack);
	}

	/**
	 *
	 * @param context
	 * @param pwdOperationType
	 * @param phoneNo 绑卡的手机号
	 * @param binding 是否是绑卡跳转到设置密码页面0：正常设置密码  1：银行卡绑定跳转设置密码
	 * @param callBack
	 */
	public static void getUrl(Context context,int pwdOperationType,String phoneNo,int binding,
							  OkHttpModelCallBack<UrlModel> callBack){
		final Map<String, Object> requestParamsMap = getWithPartnerIdMap();
		requestParamsMap.put("pwdOperationType", pwdOperationType);
		requestParamsMap.put("phoneNo",phoneNo);
		requestParamsMap.put("binding",binding);
		OkHttpWork.getInstance().post(context,HttpUrl.GET_H5_URL,requestParamsMap,callBack);
	}

	/**
	 * 获得带有公共请求参数的map
	 * 
	 * @return
	 */
	private static Map<String, Object> getWithPartnerIdMap() {
		Map<String, Object> requestParamsMap = new HashMap<String, Object>();
		String parenerId = JrmfClient.getPartnerid();
		if (StringUtil.isEmpty(parenerId)) {
			throw new IllegalStateException("partner_id 不能为空");
		}
		requestParamsMap.put("custUid", UserInfoManager.getInstance().getUserId());
		requestParamsMap.put("thirdToken", UserInfoManager.getInstance().getThirdToken());
		requestParamsMap.put("partnerId", parenerId);
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
				String sdkName = "钱包SDK";
				String sdkVersion = "1.2.1";
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
				OkHttpWork.getInstance().uploadConent(url,requestParamsMap);
			}
		});
	}

}
