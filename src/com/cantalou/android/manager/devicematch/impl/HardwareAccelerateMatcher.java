package com.cantalou.android.manager.devicematch.impl;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.cantalou.android.manager.devicematch.BuildPattern;
import com.cantalou.android.manager.devicematch.Matcher;
import com.cantalou.android.util.ViewUtils;

/**
 * 适配由于硬件加速内存不足导致崩溃问题
 */
public class HardwareAccelerateMatcher implements Matcher {
    public boolean match(Activity activity) {
        for (BuildPattern bp : new BuildPattern[]{new BuildPattern("V801s"), new BuildPattern("GN151"), new BuildPattern("Colorfly E708 Q2")}) {
            if (bp.match()) {
                ViewGroup decor = ViewUtils.getDecorView(activity);
                disableWebViewHardwareAccelerate(decor);
                return true;
            }
        }
        return false;
    }

    /**
     * 禁用WebView的硬件加速
     *
     * @param parent
     */
    public void disableWebViewHardwareAccelerate(ViewGroup parent) {
        for (int i = 0, len = parent.getChildCount(); i < len; i++) {
            View v = parent.getChildAt(i);
            if (v instanceof ViewGroup) {
                disableWebViewHardwareAccelerate((ViewGroup) v);
            } else if (v instanceof WebView && Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                v.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
        }
    }
}
