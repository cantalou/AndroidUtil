
package com.cantalou.android.manager.lifecycle;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author cantalou
 * @date 2016年4月17日 下午10:56:16
 */
public interface ActivityLifecycleCallbacks extends android.app.Application.ActivityLifecycleCallbacks {

    public void beforeActivityOnCreate(Activity activity, Bundle savedInstanceState);

    public void onBeforeActivityCreate(Activity activity, Bundle savedInstanceState);

    @Override
    public void onActivityDestroyed(Activity activity);

    public void onActivityWindowFocusChanged(Activity activity);

    public void onSaveInstanceState(Activity activity, Bundle outState);
}
