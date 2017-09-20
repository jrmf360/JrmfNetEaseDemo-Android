package com.jrmf360.neteaselib.rp.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

import com.jrmf360.neteaselib.JrmfClient;
import com.jrmf360.neteaselib.base.utils.LogUtil;
import com.jrmf360.neteaselib.base.utils.SPManager;
import com.jrmf360.neteaselib.base.utils.StringUtil;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.location.LocationManager;
import android.telephony.TelephonyManager;

/**
 * 获得设备等信息
 * @author honglin
 *系统版本号、手机型号、设备号IMEI、sdk版本号、商户号，商户用户id（通过缓存的商户用户id）、地理位置、deviceuuid
 */
public class RequireInfoUtil {

	public static String getMobileSystemVersion(Context context){
		LogUtil.i("systemVersion", android.os.Build.VERSION.RELEASE);
		return android.os.Build.VERSION.RELEASE;
	}  
	
	/**
	 * 返回手机型号
	 * @return
	 */
	public static String getMobileModel(){
		LogUtil.i("model", android.os.Build.MODEL);
		return android.os.Build.MODEL;
	}
	
	/**
	 * 获得手机的IMEI
	 * @param context
	 * @return
	 */
	public static String getMobileIMEI(Context context) {
		String imei = SPManager.getInstance().getString(context, "IMEI", "");
        if(StringUtil.isEmpty(imei)) {
            try {
                TelephonyManager editor = (TelephonyManager)context.getSystemService("phone");
                imei = editor.getDeviceId();
            } catch (SecurityException var4) {
                LogUtil.e("DeviceUtils", "SecurityException");
            }

            if(StringUtil.isEmpty(imei) || imei.equals("000000000000000") || imei.equals("000000000000")) {
                SecureRandom editor1 = new SecureRandom();
                imei = (new BigInteger(64, editor1)).toString(16);
            }
            SPManager.getInstance().putString(context, "IMEI", imei);
        }

        return imei;
    }
	
	/**
	 * 获得versionName
	 * @param context
	 * @return
	 */
	public static String getSdkVersion(Context context){
		// 获取packagemanager的实例  
		String versionName = "";
        PackageManager packageManager = context.getPackageManager();  
        // getPackageName()是你当前类的包名，0代表是获取版本信息  
        PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(),PackageManager.GET_ACTIVITIES);
			versionName = packInfo.versionName;  
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}  
		LogUtil.i("versionName", versionName);
        return versionName;  
	}
	
	/**
	 * 得到应用的名称
	 * @param context
	 * @return
	 */
	public static String getApplicationName(Context context) {  
        PackageManager packageManager = null;  
        ApplicationInfo applicationInfo = null;  
        try {  
            packageManager = context.getApplicationContext().getPackageManager();  
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);  
        } catch (NameNotFoundException e) {
            applicationInfo = null;  
        }  
        String applicationName = (String) packageManager.getApplicationLabel(applicationInfo); 
        LogUtil.i("applicationName",applicationName);
        return applicationName;  
    }
	
	/**
	 * 获得位置信息
	 * @param context
	 * @return
	 */
	public static String getLocation(Context context){
        PackageManager packageManager = context.getPackageManager();  
		boolean flag = false;
		try {
			String[] permissions = packageManager.getPackageInfo(context.getPackageName(),PackageManager.GET_PERMISSIONS).requestedPermissions;
			for (String permiss : permissions) {
				if ("android.permission.ACCESS_FINE_LOCATION".equals(permiss) || "android.permission.ACCESS_COARSE_LOCATION_LOCATION".equals(permiss)) {
					flag = true;
					break;
				}
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		if (flag) {
			//获得位置管理器
			LocationManager	locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			//获取所有可用的位置提供器
			List<String> providers = locationManager.getProviders(true);
			String locationProvider = "";
			if(providers.contains(LocationManager.GPS_PROVIDER)){
				//如果是GPS
				locationProvider = LocationManager.GPS_PROVIDER;
			}else if(providers.contains(LocationManager.NETWORK_PROVIDER)){
				//如果是Network
				locationProvider = LocationManager.NETWORK_PROVIDER;
			}else{
				LogUtil.i("location","没有可用的位置提供器");
				return "没有可用的位置提供器";
			}
			//获取Location
			Location location = locationManager.getLastKnownLocation(locationProvider);
			if (location == null) {
				return "没有可用的位置提供器";
			}
			StringBuffer sb = new StringBuffer();
			sb.append(location.getLongitude());
			sb.append(":");
			sb.append(location.getLatitude());
			LogUtil.i("location", sb.toString());
			return sb.toString();
		}
		return "";
	}
	
	/**
	 * 商户号－合作伙伴id
	 * @return
	 */
	public static String getPartnerId(){
		LogUtil.i("partnerId", JrmfClient.getPartnerid());
		return JrmfClient.getPartnerid();
	}
	/**
	 * 商户用户Id
	 * @return
	 */
	public static String getUserId(Context context){
		String userId = SPManager.getInstance().getString(context, "userId", "");
		LogUtil.i("userId",userId);
		return userId;
	}
}
