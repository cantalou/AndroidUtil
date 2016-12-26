package com.cantalou.android.manager.system;

import android.app.Instrumentation;
import android.content.Context;
import android.os.Looper;

import com.cantalou.android.util.InstrumentationWrapper;
import com.cantalou.android.util.Log;

import static com.cantalou.android.util.ReflectUtil.forName;
import static com.cantalou.android.util.ReflectUtil.invoke;
import static com.cantalou.android.util.ReflectUtil.set;

/**
 * 1.实现对Instrumentation类的代理<br>
 * 2.修复系统内存泄漏问题<br>
 * 3.修复系统bug
 *
 * @author cantalou
 * @date 2016年4月17日 下午10:48:05
 */
public final class InstrumentationCompat{

    private static boolean replaced;

    public static void install(Context context) {

        if(replaced){
            return ;
        }

        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new RuntimeException("Method can only be called in the main thread");
        }

        Class<?> activityThreadClass = forName("android.app.ActivityThread");
        if (activityThreadClass == null) {
            Log.w("Can not load class android.app.ActivityThread.");
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

        if(instrumentation instanceof InstrumentationWrapper){
            Log.w("Field mInstrumentation had replaced, ignore.");
            return;
        }

        InstrumentationWrapper instrumentationWrapper = new InstrumentationWrapper(instrumentation);
        if (!set(activityThread, "mInstrumentation", instrumentationWrapper)) {
            Log.w("Fail to replace field named mInstrumentation.");
            return;
        }
        replaced = true;
    }
}
