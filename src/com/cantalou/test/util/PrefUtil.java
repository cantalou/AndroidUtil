package com.cantalou.test.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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