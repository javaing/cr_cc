package com.aliee.quei.mo.application

//import com.squareup.leakcanary.LeakCanary
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
/*
import cn.jpush.android.api.JPushInterface
*/
import com.alibaba.android.arouter.launcher.ARouter
import com.aliee.quei.mo.utils.rxjava.RxBus
import com.dueeeke.videoplayer.BuildConfig
import com.dueeeke.videoplayer.ijk.IjkPlayerFactory
import com.dueeeke.videoplayer.player.VideoViewConfig
import com.dueeeke.videoplayer.player.VideoViewManager
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.aliee.quei.mo.base.ActivityLifecycle
import com.aliee.quei.mo.base.AppManager
import com.aliee.quei.mo.component.EventRefreshHome
import com.aliee.quei.mo.data.Channel
import com.aliee.quei.mo.data.bean.UserInfoBean
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.ui.launch.activity.LaunchActivity
import com.dueeeke.videoplayer.player.AndroidMediaPlayerFactory
import com.tencent.mmkv.MMKV
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import io.reactivex.plugins.RxJavaPlugins
import io.realm.Realm

/**
 * @Author: YangYang
 * @Date: 2018/1/2
 * @Version: 1.0.0
 * @Description:
 */
class ReaderApplication : Application(), LifecycleOwner, Application.ActivityLifecycleCallbacks {
    var currentVisibleActivity: Activity? = null

    override fun onActivityPaused(activity: Activity?) {
        currentVisibleActivity = null

    }

    override fun onActivityResumed(activity: Activity?) {
        currentVisibleActivity = activity
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
        if (activity is LaunchActivity) {
            //刷新
            RxBus.getInstance().post(EventRefreshHome())
        } else {
            //不刷新
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }

    val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    lateinit var appManager: AppManager

    lateinit var activityLifecycle: ActivityLifecycle


//    lateinit var globalEventHandler : GlobalEventHandler

    override fun onCreate() {
        super.onCreate()
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
        instance = this


        initAppComponent()

        initDatabase()

        initNet()

        this.registerActivityLifecycleCallbacks(activityLifecycle)

        XLog.init(LogConfiguration.Builder().tag("COMIC").b().logLevel(LogLevel.NONE/*if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.NONE*/).build())

        initEvent()

        initBoxing()

        //  initStrict()

        initUmeng()

        initLeakCanary()

//        initRefreshConfig()

        initGlobalActivity()

//        initKefu()
        initARouter()

        initMMKV()

        initJpush()

        initIjk()

        RxJavaPlugins.setErrorHandler {
            //异常处理
            Log.d("RxJavaPlugins","it:${it.message}")
        }
    }

    private fun StrictMode() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    private fun initJpush() {
       /* JPushInterface.setDebugMode(true)
        JPushInterface.init(this)
        jpushid = JPushInterface.getRegistrationID(this)*/
    }

    private fun initMMKV() {
        MMKV.initialize(this)
    }

    private fun initARouter() {
       /* if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }*/
        ARouter.init(this)
    }

    private fun initGlobalActivity() {
        registerActivityLifecycleCallbacks(this)
    }

    private fun initUmeng() {
       // UMConfigure.init(this, "5df1f468570df3fef6000e7f", Channel.channelName, UMConfigure.DEVICE_TYPE_PHONE, null)
        //UMConfigure.init(this, "5f61cc9fa4ae0a7f7d067d43", Channel.channelName, UMConfigure.DEVICE_TYPE_PHONE, null)
        UMConfigure.init(this, com.aliee.quei.mo.BuildConfig.UMENG_APPID, Channel.channelName, UMConfigure.DEVICE_TYPE_PHONE, null)

        UMConfigure.setLogEnabled(true)
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL)
        MobclickAgent.openActivityDurationTrack(false)
    }

    private fun initLeakCanary() {
        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);*/
    }

    private fun initStrict() {
        /*if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }*/
    }

    private fun initBoxing() {
//        val loader = BoxingGlideLoader()
//        BoxingMediaLoader.getInstance().init(loader)
//        BoxingCrop.getInstance().init(BoxingUcrop())
    }

    @SuppressLint("CheckResult")
    private fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is UserInfoBean -> {
                        MobclickAgent.onProfileSignIn("id", it.id.toString())
                    }
                }
            }, { it.printStackTrace() })
    }

    private fun initNet() {
        RetrofitClient.initBaseParams(this)
    }

    private fun initDatabase() {
        Realm.init(this)
    }

    private fun initAppComponent() {
        appManager = AppManager(this)
        activityLifecycle = ActivityLifecycle(appManager)
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        // you must install multiDex whatever tinker is installed!
//        MultiDex.install(base)
    }


    /**
     * 程序终止的时候执行
     */
    override fun onTerminate() {

        super.onTerminate()
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED)
        this.appManager.release()
    }

    companion object {
        lateinit var jpushid: String
        lateinit var instance: ReaderApplication

        var freeTime = 0L

        fun getCurProcessName(context: Context): String? {
            val pid = android.os.Process.myPid()
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (appProcess in activityManager.runningAppProcesses) {
                if (appProcess.pid == pid) {
                    return appProcess.processName
                }
            }
            return null
        }
    }

    private fun initIjk(){
        //播放器配置，注意：此为全局配置，按需开启
        VideoViewManager.setConfig(VideoViewConfig.newBuilder()
                .setLogEnabled(false) //调试的时候请打开日志，方便排错
                .setPlayerFactory(IjkPlayerFactory.create()) //
                //                .setRenderViewFactory(SurfaceRenderViewFactory.create())
                //                .setEnableOrientation(true)
                //                .setEnableAudioFocus(false)
                //                .setScreenScaleType(VideoView.SCREEN_SCALE_MATCH_PARENT)
                //                .setAdaptCutout(false)
                //                .setPlayOnMobileNetwork(true)
                //                .setProgressManager(new ProgressManagerImpl())
                .build())
    }
}