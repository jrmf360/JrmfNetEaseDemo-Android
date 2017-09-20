package com.jrmf360.neteaselib.base.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @创建人 honglin
 * @创建时间 16/9/9 下午2:14
 * @类描述 sharePreference 的管理者
 */
public class SPManager {

    private static SPManager instance = null;

    private SPManager() {
    }

    public static SPManager getInstance() {
        synchronized (SPManager.class) {
            if (instance == null) {
                instance = new SPManager();
            }
        }
        return instance;
    }

    public SharedPreferences getsp(Context context) {
        return context.getSharedPreferences("jrmf_config", Context.MODE_PRIVATE);
    }

    public SharedPreferences.Editor getEdit(Context context) {
        return getsp(context).edit();
    }

    public void putInt(Context context, String key, int value) {
        getEdit(context).putInt(key, value).commit();
    }

    public void putString(Context context, String key, String value) {
        getEdit(context).putString(key, value).commit();
    }

    public int getInt(Context context, String key, int defValue) {
        return getsp(context).getInt(key, defValue);
    }

    public String getString(Context context, String key, String defValue) {
        return getsp(context).getString(key, defValue);
    }

    public void clear(Context context) {
        getsp(context).edit().clear().commit();
    }

    public boolean getBoolean(Context context, String isVailPwd, boolean defValue) {
        return getsp(context).getBoolean(isVailPwd,defValue);
    }

    public void putBoolean(Context context, String isVailPwd, boolean defValue) {
        getEdit(context).putBoolean(isVailPwd,defValue).commit();
    }
}
