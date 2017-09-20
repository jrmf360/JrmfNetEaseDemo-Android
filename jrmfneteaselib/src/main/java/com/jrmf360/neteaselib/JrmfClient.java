package com.jrmf360.neteaselib;

import android.content.Context;

import com.jrmf360.neteaselib.base.manager.GlobalExceptionManager;
import com.jrmf360.neteaselib.base.utils.LogUtil;
import com.jrmf360.neteaselib.base.utils.ManifestUtil;
import com.jrmf360.neteaselib.base.utils.SPManager;
import com.jrmf360.neteaselib.base.utils.StringUtil;

import java.lang.reflect.Method;

/**
 * @创建人 honglin
 * @创建时间 16/12/7 下午2:47
 * @类描述 金融魔方的客户端,用来初始化渠道和环境
 */
public class JrmfClient {

	private static Context	appContext;

	private static String mWxAppid = "";

	public static int		WX_PAY_TYPE;

	public static int		WX_PAY_TYPE_RED_PACKET		= 0X210;//红包

	public static int		WX_PAY_TYPE_TRANS_ACCOUNT	= 0X220;//转账

	public static int		WX_PAY_TYPE_WALLET_PAY		= 0X230;//收银台

	/**
	 * 默认正式环境
	 */
	private static String	JRMF_BASE_URL	= "https://api.jrmf360.com/";

	private static boolean	isTintStatusBar	= false;

	/**
	 * 是否是测试环境,true 测试环境 ,false正式环境;默认正式环境
	 *
	 * @param isDebug
	 */
	public static void isDebug(boolean isDebug) {
		if (isDebug) {
//			 JRMF_BASE_URL = "https://api-test.jrmf360.com/";//内部测试环境
//			JRMF_BASE_URL = "https://yun-test.jrmf360.com/"; // 商户测试环境
			JRMF_BASE_URL = "https://api-pre.jrmf360.com/"; // 预生产环境
		} else {
			JRMF_BASE_URL = "https://api.jrmf360.com/";
		}
	}


	/**
	 * 设置是否给状态栏着色-默认是不着色的
	 *
	 * @param tintStatusBar
	 */
	public static void setTintStatusBar(boolean tintStatusBar) {
		isTintStatusBar = tintStatusBar;
	}

	public static boolean getIsTintStatusBar() {
		return isTintStatusBar;
	}

	/**
	 * 初始化sdk
	 * @param context
	 */
	public static void init(Context context){
		appContext = context;
		initsdk(context);
	}

	/**
	 *设置微信appid
	 * @param wxAppid
	 */
	public static void setWxAppid(String wxAppid){
		mWxAppid = wxAppid;
	}

	private static void initsdk(Context context) {
		// 获得渠道id- 渠道id是不会变的
		String partnerId = ManifestUtil.getMetaData(context, "JRMF_PARTNER_ID");
		if (StringUtil.isNotEmptyAndNull(partnerId)) {
			SPManager.getInstance().putString(context, "partner_id", partnerId);
		}

		// 获得红包的名字并保存
		String partnerName = ManifestUtil.getMetaData(context, "JRMF_PARTNER_NAME");
		LogUtil.e("partnerName:", partnerName);
		if (StringUtil.isNotEmpty(partnerName)) {
			if (StringUtil.isNotEmptyAndNull(partnerName)) {
				SPManager.getInstance().putString(context, "partner_name", partnerName);
			}
		}
		checkSDK();
		GlobalExceptionManager.getInstance().init(context);
	}

	public static void setPartnerId(String partnerId){
		SPManager.getInstance().putString(appContext, "partner_id", partnerId);
	}

	public static String getPartnerid() {
		return SPManager.getInstance().getString(appContext, "partner_id", "");
	}

	/**
	 * 检查用户集成的sdk
	 */
	private static void checkSDK() {

		try {
			// 红包
			Class rpHttpManager = Class.forName("com.jrmf360.rplib.http.RpHttpManager");
			Method rpInit = rpHttpManager.getDeclaredMethod("init", Context.class);
			rpInit.invoke(rpHttpManager, appContext);
		}catch (Exception e){
			LogUtil.e("no rplib");
		}

		try {
			// 钱包
			Class wHttpManager = Class.forName("com.jrmf360.walletlib.http.RpHttpManager");
			Method wInit = wHttpManager.getDeclaredMethod("init", Context.class);
			wInit.invoke(wHttpManager, appContext);
		} catch (Exception e) {
			LogUtil.e("no walletlib");
		}

		try {
			// 收银台
			Class wpHttpManager = Class.forName("com.jrmf360.walletpaylib.http.RpHttpManager");
			Method wpInit = wpHttpManager.getDeclaredMethod("init", Context.class);
			wpInit.invoke(wpHttpManager, appContext);
		}catch (Exception e){
			LogUtil.e("no walletpaylib");
		}
	}

	public static String getBaseUrl() {
		return JRMF_BASE_URL;
	}

	public static Context getAppContext() {
		return appContext;
	}


	public static String getWxAppId(){
		if (StringUtil.isEmpty(mWxAppid)){
			throw new IllegalArgumentException("wxappid为空");
		}
		return mWxAppid;
	}

}
