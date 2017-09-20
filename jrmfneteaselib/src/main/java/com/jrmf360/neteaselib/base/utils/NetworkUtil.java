package com.jrmf360.neteaselib.base.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.jrmf360.neteaselib.JrmfClient;

/**
 * @创建人 honglin
 * @创建时间 17/1/17 下午5:02
 * @类描述 网络的工具类
 */
public class NetworkUtil {

	/**
	 * 判断是否有网
	 *
	 * @return
	 */
	public static boolean isNetConnect() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) JrmfClient.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
		if (info == null || !info.isConnectedOrConnecting()) {
			return false;
		}
		return true;
	}

	/**
	 * 获取网络状态
	 *
	 * @return
	 */
	public static String getNetType() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) JrmfClient.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
		if (info == null || !info.isConnectedOrConnecting()) {
			return "无网";
		}
		switch (info.getType()) {
			case ConnectivityManager.TYPE_WIFI:
			case ConnectivityManager.TYPE_WIMAX:
			case ConnectivityManager.TYPE_ETHERNET:
				return "WiFi";
			case ConnectivityManager.TYPE_MOBILE:
				switch (info.getSubtype()) {
					case TelephonyManager.NETWORK_TYPE_LTE: // 4G
					case TelephonyManager.NETWORK_TYPE_HSPAP:
					case TelephonyManager.NETWORK_TYPE_EHRPD:
						return "4G";
					case TelephonyManager.NETWORK_TYPE_UMTS: // 3G
					case TelephonyManager.NETWORK_TYPE_CDMA:
					case TelephonyManager.NETWORK_TYPE_EVDO_0:
					case TelephonyManager.NETWORK_TYPE_EVDO_A:
					case TelephonyManager.NETWORK_TYPE_EVDO_B:
						return "3G";
					case TelephonyManager.NETWORK_TYPE_GPRS: // 2G
					case TelephonyManager.NETWORK_TYPE_EDGE:
						return "2G";
				}
				break;
		}
		return "无网";
	}
}
