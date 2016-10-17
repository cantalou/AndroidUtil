package com.cantalou.android.util;

import android.app.KeyguardManager;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.lang.reflect.Field;

public class DeviceUtils {

    /**
     * 获取设备唯一标识
     *
     * @return
     */
    public static String getUniqueID(Context ctx) {
        /**
         * 获取设备唯一标识的逻辑是：先取得TelephonyManager.getDeviceId()，如果前者为空的话，取 mac
         * 作为设备标示，如果mac为空的话，再取secure.ANDROID_ID为设备标示
         */
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

        WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);

        String wifiMac = "";
        try {
            WifiInfo info = wifi.getConnectionInfo();
            wifiMac = info.getMacAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String androidId = "" + android.provider.Settings.Secure.getString(ctx.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        String deviceId = "";
        try {
            deviceId = tm.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(deviceId)) {
            if (deviceId.matches("[0-9]+")) {
                if (Long.parseLong(deviceId) != 0) {
                    return deviceId;
                }
            } else if (!deviceId.contains("unknow")) {
                return deviceId;
            }
        }
        if (!TextUtils.isEmpty(wifiMac)) {
            String mac = wifiMac.replace(":", "");
            if (mac.matches("[0-9]+")) {
                if (Long.valueOf(mac) != 0) {
                    return wifiMac;
                } else {
                    return androidId;
                }
            } else {
                return wifiMac;
            }
        } else {
            return androidId;
        }
    }

    /**
     * Returns the unique device ID, for example, the IMEI for GSM and the MEID or ESN for CDMA phones. Return null if device ID is not available.
     * Requires Permission: READ_PHONE_STATE
     *
     * @return device ID , null if device ID is not available
     */
    public static String getDeviceId(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

        String deviceId = null;
        try {
            deviceId = tm.getDeviceId();
        } catch (Throwable e) {
            Log.w("Require the unique device ID error." + e.getMessage());
        }
        return deviceId == null || deviceId.contains("unknow") || deviceId.matches("0+") ? "" : deviceId;
    }

    /**
     * Require the MAC address of the WLAN interface
     *
     * @return the MAC address in {@code XX:XX:XX:XX:XX:XX} form
     */
    public static String getMAC(Context ctx) {
        WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);

        String wifiMac = "";
        try {
            WifiInfo info = wifi.getConnectionInfo();
            wifiMac = info.getMacAddress();
        } catch (Throwable e) {
            Log.w("Require the unique device ID error." + e.getMessage());
        }
        return wifiMac;
    }

    /**
     * A 64-bit number (as a hex string) that is randomly generated when the user first sets up the device
     * and should remain constant for the lifetime of the user's device.
     * The value may change if a factory reset is performed on the device.
     *
     * @return ANDROID_ID {@link android.provider.Settings.Secure#ANDROID_ID}
     */
    public static String getAndroidId(Context ctx) {
        return android.provider.Settings.Secure.getString(ctx.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取设备的宽度像素点
     *
     * @param context
     * @return
     */
    public static int getDeviceWidthPixels(Context context) {
        return context.getResources()
                .getDisplayMetrics().widthPixels;
    }

    /**
     * 获取设备的高度像素点
     *
     * @param context
     * @return
     */
    public static int getDeviceHeightPixels(Context context) {
        return context.getResources()
                .getDisplayMetrics().heightPixels;
    }

    /**
     * 获取设备的密度
     *
     * @param context
     * @return
     */
    public static float getDeviceDensity(Context context) {
        return context.getResources()
                .getDisplayMetrics().density;
    }

    /**
     * @param context
     * @return
     * @Description:获取通知栏高度
     * @Title: DeviceUtils.java
     * @date 2014年12月20日 上午10:07:29
     * @author LinXin
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj)
                    .toString());
            statusBarHeight = context.getResources()
                    .getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 获取一个值，表示手机卡系序号
     * 如：10
     *
     * @param context
     * @return
     */
    public static String getSimSerialNumber(Context context) {
        TelephonyManager phoneMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return phoneMgr.getSimSerialNumber();
    }


    /**
     * 判断设备是否锁屏
     *
     * @param content
     * @return
     */
    public static boolean isScreenLocked(Context content) {
        KeyguardManager km = (KeyguardManager) content.getSystemService(Context.KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }

}
