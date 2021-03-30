package com.aliee.quei.mo.base

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * @Author: YangYang
 * @Date: 2017/12/29
 * @Version: 1.0.0
 * @Description:
 */
class ActivityLifecycle constructor(private val appManager: AppManager) : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        appManager.addActivity(activity)
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {
        appManager.currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        if (appManager.currentActivity === activity) {
            appManager.currentActivity = null
        }
    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        appManager.removeActivity(activity)
    }
}