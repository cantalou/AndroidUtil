package com.cantalou.android.manager.lifecycle;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.Bundle;

import com.cantalou.android.util.Log;

import java.util.ArrayList;

import static com.cantalou.android.util.ReflectUtil.forName;
import static com.cantalou.android.util.ReflectUtil.invoke;
import static com.cantalou.android.util.ReflectUtil.set;

/**
 * @author cantalou
 * @date 2016年4月17日 下午11:06:46
 */
public class ActivityLifecycleManager {

    private static boolean replaced;

    private ArrayList<ActivityLifecycleCallbacks> lifecycleCallbacks = new ArrayList<ActivityLifecycleCallbacks>();

    private ActivityLifecycleManager() {
        install();
    }

    static class InstanceHolder {
        static ActivityLifecycleManager instance = new ActivityLifecycleManager();
    }

    public static ActivityLifecycleManager getInstance() {
        return InstanceHolder.instance;
    }

    public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        synchronized (lifecycleCallbacks) {
            lifecycleCallbacks.add(callback);
        }
    }

    public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        synchronized (lifecycleCallbacks) {
            lifecycleCallbacks.remove(callback);
        }
    }

    public void dispatchBeforeActivityOnCreate(Activity activity, Bundle savedInstanceState) {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (int i = 0; i < callbacks.length; i++) {
                ((ActivityLifecycleCallbacks) callbacks[i]).beforeActivityOnCreate(activity, savedInstanceState);
            }
        }
    }

    public void dispatchActivityCreated(Activity activity, Bundle savedInstanceState) {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (int i = 0; i < callbacks.length; i++) {
                ((ActivityLifecycleCallbacks) callbacks[i]).onActivityCreated(activity, savedInstanceState);
            }
        }
    }

    public void dispatchActivityStarted(Activity activity) {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (int i = 0; i < callbacks.length; i++) {
                ((ActivityLifecycleCallbacks) callbacks[i]).onActivityStarted(activity);
            }
        }
    }

    public void dispatchActivityResumed(Activity activity) {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (int i = 0; i < callbacks.length; i++) {
                ((ActivityLifecycleCallbacks) callbacks[i]).onActivityResumed(activity);
            }
        }
    }

    public void dispatchActivityPaused(Activity activity) {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (int i = 0; i < callbacks.length; i++) {
                ((ActivityLifecycleCallbacks) callbacks[i]).onActivityPaused(activity);
            }
        }
    }

    public void dispatchActivityStopped(Activity activity) {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (int i = 0; i < callbacks.length; i++) {
                ((ActivityLifecycleCallbacks) callbacks[i]).onActivityStopped(activity);
            }
        }
    }

    public void dispatchActivitySaveInstanceState(Activity activity, Bundle outState) {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (int i = 0; i < callbacks.length; i++) {
                ((ActivityLifecycleCallbacks) callbacks[i]).onActivitySaveInstanceState(activity, outState);
            }
        }
    }

    public void dispatchActivityDestroyed(Activity activity) {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (int i = 0; i < callbacks.length; i++) {
                ((ActivityLifecycleCallbacks) callbacks[i]).onActivityDestroyed(activity);
            }
        }
    }

    private Object[] collectActivityLifecycleCallbacks() {
        Object[] callbacks = null;
        synchronized (lifecycleCallbacks) {
            if (lifecycleCallbacks.size() > 0) {
                callbacks = lifecycleCallbacks.toArray();
            }
        }
        return callbacks;
    }

    public void install() {

        if (replaced) {
            return;
        }

        Class<?> activityThreadClass = forName("android.app.ActivityThread");
        if (activityThreadClass == null) {
            Log.w("Can not loadclass android.app.ActivityThread.");
            return;
        }

        Object activityThread = invoke(activityThreadClass, "currentActivityThread");
        if (activityThread == null) {
            Log.w("Can not get ActivityThread instance.");
            return;
        }

        Instrumentation instrumentation = invoke(activityThread, "getInstrumentation");
        if (instrumentation == null) {
            Log.w("Can not load class android.app.ActivityThread.");
            return;
        }

        InstrumentationWrapper instrumentationWrapper = new InstrumentationWrapper(instrumentation, this);
        if (!set(activityThread, "mInstrumentation", instrumentationWrapper)) {
            Log.w("Fail to replace field named mInstrumentation.");
            return;
        }
        replaced = true;
    }
}
