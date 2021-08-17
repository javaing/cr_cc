package com.aliee.quei.mo.ui.launch.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.aliee.quei.mo.BuildConfig
import com.aliee.quei.mo.R
import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.config.e
import com.aliee.quei.mo.data.bean.AdBean
import com.aliee.quei.mo.net.ApiConstants
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.ui.launch.vm.LaunchVModel
import com.aliee.quei.mo.ui.main.vm.AdVModel
import com.aliee.quei.mo.utils.AppStatusManager
import com.aliee.quei.mo.utils.SharedPreUtils
import com.elvishew.xlog.XLog
import kotlinx.android.synthetic.main.activity_comic_read.*
import kotlinx.android.synthetic.main.activity_launch.*
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URLDecoder
import java.util.*

/**
 * Created by Administrator on 2018/4/18 0018.
 */
//@Route(path = Path.PATH_LAUNCH_ACTIVITY)
class LaunchActivity : BaseActivity() {

    override fun getPageName() = "启动页"

    private val VM = LaunchVModel()
    private val adVm = AdVModel()

    override fun getLayoutId() = R.layout.activity_launch

    override fun onCreate(savedInstanceState: Bundle?) {
        AppStatusManager.getInstance().appStatus = AppStatusManager.AppStatusConstant.APP_NORMAL
        super.onCreate(savedInstanceState)
    }

    private var getDomainSuccess = {
        VM.doLaunch(ReaderApplication.instance) {}
    }


    private var retryTime = 0
    private fun getDomainApi() {
        XLog.enableStackTrace(1).e("获取域名第${retryTime}次")
        retryTime++
        VM.getApiDomain(getDomainSuccess, {
            Log.d("tag", "域名获取失败")
            if (retryTime < 10) doDelay({
                val backupNums: Int = SharedPreUtils.getInstance().getInt("backupNums", 0)
                for (i in 0 until backupNums) {
                    val back_oss: String = SharedPreUtils.getInstance().getString("backup_" + i)
                    VM.reGet(getDomainSuccess, {}, back_oss)
                }
            }, 1000)
        })

    }

    private fun getVDomainApi() {
        //第一個括弧是打成功之後要做的事，第二個括弧是失敗要做的事
        VM.getVApiDomain({ getDomainApi() }, {
            Log.d("tag", "VIDEO域名获取失败")
            doDelay({
                VM.reGetV({getDomainApi()}, {getDomainApi()}, ApiConstants.VOSS_PATH2)
            }, 1000)
        })
    }

    @Autowired
    @JvmField
    var url: String? = null

    override fun initData() {
        if (!url.isNullOrEmpty()) {
            url = URLDecoder.decode(url, "utf-8")
        }

    }

    fun loadAd(successCall: (MutableList<AdBean>) -> Unit) {
        adVm.getAdListK( {
            Log.d("tag", "加载广告成功:$it")
            successCall.invoke(it)
        }, {
            Log.d("tag", "無加载广告")
            successCall.invoke(mutableListOf<AdBean>())
        })

    }

    /**
     * 3秒或者配置加载成功后进入APP
     */
    private var newAdBeans = mutableListOf<AdBean>()

    @SuppressLint("CheckResult")
    override fun initView() {
        getVDomainApi()

        loadAd { ads ->
            newAdBeans = ads
        }

        doDelays(3, {
            timer.text = "$it"
        }, {
            if (newAdBeans.size <= 0) {
                newAdBeans = CommonDataProvider.instance.getFullscreenAdList()?:mutableListOf()
            }
            try {
                if (newAdBeans.size <= 0) {
                    enterApp()
                    Log.d("tag", "广告请求失败")
                } else {
                    Log.d("tag", "广告请求成功")
    //                try {
    //                    arrayOf(newAdBeans.shuffled().take(1))
    //                } catch (e:Exception) {
    //                    e.printStackTrace()
    //                }
                    LaunchAdActivity.toThis(this@LaunchActivity, newAdBeans as ArrayList<AdBean>)
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
        opUpdate()
    }

    /**
     * 进入应用
     */
    private fun enterApp() {
        if (isFinishing) return

        if (BuildConfig.futsu && !CommonDataProvider.instance.hasLogin()) {
            ARouterManager.goLoginActivity(this, url = "app://comic.hkzy.com/main/activity?showPage=0")
            finish()
            return
        }

        ARouterManager.goContentActivity(this, showPage = ARouterManager.TAB_SHOP)

        finish()
        return
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
                    VM.appUpdateOp(userInfo.id!!, 1, 1)
                    VM.updateAppop(1, it.id!!, 1)
                } else {
                    //注册用户tempuid为null，uid不为空
                    VM.appUpdateOp(userInfo.uid!!, 0, 1)
                    VM.updateAppop(1, it.uid!!, 0)
                }
            }
        }
    }
}