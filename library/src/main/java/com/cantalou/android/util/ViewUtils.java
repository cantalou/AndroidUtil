package com.cantalou.android.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;

/**
 *
 */
public class ViewUtils {
    /**
     * 获取view在屏幕Y轴的坐标
     *
     * @param v
     * @return Y轴的坐标
     */
    public static int getScreeLocationY(View v) {
        return getScreeLocationXY(v)[1];
    }

    /**
     * 获取view在屏幕X轴的坐标
     *
     * @param v
     * @return X轴的坐标
     */
    public static int getScreeLocationX(View v) {
        return getScreeLocationXY(v)[0];
    }

    /**
     * 获取View屏幕坐标
     *
     * @param v
     * @return
     */
    public static int[] getScreeLocationXY(View v) {
        int[] xy = new int[2];
        v.getLocationOnScreen(xy);
        return xy;
    }

    /**
     * 优先从Activity获取LayoutInflater
     *
     * @param cxt
     * @return LayoutInflater
     */
    public static LayoutInflater getLayoutInflater(Context cxt) {
        LayoutInflater li = null;
        if (cxt instanceof Activity) {
            li = ((Activity) cxt).getLayoutInflater();
        } else {
            li = LayoutInflater.from(cxt);
        }
        return li;
    }

    /**
     * 优先从Activity获取LayoutInflater
     *
     * @param v
     * @return LayoutInflater
     */
    public static LayoutInflater getLayoutInflater(View v) {
        return getLayoutInflater(v.getContext());
    }

    /**
     * 增加view的点击区域
     *
     * @param view
     */
    public static void expandViewTouchDelegate(final View view, final float padding) {
        if (view == null) {
            return;
        }
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                Rect bounds = new Rect();
                view.setEnabled(true);
                view.getHitRect(bounds);

                bounds.top -= padding;
                bounds.bottom += padding;
                bounds.left -= padding;
                bounds.right += padding;
                if (View.class.isInstance(view.getParent())) {
                    ((View) view.getParent()).setTouchDelegate(new TouchDelegate(bounds, view));
                }
            }
        }, 100);
    }

    /**
     * 获取Activity对应的DecorView
     *
     * @param activity
     * @param <T>
     * @return decorView
     */
    public static <T> T getDecorView(Activity activity) {
        if (activity == null) {
            Log.w(" activity activity");
            return null;
        }

        Window w = activity.getWindow();
        if (w == null) {
            Log.w("activity.getWindow() null");
            return null;
        }

        return (T) w.getDecorView();
    }


    public interface RecursionChildViewHandler {
        /**
         * @param v
         * @return 返回 true时表示这次处理已完成, 跳出递归逻辑不再处理剩下的 childView
         */
        public boolean handle(View v);
    }

    public static void recursionChildView(Activity activity, RecursionChildViewHandler filter) {
        recursionChildView((ViewGroup) getDecorView(activity), filter);
    }

    public static void recursionChildView(ViewGroup vg, RecursionChildViewHandler filter) {
        for (int i = 0, len = vg.getChildCount(); i < len; i++) {
            View child = vg.getChildAt(i);
            if (filter.handle(child)) {
                return;
            }
            if (child instanceof ViewGroup) {
                recursionChildView((ViewGroup) child, filter);
            }
        }
    }


    /**
     * 启动WebView的js线程
     *
     * @param context
     */
    public static void webViewResumeTimers(Context context) {
        Context appContext = context.getApplicationContext();
        WebView webView = new WebView(appContext);
        webView.resumeTimers();
        webView.destroy();
    }
}
