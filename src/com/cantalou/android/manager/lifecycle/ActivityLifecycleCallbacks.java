/**
 * 
 */
package com.cantalou.android.manager.lifecycle;

import android.app.Activity;
import android.os.Bundle;

/**
 *
 * @author cantalou
 * @date 2016年4月17日 下午10:56:16
 */
public interface ActivityLifecycleCallbacks extends android.app.Application.ActivityLifecycleCallbacks {

    void beforeActivityOnCreate(Activity activity, Bundle savedInstanceState);

}
