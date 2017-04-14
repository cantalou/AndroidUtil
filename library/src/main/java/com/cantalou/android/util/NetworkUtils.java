package com.cantalou.android.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

    private static final String TAG = "NetworkUtils";

    /**
     * 判断是否有网络
     *
     * @return
     */
    public static boolean isNetworkAvailable(Context ctx) {
        NetworkInfo networkInfo = getActiveNetworkInfo(ctx);
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    /**
     * 获取当前的网络信息
     *
     * @return NetworkInfo
     */
    public static NetworkInfo getActiveNetworkInfo(Context ctx) {
        NetworkInfo result = null;
        try {
            ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                result = cm.getActiveNetworkInfo();
            }
        } catch (Exception e) {
            Log.e(e);
        }
        return result;
    }

    /**
     * 判断是否WIFI
     *
     * @return
     */
    public static boolean isWifiAvailable(Context ctx) {
        NetworkInfo wifi = getActiveNetworkInfo(ctx);
        return wifi != null && wifi.getType() == ConnectivityManager.TYPE_WIFI && wifi.isAvailable() && wifi.isConnected();
    }

}
