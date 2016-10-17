package com.cantalou.android.manager.devicematch;

import android.app.Activity;

public interface Matcher {
    /**
     * 适配机型
     *
     * @param activity
     */
    public boolean match(Activity activity);
}
