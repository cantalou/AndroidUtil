package com.cantalou.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * 
 *
 * 
 * @author cantalou
 * @date 2016年2月29日 上午10:56:04
 */
public class PrefUtil {

    public static void setString(Context cxt, String key, String value) {
	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(cxt);
	sp.edit().putString(key, value).commit();
    }

    public static String getString(Context cxt, String key) {
	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(cxt);
	return sp.getString(key, null);
    }

    public static boolean getBoolean(Context cxt, String key) {
	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(cxt);
	return sp.getBoolean(key, false);
    }
}
