package com.aliee.quei.mo.ui.launch.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.BuildConfig
import com.aliee.quei.mo.R
import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.component.GsonProvider
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.data.bean.AdBean
import com.aliee.quei.mo.data.bean.AdInfo
import com.aliee.quei.mo.data.local.LoginRecordBean
import com.aliee.quei.mo.data.local.TaskBean
import com.aliee.quei.mo.database.DatabaseProvider
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.launch.vm.LaunchVModel
import com.aliee.quei.mo.ui.main.vm.AdVModel
import com.aliee.quei.mo.utils.AppStatusManager
import com.aliee.quei.mo.utils.BrightnessUtils
import com.aliee.quei.mo.utils.SharedPreUtils
import com.aliee.quei.mo.utils.extention.getResDrawable
import com.aliee.quei.mo.widget.view.dialog.LoadingDialog
import com.bumptech.glide.Glide
import com.elvishew.xlog.XLog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_comic_read.*
import kotlinx.android.synthetic.main.activity_launch.*
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread


/**
 * Created by Administrator on 2018/4/18 0018.
 */
@Route(path = Path.PATH_LAUNCH_ACTIVITY)
class LaunchActivity : BaseActivity() {
    private var isShowAd = false

    override fun getPageName() = "启动页"


    private val VM = LaunchVModel()
    private val adVm = AdVModel()
    private val realm = DatabaseProvider.getRealm()

    override fun getLayoutId() = R.layout.activity_launch

    //是否首次打开
    private var firstOpen: Boolean? = true

    override fun onCreate(savedInstanceState: Bundle?) {
        AppStatusManager.getInstance().appStatus = AppStatusManager.AppStatusConstant.APP_NORMAL
        super.onCreate(savedInstanceState)
    }

    private var getDomainSuccess = {
        VM.doLaunch(ReaderApplication.instance) {}
    }


    private var retryTime = 0
    private fun getDomainApi() {
        XLog.st(1).e("获取域名第${retryTime}次")
        retryTime++
        VM.getApiDomain(getDomainSuccess, {
            Log.d("tag", "域名获取失败")
            if (retryTime < 100) doDelay({
                val backupNums: Int = SharedPreUtils.getInstance().getInt("backupNums", 0)
                for (i in 0 until backupNums) {
                    val back_oss: String = SharedPreUtils.getInstance().getString("backup_" + i)
                    VM.reGet(getDomainSuccess, {}, back_oss)
                }
            }, 1000)
        })

     //   VM.loadUseInfo(this)
    }

    @Autowired
    @JvmField
    var url: String? = null

    override fun initData() {
        if (!url.isNullOrEmpty()) {
            url = URLDecoder.decode(url, "utf-8")
        }
        //   loadAd()
    }


    fun loadAd(successCall: (MutableList<AdBean>) -> Unit) {
        adVm.getAdList(this, {
            Log.d("tag", "加载广告成功:${it.toString()}")
            successCall.invoke(it)
        }, {
            Log.d("tag", "加载广告失败")
            successCall.invoke(mutableListOf<AdBean>())
        })
/*
        adVm.getAdList(this, 1)
        adVm.adList1LiveData.observe(this, android.arch.lifecycle.Observer {
            when (it?.status) {
                Status.Success -> {
                    val s = it.data
                    CommonDataProvider.instance.saveAdList(Gson().toJson(s))
                }
                Status.Error -> {
                    adVm.getAdList(this, 2)
                }
            }
        })

        adVm.getAdList(this, 2)
        adVm.adListLiveData.observe(this, android.arch.lifecycle.Observer {
            when (it?.status) {
                Status.Success -> {
                    val s = it.data
                    adBeans = s as ArrayList<AdBean>
                    adBeans.forEach {adBean->
                        if (AdConfig.interceptorAd(adBean)){
                            newAdBeans.add(adBean)
                        }
                    }
                }
                Status.Error -> {
                    enterApp()
                }
            }
        })*/
    }

    /**
     * 5秒或者配置加载成功后进入APP
     */
    private var newAdBeans = mutableListOf<AdBean>()

    @SuppressLint("CheckResult")
    override fun initView() {
        /*doDelay({
            enterApp()
            opUpdate()
        }, 5000)*/
        getDomainApi()
        loadAd { ads ->
            newAdBeans = ads
        }
        doDelays(3, {
            timer.text = "$it"
        }, {
            if (newAdBeans.size <= 0) {
                enterApp()
                Log.d("tag", "广告请求失败")
            } else {
                Log.d("tag", "广告请求成功")
                LaunchAdActivity.toThis(this@LaunchActivity, newAdBeans as ArrayList<AdBean>)
                finish()
            }
        })
        opUpdate()
    }

    private fun checkPushSwitchStatus() {
        val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(this);
        val isOpend = notificationManager.areNotificationsEnabled()
        if (isOpend) {
            VM.prepareLiveData.observe(this, android.arch.lifecycle.Observer {
                when (it?.status) {
                    Status.Success -> {
                        enterApp()
                    }
                }
            })
            val thread = thread {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {

                }
            }
            thread.interrupt()

        } else {
//            val text = "通知权限没有被开启，请开启"
//            val duration = Toast.LENGTH_SHORT
//            Toast.makeText(this, text, duration).show()

            val dialog = AlertDialog.Builder(this)
//                .setTitle(getString(R.string.notification_notice))
                    .setMessage(getString(R.string.notification_notice))
                    .setPositiveButton(getString(android.R.string.yes)) { dialog, which ->
                        dialog.dismiss()
                        notificatinSetting()
//                    finishAffinity()
                        thread(start = true) {
                            Thread.sleep(1000)
                            byPass()
                        }

                    }
                    .setNegativeButton(getString(android.R.string.cancel)) { dialog, which ->
                        dialog.dismiss()
                        VM.prepareLiveData.observe(this, android.arch.lifecycle.Observer {
                            when (it?.status) {
                                Status.Success -> {
                                    enterApp()
                                }
                            }
                        })
                    }.create()
            dialog.show()
        }

    }

    private fun byPass() {
        VM.prepareLiveData.observe(this, android.arch.lifecycle.Observer {
            when (it?.status) {
                Status.Success -> {
                    enterApp()
                }
            }
        })
        val thread = thread {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {

            }
        }
        thread.interrupt()
    }

    private fun notificatinSetting() {
//        tv_msg.setOnClickListener {
        val intent: Intent = Intent()
        try {
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            if (Build.VERSION.SDK_INT >= 26) {
                //8.0及以后版本使用这两个extra.  >=API 26
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, applicationInfo.uid)
            } else if (Build.VERSION.SDK_INT >= 21 || Build.VERSION.SDK_INT <= 25) {
                //5.0-7.1 使用这两个extra.  <= API 25, >=API 21
                intent.putExtra("app_package", packageName)
                intent.putExtra("app_uid", applicationInfo.uid)
            }

            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()

            //其他低版本或者异常情况，走该节点。进入APP设置界面
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.putExtra("package", packageName)

            //val uri = Uri.fromParts("package", packageName, null)
            //intent.data = uri
            startActivity(intent)
        }
//        }
    }


    /**
     * 进入应用
     */
    private fun enterApp() {
        if (isFinishing) return

        if (BuildConfig.needLogin && !CommonDataProvider.instance.hasLogin()) {
            ARouterManager.goLoginActivity(this, url = "app://comic.hkzy.com/main/activity?showPage=0")
            finish()
            return
        }

//        ARouterManager.goMainActivity(this, showPage = ARouterManager.TAB_SHOP)
//        ARouterManager.goReserveActivity(this)
        ARouterManager.goContentActivity(this, showPage = ARouterManager.TAB_SHOP)

        finish()
        return
    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }


    /**
     * 记录用户点击对更新提示的操作的接口
     */
    private fun opUpdate() {
        //是否第一次打开
        var isFirstOpen = SharedPreUtils.getInstance().saveFirstOpen
        if (isFirstOpen) {
            SharedPreUtils.getInstance().saveFirstOpen = false
            var userInfo = CommonDataProvider.instance.getUserInfo()
            //判断临时用户或者注册用户
            userInfo?.let {
                if (userInfo.tempUid != null) {
                    //临时用户tempuid不为null
                    VM.appUpdateOp(this, userInfo.id!!, 1, 1)
                    VM.updateAppop(this, 1, it.id!!, 1)
                } else {
                    //注册用户tempuid为null，uid不为空
                    VM.appUpdateOp(this, userInfo.uid!!, 0, 1)
                    VM.updateAppop(this, 1, it.uid!!, 0)
                }
            }
        }
    }
}