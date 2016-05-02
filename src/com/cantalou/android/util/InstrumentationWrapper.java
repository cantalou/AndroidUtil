package com.cantalou.android.util;

import com.cantalou.android.util.lifecycle.ActivityLifecycleCallbacks;
import com.cantalou.android.util.lifecycle.ActivityLifecycleManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

/**
 *
 * @author cantalou
 * @date 2016年4月17日 下午10:53:36
 */
@SuppressWarnings("deprecation")
public class InstrumentationWrapper extends Instrumentation {

    private Instrumentation target;

    private ActivityLifecycleManager manager;

    public InstrumentationWrapper(Instrumentation target) {
	this.target = target;
	manager = ActivityLifecycleManager.getInstance();
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
	manager.dispatchBeforeActivityOnCreate(activity, icicle);
	target.callActivityOnCreate(activity, icicle, persistentState);
	manager.dispatchActivityCreated(activity, icicle);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
	manager.dispatchBeforeActivityOnCreate(activity, icicle);
	target.callActivityOnCreate(activity, icicle);
	manager.dispatchActivityCreated(activity, icicle);
    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
	target.callActivityOnDestroy(activity);
	manager.dispatchActivityDestroyed(activity);
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
	return target.newActivity(cl, className, intent);
    }

    @Override
    public void endPerformanceSnapshot() {
	target.endPerformanceSnapshot();
    }

    @Override
    public Context getContext() {
	return target.getContext();
    }

    @Override
    public ComponentName getComponentName() {
	return target.getComponentName();
    }

    @Override
    public Context getTargetContext() {
	return target.getTargetContext();
    }

    @Override
    public boolean isProfiling() {
	return target.isProfiling();
    }

    @Override
    public void addMonitor(ActivityMonitor monitor) {
	target.addMonitor(monitor);
    }

    @Override
    public ActivityMonitor addMonitor(IntentFilter filter, ActivityResult result, boolean block) {
	return target.addMonitor(filter, result, block);
    }

    @Override
    public ActivityMonitor addMonitor(String cls, ActivityResult result, boolean block) {
	return target.addMonitor(cls, result, block);
    }

    @Override
    public void callActivityOnNewIntent(Activity activity, Intent intent) {
	target.callActivityOnNewIntent(activity, intent);
    }

    @Override
    public void callActivityOnPause(Activity activity) {
	target.callActivityOnPause(activity);
    }

    @Override
    public void callActivityOnPostCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
	target.callActivityOnPostCreate(activity, icicle, persistentState);
    }

    @Override
    public void callActivityOnPostCreate(Activity activity, Bundle icicle) {
	target.callActivityOnPostCreate(activity, icicle);
    }

    @Override
    public void callActivityOnRestart(Activity activity) {
	target.callActivityOnRestart(activity);
    }

    @Override
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState, PersistableBundle persistentState) {
	target.callActivityOnRestoreInstanceState(activity, savedInstanceState, persistentState);
    }

    @Override
    public boolean checkMonitorHit(ActivityMonitor monitor, int minHits) {
	return target.checkMonitorHit(monitor, minHits);
    }

    @Override
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
	target.callActivityOnRestoreInstanceState(activity, savedInstanceState);
    }

    @Override
    public void callActivityOnStart(Activity activity) {
	target.callActivityOnStart(activity);
    }

    @Override
    public void callActivityOnResume(Activity activity) {
	target.callActivityOnResume(activity);
    }

    @Override
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState, PersistableBundle outPersistentState) {
	target.callActivityOnSaveInstanceState(activity, outState, outPersistentState);
    }

    @Override
    public void finish(int resultCode, Bundle results) {
	target.finish(resultCode, results);
    }

    @Override
    public boolean invokeMenuActionSync(Activity targetActivity, int id, int flag) {
	return target.invokeMenuActionSync(targetActivity, id, flag);
    }

    @Override
    public boolean invokeContextMenuAction(Activity targetActivity, int id, int flag) {
	return target.invokeContextMenuAction(targetActivity, id, flag);
    }

    @Override
    public void callApplicationOnCreate(Application app) {
	target.callApplicationOnCreate(app);
    }

    @Override
    public void callActivityOnStop(Activity activity) {
	target.callActivityOnStop(activity);
    }

    @Override
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState) {
	target.callActivityOnSaveInstanceState(activity, outState);
    }

    @Override
    public void callActivityOnUserLeaving(Activity activity) {
	target.callActivityOnUserLeaving(activity);
    }

    @Override
    public Bundle getAllocCounts() {
	return target.getAllocCounts();
    }

    @Override
    public Bundle getBinderCounts() {
	return target.getBinderCounts();
    }

    @Override
    public UiAutomation getUiAutomation() {
	return target.getUiAutomation();
    }

    @Override
    public void onCreate(Bundle arguments) {
	target.onCreate(arguments);
    }

    @Override
    public void start() {
	target.start();
    }

    @Override
    public void onStart() {
	target.onStart();
    }

    @Override
    public boolean onException(Object obj, Throwable e) {
	return target.onException(obj, e);
    }

    @Override
    public void sendStatus(int resultCode, Bundle results) {
	target.sendStatus(resultCode, results);
    }

    @Override
    public void setAutomaticPerformanceSnapshots() {
	target.setAutomaticPerformanceSnapshots();
    }

    @Override
    public void startPerformanceSnapshot() {
	target.startPerformanceSnapshot();
    }

    @Override
    public void onDestroy() {

	target.onDestroy();
    }

    @Override
    public void startProfiling() {
	target.startProfiling();
    }

    @Override
    public void stopProfiling() {
	target.stopProfiling();
    }

    @Override
    public void setInTouchMode(boolean inTouch) {

	target.setInTouchMode(inTouch);
    }

    @Override
    public void waitForIdle(Runnable recipient) {
	target.waitForIdle(recipient);
    }

    @Override
    public void waitForIdleSync() {
	target.waitForIdleSync();
    }

    @Override
    public void runOnMainSync(Runnable runner) {
	target.runOnMainSync(runner);
    }

    @Override
    public Activity startActivitySync(Intent intent) {
	return target.startActivitySync(intent);
    }

    @Override
    public Activity waitForMonitor(ActivityMonitor monitor) {
	return target.waitForMonitor(monitor);
    }

    @Override
    public Activity waitForMonitorWithTimeout(ActivityMonitor monitor, long timeOut) {
	return target.waitForMonitorWithTimeout(monitor, timeOut);
    }

    @Override
    public void removeMonitor(ActivityMonitor monitor) {
	target.removeMonitor(monitor);
    }

    @Override
    public void sendStringSync(String text) {
	target.sendStringSync(text);
    }

    @Override
    public void sendKeySync(KeyEvent event) {
	target.sendKeySync(event);
    }

    @Override
    public void sendKeyDownUpSync(int key) {
	target.sendKeyDownUpSync(key);
    }

    @Override
    public void sendCharacterSync(int keyCode) {
	target.sendCharacterSync(keyCode);
    }

    @Override
    public void sendPointerSync(MotionEvent event) {
	target.sendPointerSync(event);
    }

    @Override
    public void sendTrackballEventSync(MotionEvent event) {
	target.sendTrackballEventSync(event);
    }

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
	return target.newApplication(cl, className, context);
    }

    @Override
    public Activity newActivity(Class<?> clazz, Context context, IBinder token, Application application, Intent intent, ActivityInfo info, CharSequence title, Activity parent,
	    String id, Object lastNonConfigurationInstance) throws InstantiationException, IllegalAccessException {
	return target.newActivity(clazz, context, token, application, intent, info, title, parent, id, lastNonConfigurationInstance);
    }

    @Override
    public void startAllocCounting() {
	target.startAllocCounting();
    }

    @Override
    public void stopAllocCounting() {
	target.stopAllocCounting();
    }

    public void execStartActivities(Context who, IBinder contextThread, IBinder token, Activity activity, Intent[] intents) {
	ReflectUtil.invoke(target, "execStartActivities", new Class<?>[] { Context.class, IBinder.class, IBinder.class, Activity.class, intents.getClass() }, who, contextThread,
		token, activity, intents);
    }

    @SuppressLint("NewApi")
    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Fragment fragment, Intent intent, int requestCode) {
	return (ActivityResult) ReflectUtil.invoke(target, "execStartActivity", new Class<?>[] { Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class,
		int.class }, who, contextThread, token, fragment, intent, requestCode);
    }
}
