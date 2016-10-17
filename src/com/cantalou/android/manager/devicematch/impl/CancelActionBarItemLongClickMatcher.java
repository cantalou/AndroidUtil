package com.cantalou.android.manager.devicematch.impl;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.cantalou.android.manager.devicematch.Matcher;
import com.cantalou.android.util.ReflectUtil;

/**
 * 取消actionBar里面item的longClick显示toast的事件.
 */
public class CancelActionBarItemLongClickMatcher implements Matcher {

    @Override
    public boolean match(Activity activity) {
        Object actionBar = ReflectUtil.invoke(activity, "getSupportActionBar");
        if (actionBar == null) {
            actionBar = ReflectUtil.invoke(activity, "getActionBar");
        }

        if (actionBar == null) {
            return true;
        }

        cancelLongClick((ViewGroup) ReflectUtil.get(actionBar, "mActionBar.mActionView"), "ActionMenuItemView");
        return true;
    }

    private void cancelLongClick(ViewGroup content, String viewName) {
        if (null == content) {
            return;
        }
        int childCount = content.getChildCount();
        if (childCount == 0) {
            return;
        }
        for (int i = 0; i < childCount; i++) {
            View v = content.getChildAt(i);
            if (v.getClass()
                    .getSimpleName()
                    .matches(viewName)) {
                v.setOnLongClickListener(null);
                if (v instanceof ViewGroup) {
                    cancelLongClick((ViewGroup) v, ".*");
                }
            } else if (v instanceof ViewGroup) {
                cancelLongClick((ViewGroup) v, viewName);
            }
        }
    }
}
