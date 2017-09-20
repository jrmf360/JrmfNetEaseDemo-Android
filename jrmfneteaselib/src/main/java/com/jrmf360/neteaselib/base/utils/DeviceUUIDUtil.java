package com.jrmf360.neteaselib.base.utils;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

/**
 * 获得或者生成设备唯一id
 * @author honglin
 *
 */
public class DeviceUUIDUtil {

	private Context mContext;
	
	private  final String PREFS_FILE="device_uuid";

	private  final String PREFS_DEVICE_ID="device_id";
	
	private UUID uuid;

	public DeviceUUIDUtil(Context context){
		this.mContext = context;
	}

	private void generateDevUUID() {
		if( uuid ==null ) {
            synchronized (DeviceUUIDUtil.class) {
                if( uuid == null) {
                    final SharedPreferences prefs = mContext.getSharedPreferences( PREFS_FILE, 0);
                    final String id = prefs.getString(PREFS_DEVICE_ID, null );
                    if (id != null) {
                        // Use the ids previously computed and stored in the prefs file
                        uuid = UUID.fromString(id);
                    } else {
                        final String androidId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
                        // Use the Android ID unless it's broken, in which case fallback on deviceId,
                        // unless it's not available, then fallback on a random number which we store
                        // to a prefs file
                        try {
                            if (!"9774d56d682e549c".equals(androidId)) {
                                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                            } else {
                                final String deviceId = ((TelephonyManager) mContext.getSystemService( Context.TELEPHONY_SERVICE )).getDeviceId();
                                uuid = deviceId!=null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        // Write the value out to the prefs file
                        prefs.edit().putString(PREFS_DEVICE_ID, uuid.toString() ).commit();
                    }
                }
            }
        }
	}
	
	public String getDeviceUUID(){
		if (uuid == null) {
			generateDevUUID();
		}
		return uuid == null? "获取deviceUuid失败":uuid.toString();
	}
}
