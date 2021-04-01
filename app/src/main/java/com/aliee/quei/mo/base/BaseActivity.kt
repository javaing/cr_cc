package com.aliee.quei.mo.base

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.aliee.quei.mo.analyze.IAnalyzePage
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.utils.AppStatusManager
import com.aliee.quei.mo.widget.view.dialog.LoadingDialog
import com.bumptech.glide.Glide
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import com.umeng.analytics.MobclickAgent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


/**
 * @Author: YangYang
 * @Date: 2017/12/28
 * @Version: 1.0.0
 * @Description:
 */
abstract class BaseActivity : AppCompatActivity(), LifecycleOwner, IAnalyzePage {
    lateinit var loading: LoadingDialog

//    lateinit var huodongWM : HuodongVModel

    //    private val maxShowTime = 3
    private var isTouching = false
//    private var isCountdown = false
//    val huodongWindowList = mutableListOf<HuodongWindow>()

    private var timeObservable: Observable<Long>? = null
    private var timeDisposable: Disposable? = null

    private var notificationView: View? = null
    private var rootView: ViewGroup? = null
    private var startY: Float = 0F

    var isForground = true

    init {
//        val repository = OtherRepository()
//        huodongWM = HuodongVModel(repository)
    }

    private fun isWindowFullScreen(): Boolean {
        return true
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        ARouter.getInstance().inject(this)
    }

    private fun checkAppStatus() {
        if (AppStatusManager.getInstance().appStatus == AppStatusManager.AppStatusConstant.APP_FORCE_KILLED) {
            Log.e("BaseActivity", "切回APP时发现进程被干掉，重新启动")
            ARouterManager.goLaunchActivity(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        checkAppStatus()
        super.onCreate(savedInstanceState)
        window.setFormat(PixelFormat.TRANSLUCENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT/* && isWindowFullScreen()*/) {
            val localLayoutParams = window.attributes
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS xor localLayoutParams.flags)
        }
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
            return
        }

        setContentView(getLayoutId())
        setStatusBarBlackText()
        ARouter.getInstance().inject(this)
        loading = LoadingDialog(this)
        initView()
        initData()
    }

    fun showLoading(canCancel: Boolean = false) {
        loading.setCanCancel(canCancel)
        loading.show()
    }

    fun disLoading() {
        loading.dismiss()
    }


    private fun setStatusBarBlackText() {
        //原生
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        //小米
        setMiUIStatusBarTextColor(true)
        //魅族
        setFlymeStatusBarTextColor(true)
    }

    private fun setStatusBarWhiteText() {
        //原生
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        //小米
        setMiUIStatusBarTextColor(false)
        //魅族
        setFlymeStatusBarTextColor(false)
    }

    /**
     * 小米设置状态栏字体颜色
     *
     * @param isBlack
     */
    @SuppressLint("PrivateApi")
    private fun setMiUIStatusBarTextColor(isBlack: Boolean) {
        val clazz = this.window::class.java
        try {
            val darkModeFlag: Int
            val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
            val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            darkModeFlag = field.getInt(layoutParams)
            val extraFlagField =
                    clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
            extraFlagField.invoke(this.window, if (isBlack) darkModeFlag else 0, darkModeFlag)
        } catch (e: Exception) {
            //            Logger.e("不是MIUI");
        }
    }

    /**
     * 魅族设置状态栏字体颜色
     *
     * @param isBlack
     * @return
     */
    private fun setFlymeStatusBarTextColor(isBlack: Boolean): Boolean {
        var result = false
        if (this.window != null) {
            try {
                val lp = this.window.attributes
                val darkFlag =
                        WindowManager.LayoutParams::class.java.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                val meizuFlags = WindowManager.LayoutParams::class.java.getDeclaredField("meizuFlags")
                darkFlag.isAccessible = true
                meizuFlags.isAccessible = true
                val bit = darkFlag.getInt(null)
                var value = meizuFlags.getInt(lp)
                if (isBlack) {
                    value = value or bit
                } else {
                    value = value and bit.inv()
                }
                meizuFlags.setInt(lp, value)
                this.window.attributes = lp
                result = true
            } catch (e: Exception) {
                //                Logger.e("不是Flyme");
            }

        }
        return result
    }

    abstract fun getLayoutId(): Int

    abstract fun initData()

    abstract fun initView()

    override fun onResume() {
        super.onResume()
        isForground = true
        MobclickAgent.onResume(this)
        //   MobclickAgent.onPageStart(getPageName())
//        floatDragWindow?.updateMsgNum()
    }

    override fun onPause() {
        isForground = false
        super.onPause()
        MobclickAgent.onPause(this)
        // MobclickAgent.onPageEnd(getPageName())
    }

    fun toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
        if (isForground) {
            Toast.makeText(this, msg, duration).show()
        }
    }

    fun doDelay(task: () -> Unit, delay: Long? = null) {
        Observable.timer(delay ?: 500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .bindUntilEvent(this, Lifecycle.Event.ON_DESTROY)
                .subscribe({
                    Log.d("tag", "times::::$it")
                    task.invoke()
                }, {
                    it.printStackTrace()
                })
    }

    @SuppressLint("CheckResult")
    fun doDelays(delay: Int, time: (time: Long) -> Unit, over: () -> Unit) {
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(delay.toLong() + 1)
                .map {
                    delay - it
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ time(it) }, {}, { over.invoke() })
    }

    override fun onDestroy() {
        super.onDestroy()
       // Glide.with(applicationContext).pauseRequests();
    }
}