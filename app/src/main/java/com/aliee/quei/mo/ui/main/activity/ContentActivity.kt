package com.aliee.quei.mo.ui.main.activity

import android.Manifest
import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Handler
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.aliee.quei.mo.BuildConfig
import com.aliee.quei.mo.R
import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.component.*
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.config.AdEnum
import com.aliee.quei.mo.data.bean.AdBean
import com.aliee.quei.mo.data.bean.UserInfoBean
import com.aliee.quei.mo.data.bean.VersionInfoBean
import com.aliee.quei.mo.data.exception.RequestException
import com.aliee.quei.mo.net.imageloader.glide.GlideApp
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.service.VersionUpdate
import com.aliee.quei.mo.ui.launch.vm.LaunchVModel
import com.aliee.quei.mo.ui.main.adapter.FragmentAdapter
import com.aliee.quei.mo.ui.main.vm.AdVModel
import com.aliee.quei.mo.ui.main.vm.MainVModel
import com.aliee.quei.mo.ui.upgradle.UpgradeActivity
import com.aliee.quei.mo.utils.ScreenUtils
import com.aliee.quei.mo.utils.SharedPreUtils
import com.aliee.quei.mo.utils.extention.*
import com.aliee.quei.mo.utils.rxjava.RxBus
import com.aliee.quei.mo.widget.view.bottombar.BottomBar
import com.aliee.quei.mo.widget.view.bottombar.BottomBarTab
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dueeeke.videoplayer.player.VideoViewManager
import com.elvishew.xlog.XLog
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper
import com.google.gson.Gson
import com.taobao.sophix.SophixManager
import kotlinx.android.synthetic.main.activity_comic_read.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.layoutDailyReward
import kotlinx.android.synthetic.main.activity_main.tvCoinsGive
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Administrator on 2018/4/18 0018.
 */
@Route(path = Path.PATH_CONTENT_ACTIVITY, name = "主界面")
class ContentActivity : BaseActivity() {
    private val adVm = AdVModel()
    private val VM = MainVModel()
    private val launchModel = LaunchVModel()
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    var mCurrentIndex = 0

    /**
     * 打开首页后 随即打开的页面
     */
    @Autowired
    @JvmField
    var url: String? = null

    /**
     * 显示第几个tab
     */
    @Autowired
    @JvmField
    var showPage: Int = 0

    /**
     * 书架、历史
     */
    @Autowired
    @JvmField
    var showTab: Int = 0

    var coins: Int = 0

    override fun getPageName() = "首页"

    val MAIN_PAGE_FIRST = 0
    val MAIN_PAGE_SECOND = 1
    val MAIN_PAGE_THIRD = 2
    val MAIN_PAGE_FOURTH = 3

    private var prePage: Int = MAIN_PAGE_FIRST
    private lateinit var settings: SharedPreferences
    private val listFragment: MutableList<BaseFragment> = mutableListOf()
    private lateinit var fragmentAdapter: FragmentAdapter

    override fun getLayoutId() = R.layout.activity_main
    private val tabSelectedListener: BottomBar.OnTabSelectedListener = object : BottomBar.OnTabSelectedListener {
        override fun onTabSelected(position: Int, prePosition: Int) {
            Log.d("tag", "onTabSelected:${position},prePostion:${prePosition}")
            if (prePosition == 0 && position == 3) {
                //漫画首页到个人中心
                RxBus.getInstance().post(EventToMineFrom(true))
            } else if (prePosition == 2 && position == 3) {
                //视频首页到个人中心
                RxBus.getInstance().post(EventToMineFrom(false))
            }

            if (prePosition == 2 || prePosition == 3) {
                //移除正在播放的播放器
                VideoViewManager.instance().releaseByTag("list")
            }
            // if (position != 4) {R
            prePage = prePosition
            showPage = position
            showPage()
            /*  } else {
                    val dialog = AlertDialog.Builder(this@ContentActivity)
                            .setMessage(getString(R.string.csenter))
                            .setPositiveButton(getString(R.string.confirm)) { dialog, which ->
                                dialog.dismiss()
                                ARouterManager.goCustomServiceActivity(this@ContentActivity)
                            }
                            .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                                dialog.dismiss()

                            }.create()
                    dialog.show()
                }*/
        }

        override fun onTabUnselected(position: Int) {
        }

        override fun onTabReselected(position: Int) {
//            listFragment.getOrNull(position)?.scrollToTop()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d("MainActivity", "Intent showPage:" + showPage)
        showPage()
        if (!url.isNullOrEmpty()) {
            ARouter.getInstance().build(url).navigation(this)
        }
    }


    private var isFirstShow = true

    @SuppressLint("CheckResult")
    override fun initData() {
        val installTime = SharedPreUtils.getInstance().getLong("installTime")
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val installDay = sdf.format(Date(installTime))
        val today = sdf.format(Date())
        isFirstShow = installDay == today

        val p = ScreenUtils.getScreenSize(this)
        screenWidth = p.x
        screenHeight = p.y

        showPage()

        intent?.let {
            showPage()
        }
        intent?.let {
            if (!url.isNullOrEmpty()) {
                ARouter.getInstance().build(Uri.parse(url))
                        .navigation(this)
            }
        }

        if (!SharedPreUtils.getInstance().getBoolean("hasOpenChannelChapter", false)) {
            VM.getUserChannelInfo(this)
        }


        getVersionUpdate()
//        adZoneCheck()

        val chapter: String = ""
//        WxAdCheck()
//        VM.getWxAttention(this, installTime, chapter)

//        VM.versionCheck(this)


        val token = CommonDataProvider.instance.getRegisterToken()
        if (token.isEmpty()) {
            launchModel.retryRegisterToken(this)
        }

        var userInfo = CommonDataProvider.instance.getUserInfo()
        if (userInfo == null) {
            launchModel.retryUserInfo(this) {
                userInfo = it
            }
        }
        userInfo?.let {
            if (userInfo?.tempUid != null) {
                //临时用户tempuid不为null
                VM.updateAppget(this, userInfo?.id!!, 1)
            } else {
                //注册用户tempuid为null，uid不为空
                VM.updateAppget(this, userInfo?.uid!!, 0)
            }
        }
        VM.getVideoDomain(this)
        VM.videoMemberInfo(this)
        VM.mainTags(this)
        VM.autoPlay(this)
        VM.getSignAd(this)

       adVm.getAdList(this, 1)

    }


    override fun initView() {

        initClick()
        initBottomBar()
        initFragment()
        initEvent()
        fragmentAdapter = FragmentAdapter(supportFragmentManager, listFragment)
        viewPager.adapter = fragmentAdapter
        viewPager.offscreenPageLimit = listFragment.size
        initVM()

    }

    @SuppressLint("CheckResult")
    private fun initVM() {
//        VM.checkInLiveData.observe(this, Observer {
//            when (it?.status) {
//                Status.Success -> {
//                    val bean = it.data ?: return@Observer
//                    showSignNotification(bean)
//                    Log.d("MainActivity", "checkInLiveData:"+bean)
//                    ARouterManager.goDailyLoginActivity(this, bean)
//                }
//                Status.Error -> {
//                    VM.getReadHistory(this)
//                }
//            }
//        })
        VM.signLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    val list = it.data ?: return@Observer
                    val coin = list.bookbean!!
                    coins = coin.toInt()
//                    XLog.st(0).e("SignCoin = $coins")
                    //     ARouterManager.goDailyLoginActivity(this, coins)
                    if (isFirstShow) {
                        showSignNotification(coins)
                    } else {
                        ARouterManager.goDailyLoginActivity(this, coins)
                    }
                }
                Status.Error -> {
                    val e = it.e
                    if (e is RequestException) {
                        if (e.code == 1014) {
                            showBulletin()
                        }
                    }
                }
            }
        })

        launchModel.registerTokenLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    VM.getReadHistory(this)
                }
            }
        })
        VM.wxNumberLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    val list = it.data ?: return@Observer
                    val Content = list.content
                    val WxNumber = list.wxNumber
                    val Coins = list.bookbean
//                    Log.d("MainActivity", "WxContent:"+Content)
                    Log.d("MainActivity", "WxNumber:" + WxNumber)
                    tvWelfare_content.text = Content
                    tvWxnumber.text = WxNumber
                    tvIns2.text =
                            "2、打开微信→添加朋友→公众号→输入“" + WxNumber + "”" + "→搜索并关注，即可領取" + Coins + "书币"
                    if (WxNumber!!.isNotEmpty() || WxNumber!!.isNotBlank()) {
                        showSignNotification(Coins!!.toInt())
                    }
                }
            }
        })
        settings = getSharedPreferences("DATA", 0)
        VM.versionLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    var versionInfo = it.data ?: return@Observer
                    versionInfo.version ?: return@Observer

                    try {
                        settings.edit()
                                .putString("appVersion", versionInfo.descr)
                                .putString("apkPath", versionInfo.url)
                                .apply()
                        val isForce: Int = versionInfo.isForce
                        val appVersion_desc = versionInfo.descr
                        Log.d("MainActivity", "appDescr:" + versionInfo.descr)
                        //val appVersion_ori = appVersion_desc!!.substring(3, 8)
//                    Log.d("MainActivity", "Backend Version:" + appVersion_ori)
                        if (versionCompare(versionInfo.version)) {
                            var hotFix = SharedPreUtils.getInstance().getInt("hotfix", 0)
                            if (hotFix == 1) {
                                SophixManager.getInstance().queryAndLoadNewPatch()
                            }
                            if (isForce == 1) {
                                UpgradeActivity.start(this, versionInfo)
                            } else {
                                UpgradeActivity.start(this, versionInfo)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })
        VM.channelInfoLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    SharedPreUtils.getInstance().putBoolean("hasOpenChannelChapter", true)
                    val bean = it.data ?: return@Observer
                    val bookId = bean.cartoon_id
                    val chapterId = bean.chapter_id
                    if (bookId > 0 && chapterId > 0) {
                        ARouterManager.goReadActivity(this, bookId, chapterId)
                        return@Observer
                    }
                    if (bookId > 0) {
                        ARouterManager.goComicDetailActivity(this, bookId)
                        return@Observer
                    }
                    val channelInfo = it.data ?: return@Observer
                    val version = BuildConfig.VERSION_NAME
                }
            }
        })
        VM.bulletinLiveData.observe(this, Observer {
            /*val calendar = Calendar.getInstance()
            val day = calendar[Calendar.DAY_OF_MONTH]
            var isShow = CommonDataProvider.instance.getBulletinDialogShow()
            val cacheDay = CommonDataProvider.instance.getSaveShowDay()*/
            when (it?.status) {
                Status.Success -> {
                    val bean = it.data
                    val img = bean?.imagePath
                    val type: Int? = bean?.type
                    val imgUrl = "${CommonDataProvider.instance.getImgDomain()}$img"
                    if (!img.isNullOrEmpty()) {
                        bulletin_image.loadNovelCover(imgUrl)
                        bulletin_layout.visibility = View.VISIBLE
                        //获取系统时间，每日24点可以弹出。其余时间不弹出
                        /*if (cacheDay.toInt() == day) {
                            return@Observer
                        } else {
                            isShow = true
                        }
                        if (isShow and (cacheDay.toInt() != day)) {
                            bulletin_layout.visibility = View.VISIBLE
                        } else {
                            bulletin_layout.visibility = View.GONE
                        }*/
                        bulletin_layout.setOnTouchListener(View.OnTouchListener { view, motionEvent -> true })
                        if (type!! == 1) {
                            bulletin_image.click {
                                prePage = 2
                                showPage = 2
                                showPage()
                                bulletin_layout.visibility = View.GONE
                                // CommonDataProvider.instance.saveBulletinDialogShow("false")
                                // CommonDataProvider.instance.saveShowDay(day.toString())
                            }
                        }
                    }
                }
            }
            bulletin_cancel.click {
                bulletin_layout.visibility = View.GONE
                //CommonDataProvider.instance.saveBulletinDialogShow("false")
                //CommonDataProvider.instance.saveShowDay(day.toString())
            }
        })

        VM.adZoneLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    val list = it.data ?: return@Observer
                    if (list.isEmpty()) return@Observer
                    val adZone = list.toString()
                    Log.d("MainActivity", "Adzone:" + adZone)
                }
            }
        })

        VM.historyLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    val list = it.data ?: return@Observer
                    if (list.isEmpty()) return@Observer
                    val history = list[0]
                    val hasOpenChannelChapter =
                            SharedPreUtils.getInstance().getBoolean("hasOpenChannelChapter", false)
                    if (!hasOpenChannelChapter) {
//                        ARouterManager.goReadActivity(this,history.bookid,history.chapterId)
//                        SharedPreUtils.getInstance().putBoolean("hasOpenChannelChapter",true)
//                        return@Observer
                    }

                    val dialog = AlertDialog.Builder(this)
                            .setTitle(getString(R.string.dialog_notice))
                            .setMessage(
                                    getString(
                                            R.string.dialog_continue_last_read,
                                            history.title,
                                            history.chapterName
                                    )
                            )
                            .setPositiveButton(getString(android.R.string.yes)) { dialog, which ->
                                dialog.dismiss()
                                ARouterManager.goReadActivity(this, history.id, history.chapterId)
                            }
                            .setNegativeButton(getString(android.R.string.cancel)) { dialog, which ->
                                dialog.dismiss()
                            }.create()
                    dialog.show()
                }
                Status.TokenError -> {
                    launchModel.registerToken(this)
                }
            }
        })
        VM.wxAttentionLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    val bean = it.data ?: return@Observer
                    val show = bean.isRecivable
//                        Log.d("MainActivity", "IsWXAttention:"+show)
                    XLog.st(1).e("IsWXAttention = $show")
                    SharedPreUtils.getInstance().putBoolean("IsWXAttention", bean.isRecivable)
//                    checkWelfare()
                }

            }
        })

        VM.appDrainageLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    it?.let {
                        if (!it!!.data.equals("")) {
                            //取一个临时常量 判断是否为导流渠道下载 version:-200
                            val userInfo = CommonDataProvider.instance.getUserInfo()
                            if (userInfo != null) {
                                UpgradeActivity.start(this, VersionInfoBean(
                                        "-200",
                                        "",
                                        it!!.data,
                                        null,
                                        "",
                                        0,
                                        userInfo
                                ))
                            }
                        }
                    }
                    VM.getReadHistory(this)
                }
                Status.Error -> {

                }
                Status.NoNetwork -> {

                }
            }
        })
        VM.versionLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    val versionInfo = it.data ?: return@Observer
                    versionInfo.version ?: return@Observer

                    settings.edit()
                            .putString("appVersion", versionInfo.descr)
                            .putString("apkPath", versionInfo.url)
                            .apply()
                    val isForce: Int = versionInfo.isForce
                    val appVersion_desc = versionInfo.descr
                    Log.d("MainActivity", "appDescr:" + versionInfo.descr)
                    try {
                        // val appVersion_ori = appVersion_desc!!.substring(3, 8)
//                      Log.d("MainActivity", "Backend Version:" + appVersion_ori)
                        if (versionCompare(versionInfo.version)) {
                            var hotFix = SharedPreUtils.getInstance().getInt("hotfix", 0)
                            if (hotFix == 1) {
                                SophixManager.getInstance().queryAndLoadNewPatch()
                            }
                            if (isForce == 1) {
                                UpgradeActivity.start(this, versionInfo)
                            } else {
                                UpgradeActivity.start(this, versionInfo)
                            }
                            /*if (isForce == 1) {
                                 UpgradeActivity.start(this, versionInfo)
                             } else {
 //                            checkWelfare()
 //                            val app_name = "pipiComic"
 //                            actIntentUpdate(app_name,versionInfo.url)
                             }*/
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

        })




        VM.videoDomainData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    /* Log.d("MainActivity","Video Url"+it.data!!.data)
                     val json: String = GsonProvider.gson.toJson(it.data!!.data)
                     Log.d("MainActivity","Video Url json"+json)
                     val videoData = JSONObject(json).getJSONArray("1");
                     val translations: JSONObject = videoData.getJSONObject(0)
                     val videoUrl = translations.getString("domain")*/
                    Log.d("tag", "domen:${it.data!!.`1`[0].domain}")
                    CommonDataProvider.instance.saveVideoDomain(it.data!!.`1`[0].domain)
                }
            }
        })

        VM.updateAppgetLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    val appupdate = it.data!!
                    val userInfo = CommonDataProvider.instance.getUserInfo()
                    val versionInfo = VersionInfoBean(appupdate.version, appupdate.descr, appupdate.url, appupdate.descImgUrl, appupdate.button, appupdate.isForce!!, userInfo!!)
                    UpgradeActivity.start(this, versionInfo)
                }
            }
        })

        //广告列表
        adVm.adList1LiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    val adList = it.data!!
               //     CommonDataProvider.instance.saveAdList(Gson().toJson(adList))
                    adList.forEach { adBean ->
                        if (adBean.id == AdEnum.BOTTOM_BAR.zid) {
                            Log.d("tag","hahahah:${adBean.toString()}")
                            //下方悬浮广告
                            bottomAd(adBean)
                        } else if (adBean.id == AdEnum.HOME_DIALOG.zid) {
                            //弹窗广告
                            dialogAd(adBean)
                        }
                    }
                }
            }
        })
    }

    /**
     *  弹窗广告
     */
    fun dialogAd(adBean: AdBean) {
        AdConfig.getAdInfo(adBean, { adInfo ->
            runOnUiThread {
                rl_home_ad.visibility = View.VISIBLE
                //加载
                iv_home_ad.loadHtmlImg(adInfo.imgurl)
                //曝光统计
                AdConfig.adPreview(adInfo.callbackurl)
                iv_home_ad.click {
                    AdConfig.adClick(this, adInfo.clickurl)
                }

                iv_home_close.click {
                    rl_home_ad.visibility = View.GONE
                }
            }
        }, {

        })
    }

    /**
     * 底部悬浮广告
     */
    private fun bottomAd(adBean: AdBean) {
        AdConfig.getAdInfo(adBean, { adInfo ->
            runOnUiThread {
                if (AdConfig.isClosed(adBean.close)) {
                    ad_iv_close.show()
                    ad_iv_close.click {
                        rl_bottom_ad.gone()
                    }
                } else {
                    ad_iv_close.gone()
                }
                rl_bottom_ad.show()
                //加载
                Log.d("tag", "gif url:${adInfo.imgurl}")
                iv_bottom_ad.loadHtmlImg(adInfo.imgurl)
                //曝光统计
                AdConfig.adPreview(adInfo.callbackurl)
                iv_bottom_ad.click {
                    AdConfig.adClick(this, adInfo.clickurl)
                }
            }
        }, {})
    }

    private fun actIntentUpdate(appName: String, downUrl: String?) {
        var intent = Intent(this, VersionUpdate::class.java)
        intent.putExtra("appName", appName)
        intent.putExtra("down_url", downUrl)
        startService(intent)
    }


    private val handler = Handler()
    private val runnable = Runnable {
        //延迟4秒隐藏顶部
        hideNotificationSignIn()
        showBulletin()
    }

    private fun showBulletin() {
        val imageDomain = CommonDataProvider.instance.getImgDomain()
        if (imageDomain.isEmpty()) {
            launchModel.retryImgDomain(this) {
                VM.getBulletin(this)
            }
        } else {
            VM.getBulletin(this)
        }
    }

    private var mTopInAnim: Animation? = null
    private var mTopOutAnim: Animation? = null
    private fun showSignNotification(amount: Int) {
        if (mTopInAnim == null) {
            mTopInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_in)
            mTopInAnim?.duration = 200
        }
        layoutDailyReward.show()
        layoutDailyReward.startAnimation(mTopInAnim)
        tvCoinsGive.text = getString(R.string.daily_first_login_reward, amount)

        handler.postDelayed(runnable, 4000)

        /* layoutNotificaiton.show()
         layoutNotificaiton.startAnimation(mTopInAnim)*
 //        tvNotification.text = "亲，欢迎回来。系统已赠送$amount 金币到您的帐户，请注意查收~"
 //        doDelay({ hideNotification() }, 5000)
         ivCancel.click {
             hideNotification()
         }

         val packageName = "com.tencent.mm"
         ivForward.click {
             copyText(ivForward)
             launchApp(packageName)

         }*/
    }

    private fun hideNotificationSignIn() {
        if (mTopOutAnim == null) {
            mTopOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_out)
            mTopOutAnim?.duration = 200
        }
        layoutDailyReward.startAnimation(mTopOutAnim)
        layoutDailyReward.gone()
    }

    private var isOutPlaying = false
    private fun hideNotification() {
        if (isOutPlaying) return
        if (mTopOutAnim == null) {
            mTopOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_out)
            mTopOutAnim?.duration = 200
            mTopOutAnim?.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    isOutPlaying = false
                }

                override fun onAnimationStart(animation: Animation?) {
                    isOutPlaying = true
                }
            })
        }

        layoutNotificaiton.startAnimation(mTopOutAnim)
        layoutNotificaiton.gone()
    }

    private fun initEvent() {

        RxBus.getInstance().toMainThreadObservable(ReaderApplication.instance, Lifecycle.Event.ON_DESTROY).subscribe({
            when (it) {
                is EventHideBottomBar -> {
                    hideTab()
                }

            }
        }, {
            it.printStackTrace()
        })

        RxBus.getInstance().toMainThreadObservable(ReaderApplication.instance, Lifecycle.Event.ON_DESTROY).subscribe({
            when (it) {
                is EventShowBottomBar -> {
                    showTab()
                }

            }
        }, {
            it.printStackTrace()
        })

        RxBus.getInstance().toMainThreadObservable(ReaderApplication.instance, Lifecycle.Event.ON_DESTROY).subscribe({
            when (it) {
                is EventToMine -> {
                    prePage = 2
                    showPage = 2
                    showPage()
//                    showTab()
                }

            }
        }, {
            it.printStackTrace()
        })


        RxBus.getInstance().toMainThreadObservable(ReaderApplication.instance, Lifecycle.Event.ON_DESTROY).subscribe({
            when (it) {
                is EventToRecharge -> {
                    ARouterManager.goRechargeActivity(this, "", 0)
                }

            }
        }, {
            it.printStackTrace()
        })

        RxBus.getInstance().toMainThreadObservable(ReaderApplication.instance, Lifecycle.Event.ON_DESTROY).subscribe({
            when (it) {
                is EventToBulletin -> {
                    ARouterManager.goBulletinActivity(this)
                }
            }
        }, {
            it.printStackTrace()
        })
        RxBus.getInstance().toMainThreadObservable(ReaderApplication.instance, Lifecycle.Event.ON_DESTROY).subscribe({
            when (it) {
                is EventReturnComic -> {
                    showPage = 0
                    showPage()
                }
            }
        }, {
            it.printStackTrace()
        })

        RxBus.getInstance().toMainThreadObservable(ReaderApplication.instance, Lifecycle.Event.ON_DESTROY).subscribe({
            when (it) {
                is EventToReLogin -> {
                    Log.d("tag", "重新获取token")
                    launchModel.registerToken(ContentActivity@ this)
                }
                is EventShowBulletin -> {
                    showBulletin()
                }
            }
        }, {
            it.printStackTrace()
        })


    }

    private fun initClick() {
    }

    private fun initBottomBar() {
        listFragment.add(ARouterManager.getShopFragment(this))
        bottomBar.addItem(BottomBarTab(this, R.mipmap.icon_01s, R.mipmap.icon_01, "书城"))
        listFragment.add(ARouterManager.getMyComicFragment(this, 0))
        bottomBar.addItem(BottomBarTab(this, R.mipmap.icon_02s, R.mipmap.icon_02, "阅读记录"))
        listFragment.add(ARouterManager.getVideoFragment(this))
        bottomBar.addItem(BottomBarTab(this, R.mipmap.icon_03s, R.mipmap.icon_03, "视频"))
        listFragment.add(ARouterManager.getMineFragment(this))
        bottomBar.addItem(BottomBarTab(this, R.mipmap.icon_04s, R.mipmap.icon_04, "我的"))
        listFragment.add(ARouterManager.getLongVideoFragment(this))
        bottomBar.addItem(BottomBarTab(this, R.mipmap.icon_05s, R.mipmap.icon_05, "客服"))

        bottomBar.setOnTabSelectedListener(tabSelectedListener)

        mCurrentIndex = 0
    }

    private fun initFragment() {
    }

    private fun showPage() {
        Log.d("MainActivity", "BottomBar showPage:" + showPage)
        bottomBar.setCurrentItem(showPage)
        viewPager.setCurrentItem(showPage, false)

//        (listFragment[0] as MineComicFragment).showTab(showTab)
    }

    fun hideTab() {
        bottomBar.visibility = View.GONE
    }

    fun showTab() {
        bottomBar.visibility = View.VISIBLE
    }


    override fun onResume() {
        super.onResume()

    }

    override fun onConfigurationChanged(config: Configuration) {
        super.onConfigurationChanged(config)
        when (config.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            }
        }
    }

    private var exitTime = 0L
    override fun onBackPressed() {
        if (VideoViewManager.instance().onBackPress("list")) return
        if (this.isForground) {
            if (bottomBar.currentItemPosition != 0) {
                bottomBar.setCurrentItem(0)
            } else {
                if (!BackHandlerHelper.handleBackPress(this)) {
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        toast(getString(R.string.tap_again_to_exit))
                        exitTime = System.currentTimeMillis()
                    } else {
                        super.onBackPressed()
                        SophixManager.getInstance().killProcessSafely()
                    }
                }
            }
        }
    }

    private fun versionCompare(sVersion: String?): Boolean {
        try {
            val cVersion = BuildConfig.VERSION_NAME
            if (cVersion == sVersion) return false;
            val sVersionNumber = sVersion!!.replace(".", "")
                    .substring(0, 3)
                    .toInt()
            val cVersionNumber = cVersion.replace(".", "")
                    .substring(0, 3)
                    .toInt()
            Log.d("versionCompare", "sVersionNumber:${sVersionNumber},cVersionNumber:${cVersionNumber}")
            return sVersionNumber > cVersionNumber
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun WxAdCheck() {

        val installTime = SharedPreUtils.getInstance().getLong("installTime")
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val today = sdf.format(Date())
        val installDay = sdf.format(Date(installTime))
        XLog.st(1).e("installDate = $installDay;today = $today")
        if (installDay != today) {
            var openRequest2 = SharedPreUtils.getInstance().getString("openRequest2")
//        Log.e("MainActivity", "openRequest:" + openRequest)
            if (openRequest2.equals("") || openRequest2.isEmpty()) {
                VM.getWxAd(this)
                SharedPreUtils.getInstance().putString("openRequest2", today.toString());
            } else {
//            val sdf = SimpleDateFormat("yyyy-MM-dd")
                val tomorrow = today.toString()
                var compare = tomorrow.compareTo(openRequest2)
//            Log.e("MainActivity", "openRequest compare:" + compare)
                if (compare > 0) {
                    VM.getWxAd(this)
                    SharedPreUtils.getInstance().putString("openRequest2", today.toString());

                }

            }
        } else {
            var openRequest2 = SharedPreUtils.getInstance().getString("openRequest2")
//        Log.e("MainActivity", "openRequest:" + openRequest)
            if (openRequest2.equals("") || openRequest2.isEmpty()) {
                VM.getWxAd(this)
                SharedPreUtils.getInstance().putString("openRequest2", today.toString());
            }
        }

    }

    private fun checkWelfare() {
//        val installTime = SharedPreUtils.getInstance().getLong("installTime")
//        val sdf = SimpleDateFormat("yyyy-MM-dd")
//        val installDay = sdf.format(Date(installTime))
//        val today = sdf.format(Date())
//        XLog.st(1).e("installDate = $installDay;today = $today")
//        if (installDay != today) {


        val show: Boolean = SharedPreUtils.getInstance().getBoolean("IsWXAttention", true)
        if (show) {
            layoutNotificaiton.show()
        }

//        }

        ivCancel.click {
            hideNotification()
        }
        val packageName = "com.tencent.mm"
        ivForward.click {
            copyText(ivForward)
            launchApp(packageName)

        }

    }

    fun copyText(view: View) {
        var myClipboard: ClipboardManager? = null
        var myClip: ClipData? = null
        myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?;
        myClip = ClipData.newPlainText("text", tvWxnumber.text);
        myClipboard?.setPrimaryClip(myClip);

        Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
    }

    private fun launchApp(packageName: String) {
        // Get an instance of PackageManager
        val pm = applicationContext.packageManager

        // Initialize a new Intent
        val intent: Intent? = pm.getLaunchIntentForPackage(packageName)

        // Add category to intent
        intent?.addCategory(Intent.CATEGORY_LAUNCHER)
        intent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        // If intent is not null then launch the app
        if (intent != null) {
            applicationContext.startActivity(intent)
            finishAffinity()
        } else {
//            toast("Intent null.")
        }
    }

    /**
     * 检查版本更新
     */
    private fun getVersionUpdate() {
        val REQUEST_EXTERNAL_STORAGE = 1
        val PERMISSIONS_STORAGE =
                arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
        val permission =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)
        } else {
            val cappid = BuildConfig.APPLICATION_ID
            val cVersion = BuildConfig.VERSION_NAME
            VM.versionCheck(this, cappid, cVersion)
            appDrainage()
        }

    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            val cappid = BuildConfig.APPLICATION_ID
            val cVersion = BuildConfig.VERSION_NAME
            VM.versionCheck(this, cappid, cVersion)
            appDrainage()
        }
    }

    private fun adZoneCheck() {
        val userInfo = CommonDataProvider.instance.getUserInfo()
        userInfo!!.let {
            if (userInfo.tempUid != null) {
                //临时用户tempuid不为null
                VM.getAdZone(this, 1)
            } else {
                //注册用户tempuid为null，uid不为空
                if (userInfo.vip != 1) {
                    VM.getAdZone(this, 2)
                } else {
                    VM.getAdZone(this, 3)
                }
            }
        }
    }

    /**
     * 是否弹出apk下载弹窗
     */
    private fun appDrainage() {
        val userInfo: UserInfoBean? = CommonDataProvider.instance.getUserInfo()
        userInfo?.let {
            if (userInfo.tempUid != null) {
                //临时用户tempuid不为null
                VM.appDrainage(MainActivity@ this, userInfo.id!!, 1)
            } else {
                //注册用户tempuid为null，uid不为空
                VM.appDrainage(MainActivity@ this, userInfo.uid!!, 0)
            }
        }
    }
}