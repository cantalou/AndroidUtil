package com.cantalou.android.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

/**
 * 
 *
 * 
 * @author cantalou
 * @date 2016年2月29日 上午10:55:07
 */
@SuppressLint("NewApi")
public class ActivityStateUtil {

    /**
     * 判断对象是否已Destroy
     *
     * @param cxt
     *            context
     * @return true 已销毁 , 否则 false
     */
    public static boolean isDestroy(Context cxt) {
	if (cxt instanceof Activity) {
	    return isDestroy(((Activity) cxt));
	} else if (cxt instanceof ContextWrapper) {
	    cxt = ((ContextWrapper) cxt).getBaseContext();
	    if (cxt instanceof Activity) {
		return isDestroy(((Activity) cxt));
	    } else {
		return false;
	    }
	} else {
	    return false;
	}
    }

    /**
     * 判断对象是否已Destroy
     *
     * @param activity
     *            要判断的activity
     * @return true 已销毁
     */
    public static boolean isDestroy(Activity activity) {

	if (activity == null) {
	    return true;
	}
	if (Build.VERSION.SDK_INT >= 17) {
	    return activity.isFinishing() || activity.isDestroyed();
	} else {
	    return activity.isFinishing();
	}
    }
}
