package com.cantalou.android.manager.devicematch;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import com.cantalou.android.manager.devicematch.impl.CancelActionBarItemLongClickMatcher;
import com.cantalou.android.manager.devicematch.impl.HardwareAccelerateMatcher;
import com.cantalou.android.manager.lifecycle.ActivityLifecycleCallbacksAdapter;
import com.cantalou.android.manager.lifecycle.ActivityLifecycleManager;
import com.cantalou.android.util.Log;

import java.util.ArrayList;

/**
 * Project Name: m4399_Forums
 * File Name:    DeviceMatchManager.java
 * ClassName:    DeviceMatchManager
 * <p/>
 * Description: 机型适配.
 *
 * @author LinZhiWei
 * @date 2015年07月08日 14:59
 * <p/>
 * Copyright (c) 2015年,  Network CO.ltd. All Rights Reserved.
 */
public class DeviceMatchManager extends ActivityLifecycleCallbacksAdapter {
    private ArrayList<Matcher> beforeOnCreateList = new ArrayList<Matcher>();

    private ArrayList<Matcher> onResumeList = new ArrayList<Matcher>();

    private ArrayList<Matcher> onDestroyList = new ArrayList<Matcher>();

    private ArrayList<Matcher> afterOnWindowFocusChangedList = new ArrayList<Matcher>();

    private static class InstanceHolder {
        static final DeviceMatchManager INSTANCE = new DeviceMatchManager();
    }

    private DeviceMatchManager() {
        ActivityLifecycleManager.getInstance()
                .registerActivityLifecycleCallbacks(this);

        //onResume
        onResumeList.add(new HardwareAccelerateMatcher());

        //onWindowFocusChanged
        afterOnWindowFocusChangedList.add(new CancelActionBarItemLongClickMatcher());

        Log.d("device info manufacturer：{}, model:{}, release:{}", Build.MANUFACTURER, Build.MODEL, Build.VERSION.RELEASE);
    }

    public static DeviceMatchManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public void onBeforeActivityCreate(Activity activity, Bundle savedInstanceState) {
        callMatcher(activity, beforeOnCreateList);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        callMatcher(activity, onDestroyList);
    }

    @Override
    public void onActivityWindowFocusChanged(Activity activity) {
        callMatcher(activity, afterOnWindowFocusChangedList);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        callMatcher(activity, onResumeList);
    }

    private void callMatcher(Activity activity, ArrayList<Matcher> matcherList) {
        for (Matcher matcher : (ArrayList<Matcher>) matcherList.clone()) {
            try {
                matcher.match(activity);
            } catch (Exception e) {
                Log.e(e);
            }
        }
    }
}
