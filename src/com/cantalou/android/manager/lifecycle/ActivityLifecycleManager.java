/**
 * 
 */
package com.cantalou.android.manager.lifecycle;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;

/**
 *
 * @author cantalou
 * @date 2016年4月17日 下午11:06:46
 */
public class ActivityLifecycleManager {

    private ArrayList<ActivityLifecycleCallbacks> lifecycleCallbacks = new ArrayList<ActivityLifecycleCallbacks>();

    private ActivityLifecycleManager() {
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

}
