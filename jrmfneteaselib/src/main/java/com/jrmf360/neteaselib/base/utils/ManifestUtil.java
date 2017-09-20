package com.jrmf360.neteaselib.base.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * @创建人 honglin
 * @创建时间 16/10/17 下午9:00
 * @类描述 获得Manifest中的内容
 */
public class ManifestUtil {

    /**
     * 获得 meta-data数据
     * @param context
     * @param name
     * @return
     */
    public static String getMetaData(Context context, String name) {
        String value = "";
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo != null) {
                value = appInfo.metaData.getString(name);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }
}