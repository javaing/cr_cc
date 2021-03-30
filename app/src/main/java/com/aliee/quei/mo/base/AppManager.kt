package com.aliee.quei.mo.base

import android.app.Activity
import android.content.Intent
import com.aliee.quei.mo.application.ReaderApplication
import java.util.*

/**
 * @Author: YangYang
 * @Date: 2017/12/29
 * @Version: 1.0.0
 * @Description:
 */
class AppManager constructor(private var application: ReaderApplication?) {

    //管理所有activity
    var mActivityList: MutableList<Activity>? = null
    //当前在前台的activity
    var currentActivity: Activity? = null

    /**
     * 让在前台的activity,打开下一个activity
     * @param intent
     */
    fun startActivity(intent: Intent) {
        if (currentActivity == null) {
            //如果没有前台的activity就使用new_task模式启动activity
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            application!!.startActivity(intent)
            return
        }
        currentActivity!!.startActivity(intent)
    }

    /**
     * 让在前台的activity,打开下一个activity

     * @param activityClass
     */
    fun startActivity(activityClass: Class<*>) {
        startActivity(Intent(application, activityClass))
    }

    /**
     * 释放资源
     */
    fun release() {
        mActivityList!!.clear()
        mActivityList = null
        currentActivity = null
        application = null
    }

    /**
     * 返回一个存储所有未销毁的activity的集合
     * @return
     */
    fun getActivityList(): MutableList<Activity>? {

        if (mActivityList == null) {
            mActivityList = mutableListOf()
        }
        return mActivityList
    }


    /**
     * 添加Activity到集合
     */
    fun addActivity(activity: Activity) {
        if (mActivityList == null) {
            mActivityList = LinkedList<Activity>()
        }
        synchronized(AppManager::class.java) {
            if (!mActivityList!!.contains(activity)) {
                mActivityList!!.add(activity)
            }
        }
    }

    /**
     * 删除集合里的指定activity
     * @param activity
     */
    fun removeActivity(activity: Activity) {
        if (mActivityList == null) {
            return
        }
        synchronized(AppManager::class.java) {
            if (mActivityList!!.contains(activity)) {
                mActivityList!!.remove(activity)
            }
        }
    }

    /**
     * 删除集合里的指定位置的activity
     * @param location
     */
    fun removeActivity(location: Int): Activity? {
        if (mActivityList == null) {
            return null
        }
        synchronized(AppManager::class.java) {
            if (location > 0 && location < mActivityList!!.size) {
                return mActivityList!!.removeAt(location)
            }
        }
        return null
    }

    /**
     * 关闭指定activity
     * @param activityClass
     */
    fun killActivity(activityClass: Class<*>) {
        if (mActivityList == null) {
            return
        }
        mActivityList!!
                .filter { it.javaClass == activityClass }
                .forEach { it.finish() }
    }


    /**
     * 指定的activity实例是否存活
     * @param activity
     * *
     * @return
     */
    fun activityInstanceIsLive(activity: Activity): Boolean {
        if (mActivityList == null) {
            return false
        }
        return mActivityList!!.contains(activity)
    }


    /**
     * 指定的activity class是否存活(一个activity可能有多个实例)
     * @param activityClass
     * *
     * @return
     */
    fun activityClassIsLive(activityClass: Class<*>): Boolean {
        if (mActivityList == null) {
            return false
        }

        return mActivityList!!.any {
            it.javaClass == activityClass
        }
    }


    /**
     * 关闭所有activity
     */
    fun killAll() {

        val iterator = mActivityList!!.iterator()
        while (iterator.hasNext()) {
            iterator.next().finish()
            iterator.remove()
        }
    }


    /**
     * 退出应用程序
     */
    fun appExit() {
        try {
            killAll()
            if (mActivityList != null)
                mActivityList = null
            System.exit(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}