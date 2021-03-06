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
import com.aliee.quei.mo.data.bean.ChannelHideBean
import com.aliee.quei.mo.data.bean.UserInfoBean
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.ui.launch.activity.LaunchActivity
import com.aliee.quei.mo.utils.AES
import com.aliee.quei.mo.utils.SharedPreUtils
import com.dueeeke.videoplayer.player.AndroidMediaPlayerFactory
import com.meituan.android.walle.WalleChannelReader
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
            //??????
            RxBus.getInstance().post(EventRefreshHome())
        } else {
            //?????????
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
            //????????????
            Log.d("RxJavaPlugins","it:${it.message}")
        }

        aes.init(password)
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

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        // you must install multiDex whatever tinker is installed!
//        MultiDex.install(base)
    }


    /**
     * ???????????????????????????
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
        //????????????????????????????????????????????????????????????
        VideoViewManager.setConfig(VideoViewConfig.newBuilder()
                .setLogEnabled(false) //?????????????????????????????????????????????
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

    fun updateIsHideVideo(bean:List<ChannelHideBean>) {
        var channelId = Channel.channelName
        Log.d("tag", "updateIsHideVideo channelId:$channelId")
        Log.d("tag", "updateIsHideVideo channelId:${bean}")
        val filter = bean.filter { it.id==channelId.toInt()}
        var isHideVideo= false
        if(filter.isNotEmpty()) {
            //1:app, 2:wap, 3:all from Paul
            isHideVideo = filter[0].hideVideo==1||filter[0].hideVideo==3
        }
        SharedPreUtils.getInstance().putBoolean(SharedPreUtils.Key_ISHIDEVIDEO, isHideVideo)

        //for TEST
        //SharedPreUtils.getInstance().putBoolean(SharedPreUtils.Key_ISHIDEVIDEO, true)
    }

    val aes = AES()
    val password = "0123456789abcdef".toByteArray()
}