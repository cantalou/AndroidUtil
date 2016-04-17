/**
 * 
 */
package com.cantalou.android.util.lifecycle;

import android.app.Activity;
import android.os.Bundle;

/**
 *
 * @author cantalou
 * @date 2016年4月17日 下午11:15:40
 */
public class ActivityLifecycleCallbacksAdapter implements ActivityLifecycleCallbacks {

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void beforeActivityOnCreate(Activity activity, Bundle savedInstanceState) {
    }

}
