package com.aliee.quei.mo.ui.comic.activity

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import android.content.Intent

import android.graphics.Color
import android.net.Uri
import android.os.Handler
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route


import com.aliee.quei.mo.R
import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.component.EventReadToHome
import com.aliee.quei.mo.component.EventRechargeSuccess
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.config.AdEnum
import com.aliee.quei.mo.data.bean.AdBean
import com.aliee.quei.mo.data.bean.AdInfo
import com.aliee.quei.mo.data.bean.ChapterContentBean
import com.aliee.quei.mo.data.bean.LocalRecordBean
import com.aliee.quei.mo.data.exception.RequestException
import com.aliee.quei.mo.data.local.ReadRecordBean
import com.aliee.quei.mo.data.local.ReadRecordManager
import com.aliee.quei.mo.net.Web
import com.aliee.quei.mo.net.WebListener
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.comic.adapter.ReadAdapter
import com.aliee.quei.mo.ui.comic.vm.ComicReadVModel
import com.aliee.quei.mo.utils.BrightnessUtils
import com.aliee.quei.mo.utils.JsonUtils
import com.aliee.quei.mo.utils.ScreenUtils
import com.aliee.quei.mo.utils.ToastUtil
import com.aliee.quei.mo.utils.extention.*
import com.aliee.quei.mo.utils.rxjava.RxBus
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.aliee.quei.mo.widget.comic.ScrollSpeedLinearLayoutManger
import com.elvishew.xlog.XLog
import com.koushikdutta.ion.Ion
import com.aliee.quei.mo.ui.launch.vm.LaunchVModel
import com.aliee.quei.mo.ui.main.vm.AdVModel
import com.tencent.mmkv.MMKV
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import com.umeng.analytics.MobclickAgent
import com.zhouyou.view.seekbar.SignSeekBar
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_comic_read.*
import kotlinx.android.synthetic.main.activity_comic_read.container
import kotlinx.android.synthetic.main.activity_comic_read.recyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_read_ad.*
import okhttp3.Response
import org.jetbrains.anko.sp
import org.json.JSONException
import org.json.JSONObject

import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

@Route(path = Path.PATH_COMIC_READ)
class ComicReadActivity : BaseActivity() {
    private val VM = ComicReadVModel()
    private val launchVModel = LaunchVModel()
    private val adVModel = AdVModel()

    private val kv = MMKV.mmkvWithID("readSetting")
    private var currentChapterBean: ChapterContentBean? = null
    private var isClick: Boolean = true

    var mBookChapter: String? = null

    @Autowired
    @JvmField
    var bookid: Int = 0

    @Autowired
    @JvmField
    var chapterId: Int = 0

    @Autowired
    @JvmField
    var mBookName: String? = null

    @Autowired
    @JvmField
    var chapterPosition: Int = 0

    @Autowired
    @JvmField
    var isHome: Boolean = false   //是否首页

    var tokenFlag = 0  //判断token出错的类型

    lateinit var adapter: ReadAdapter

    var autoScrollPix = 0

    var chapterList = mutableListOf<ChapterContentBean>()
    override fun getLayoutId() = R.layout.activity_comic_read

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData()
    }

    override fun initData() {
        readChapterCount = 0
        validReadTime = 5
        if (chapterId == 0) {
            val record = ReadRecordManager.getReadRecord(bookid)
            if (record != null) {
                VM.getContent(this, bookid, record.chapterId)
            } else {
                VM.getCatalog(this, bookid)
            }
        } else {
            if (isHome) {
                VM.getContent(this, bookid, chapterId)
            } else {
                VM.getNewContent(this, chapterId)
            }
        }
        VM.isInShelf(this, bookid)
        VM.getChapterEndRecommend()
        VM.getBalance()
        getAd()
    }

    private fun getAd() {
        adVModel.getAdList(1)
        adVModel.adList1LiveData.observe(this, Observer {
            when (it!!.status) {
                Status.Success -> {
                    val adList = it.data!!
                    adList.forEach { adBean ->
                        when (adBean.id) {
                            //头部广告
                            AdEnum.COMIC_READ_HEAD.zid -> {
                                headAd(adBean)
                            }
                            //中间广告
                            AdEnum.COMIC_READ_CENTER.zid -> {
                                centerAd(adBean)
                            }
                            //底部广告
                            AdEnum.COMIC_READ_BOTTOM.zid -> {
                                bottomAd(adBean)
                            }
                            //弹窗广告
                            AdEnum.COMIC_READ_DIALOG.zid -> {
                                dialogAd(adBean)
                            }
                        }
                    }
                }
                Status.Error -> {

                }
            }
        })
    }

    /**
     * 顶部广告
     */
    fun headAd(adBean: AdBean) {
        AdConfig.getAdInfo(adBean, { adInfo ->
            runOnUiThread {
                if (AdConfig.isClosed(adBean.close)) {
                    ad_iv_head_close.show()
                    ad_iv_head_close.click {
                        header_ad.gone()
                    }
                } else {
                    ad_iv_head_close.gone()
                }
                header_ad.visibility = View.VISIBLE
                //加载
                iv_head_ad.loadHtmlImg(adInfo.imgurl)
                //曝光统计
                AdConfig.adPreview(adInfo.callbackurl)
                iv_head_ad.click {
                    AdConfig.adClick(this, adInfo.clickurl)
                }
            }
        }, {

        })
    }

    /**
     * 中间广告
     */

    private var adInfo: AdInfo? = null
    fun centerAd(adBean: AdBean) {
        AdConfig.getAdInfo(adBean, {
            this.adInfo = it
            runOnUiThread {
                try {
                    adapter.insertAd(adBean.interval, adInfo!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, {

        })
    }


    /**
     * 底部广告
     */
    fun bottomAd(adBean: AdBean) {
        AdConfig.getAdInfo(adBean, { adInfo ->
            runOnUiThread {
                if (AdConfig.isClosed(adBean.close)) {
                    comic_ad_iv_bottom_close.show()
                    comic_ad_iv_bottom_close.click {
                        comic_bottom_ad.gone()
                    }
                } else {
                    comic_ad_iv_bottom_close.gone()
                }
                comic_bottom_ad.show()
                //加载
                comic_iv_bottom_ad.loadHtmlImg(adInfo.imgurl)
                //曝光统计
                AdConfig.adPreview(adInfo.callbackurl)
                comic_iv_bottom_ad.click {
                    AdConfig.adClick(this, adInfo.clickurl)
                }
            }
        }, {

        })
    }

    /**
     *  弹窗广告
     */
    fun dialogAd(adBean: AdBean) {
        AdConfig.getAdInfo(adBean, { adInfo ->
            runOnUiThread {
                rl_read_ad.show()
                comic_bottom_ad.show()
                //加载
                iv_comic_read_ad.loadHtmlImg(adInfo.imgurl)
                //曝光统计
                AdConfig.adPreview(adInfo.callbackurl)
                iv_comic_read_ad.click {
                    AdConfig.adClick(this, adInfo.clickurl)
                }

                iv_comic_read_close.click {
                    rl_read_ad.gone()
                }
            }
        }, {

        })
    }


    override fun initView() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        initTitle()
        initRecyclerView()
        initVM()
        initMenuAnim()
        initWidget()
        initEvent()

        iv_close.click {
            layout_ad_bottom.visibility = View.GONE
        }
    }


    private fun initTitle() {
        titleBack.click { onBackPressed() }
    }


    val layoutManager = ScrollSpeedLinearLayoutManger(this)
    private fun initRecyclerView() {
//        layoutManager.setSpeedSlow()
        recyclerView.layoutManager = layoutManager
        adapter = ReadAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.addOnScrollListener(ComicOnScrollListener())

        adapter.nextChapterClick = {
            menuNext.callOnClick()
            // statuslayout.showLoading()
        }

        autoScroll.click {
            if (autoScroll.text.equals("自动")) {
                recyclerView.post { // Call smooth scroll
//                    layoutManager.setSpeedSlow()
//                    try {
//                        recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
                    autoScrollTo(autoSpeed)
                }
                layoutLight.gone()
                layoutPlaySpeed.show()
            }
        }

        /* adapter.onRechargeClick = {
             ARouterManager.goRechargeActivity(
                 this,
                 "app://comic.hkzy.com/comic/read?id=${bookid}&chapterId=${chapterId}"
             )
         }*/
    }


    private var images: List<String>? = null

    private fun initVM() {
        VM.addShelfLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    showFav(true)
                }
                Status.Error -> {
                    val e = it.e
                    if (e is RequestException) {
                        if (e.code == 1008) showFav(false)
                    }
                }
                Status.TokenError -> {
                    tokenFlag = 4
                    launchVModel.registerToken(this)
                }
            }
        })
        VM.contentLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
//                    uploadReadTime()
                    var response = it.data
                    response?.run {
                        if (it.code == 500 || it.code == 1015) {
                            return@Observer
                        }
                    }
                    //  statuslayout.showContent()
                    //类型转换
                    Log.e("msg--Data:", response?.code.toString())
                    Log.e("msg--Data:", response?.data.toString())
                    var list = JsonUtils.performTransform<ChapterContentBean>(response?.data, ChapterContentBean::class.java)
                    chapterList = list
                    recyclerView.isNestedScrollingEnabled = response!!.code != 1009

                    if (response.code == 1009) {
                        pay_layout_reading.visibility = View.VISIBLE
                        comic_bottom_ad.gone()
                        layoutManager.setScorll(false)
                        isClick = false
                        /*  if (TitleBean.getTitles(list[0].bookid)!=null){
                              tv_bookname.text = TitleBean.getTitles(list[0].bookid)
                          }*/
                        layoutLight.visibility = View.GONE
                    } else {
                        comic_bottom_ad.show()
                        isClick = true
                        layoutManager.setScorll(true)
                        pay_layout_reading.visibility = View.GONE
                        VM.addHistory(list[0].bookid, list[0].id)
                        /*  if (TitleBean.getTitles(list[0].bookid)!=null){
                              tv_bookname.text = TitleBean.getTitles(list[0].bookid)
                          }*/
                        layoutLight.visibility = View.VISIBLE
                    }

                    tv_bookname.text = list[0].book!!.title
                    bookid = list[0].bookid
                    CommonDataProvider.instance.currentReading = list[0].book
                    readChapterCount++

                    XLog.e("msg===code:${list[0].code}")
                    /* readPaused = false
                      currentChapterBean = it.data?.get(0)
                      chapterId = it.data?.get(0)?.id ?: 0

                      currentChapterBean?.let { chapter ->
                          chapterName.text = chapter.name
                      }
                      adapter.clear()
                      this.images = it.data?.get(0)?.image as List<String>
  //                    it.data?.image?.let {
  //                        VM.downloadImage(this, this, it as List<String>)
  //                    }
                      adapter.setChapterPrice(it.data?.get(0)?.book_bean)
                      adapter.addImages(images)*/

                    readPaused = false
                    currentChapterBean = list[0]
                    chapterId = list[0]?.id ?: 0

                    currentChapterBean?.let { chapter ->
                        chapterName.text = chapter.name
                        mBookChapter = chapter.name
                    }
                    adapter.clear()
                    this.images = list[0]?.image as List<String>
                    //                    it.data?.image?.let {
                    //                        VM.downloadImage(this, this, it as List<String>)
                    //                    }
                    // adapter.setChapterPrice(list[0]?.book_bean)
                    Log.e("msg--imgUrl:", images.toString())
                    tv_price.text = "本章价格：${list[0].book_bean}金币"
                    adapter.isPay(response.code)
                    adapter.addReadmode(list[0].readmode)

                    //[mh/4612/20200703/b070f28ad34644578948aa50a3194eba.jpg, mh/4612/20200703/8dd1cd6a4c4e465587b4041cecb38e0d.jpg, mh/4612/20200703/26fed7e89a3e46a29204a4600fef8827.jpg, mh/4612/20200703/48062367eced4371be4632e67b5a0619.jpg, mh/4612/20200703/f7ab0906f8f44d6eb25d85986fdac916.jpg, mh/4612/20200703/8b8794f1cd814e2dad2ef8d926f803f8.jpg, mh/4612/20200703/2431d056949c42bbad35087cefec459f.jpg, mh/4612/20200703/6b90cac58546414d89c080c955bc76ab.jpg, mh/4612/20200703/a9b0a841056946e6be8b14953b5b499d.jpg, mh/4612/20200703/9d523b7cf76f4ec498a3a62b261d6ccd.jpg]
                    adapter.addImages(images)
                    disLoading()
                    if (currentChapterBean?.previd ?: 0 > 0) {
                        menuPrev.enable()
                    } else {
                        menuPrev.disable()
                    }
                    recyclerView.scrollToPosition(0)

                }

                Status.NoNetwork -> {
                    /*  statuslayout.showError {
                          initData()
                          statuslayout.showLoading()
                      }*/
                }
                Status.Error -> {
                    val e = it.e
                    if (e is RequestException) {
                        /*  ARouterManager.goRechargeActivity(
                              this,
                              "app://comic.hkzy.com/comic/read?id=${bookid}&chapterId=${chapterId}"
                          )
                          finish()*/
                        runOnUiThread {
                            ToastUtil.showToast(ComicReadActivity@ this, e.msg, 1000)
                        }

                    } else {
                        /*  statuslayout.showError {
                              initData()
                              statuslayout.showLoading()
                          }*/
                    }

                    if (e is HttpException) {
                        MobclickAgent.onEvent(this, "error_read")
                    }
                }
                Status.TokenError -> {
                    tokenFlag = 1
                    launchVModel.registerToken(this)
                }
            }
        })

        VM.chapterEndRecommendLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    adapter.setRecommendBottom(it.data?.filterNot {
                        var recordIds = CommonDataProvider.instance.getHistoryBookIds()
                        recordIds.add(bookid.toString())
                        recordIds.contains(it.id.toString())
                    })
                }
            }
        })

        VM.catalogLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    var c = it.data.getOrNull(0)
                    if (c != null) {
                        VM.getContent(this, bookid, c.id)
                    } else {
                        toast("没有章节")
                    }
                }
            }
        })

        VM.downloadImgLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    it.data ?: return@Observer
                    adapter.addImages(it.data)
                    disLoading()
                }
                Status.Complete -> {
                    if (chapterPosition > 0 && adapter.itemCount > chapterPosition) {
                        recyclerView.scrollToPosition(chapterPosition)
                        chapterPosition = 0
                    }
                    adapter.downloadFinish()
                }

                Status.Start -> adapter.downloadStart()
            }
        })
        VM.isInShelfLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    showFav(it.data == true)
                }
                Status.TokenError -> {
                    tokenFlag = 2
                    launchVModel.registerToken(this)
                }
            }
        })

        VM.balanceLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    // adapter.setUserBalance(it.data!!.bookBean)
                    tv_balance.text = "您的余额：${it.data!!.bookBean}书币"
                }
                Status.TokenError -> {
                    tokenFlag = 3
                    launchVModel.registerToken(this)
                }
            }
        })

        VM.addHistoryLiveData.observe(this, Observer {
            when (it?.status) {
                Status.TokenError -> {
                    tokenFlag = 5
                    launchVModel.registerToken(this)
                }
            }
        })
        launchVModel.registerTokenLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    when (tokenFlag) {
                        1 -> {
                            if (chapterId == 0) {
                                val record = ReadRecordManager.getReadRecord(bookid)
                                if (record != null) {
                                    VM.getContent(this, bookid, record.chapterId)
                                } else {
                                    VM.getCatalog(this, bookid)
                                }
                            } else {
                                if (isHome) {
                                    VM.getContent(this, bookid, chapterId)
                                } else {
                                    VM.getNewContent(this, chapterId)
                                }
                            }
                        }
                        2 -> {
                            VM.isInShelf(this, bookid)
                        }
                        3 -> {
                            VM.getBalance()
                        }
                        4 -> {
                            VM.addToShelf(bookid)
                        }
                        5 -> {
                            VM.addHistory(chapterList[0].bookid, chapterList[0].id)
                        }
                    }
                }
            }
        })

        VM.comicDetailLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    tv_bookname.text = it.data!!.title
                }
            }
        })
    }

    private lateinit var mTopInAnim: Animation
    private lateinit var mTopOutAnim: Animation
    private lateinit var mBottomOutAnim: Animation
    private lateinit var mBottomInAnim: Animation

    private fun initMenuAnim() {
        mTopInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_in)
        mTopOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_out)
        mTopInAnim.duration = 200
        mTopOutAnim.duration = 200

        mBottomInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in)
        mBottomOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out)
        mBottomInAnim.duration = 200
        mBottomInAnim.duration = 200
    }

    private val handler = Handler()

    private fun initWidget() {
        autoScrollPix = (ScreenUtils.getScreenSize(this).y * 2 / 3.0).toInt()

        container.centerClick = {
            toggleMenu(isClick)
        }

        if (kv.getBoolean("autoBrightness", true)) {
            setAutoBrightness()
        } else {
            handler.post(UpdateBrightnessRunnable(kv.getInt("brightness", 100)))
        }

        menuNext.click {
            val nextChapterId = currentChapterBean?.nextid ?: 0
            if (nextChapterId > 0) {
                Log.e("msg--Data:", "nextChapterId: +" + nextChapterId)
                chapterId = nextChapterId
                VM.getNewContent(this, chapterId)
                // layout_ad_bottom.show()
            } else {
                currentChapterBean?.let {
                    ARouterManager.goReadFinishActivity(this, it.book?.title ?: "", it.book?.status
                            ?: 1)
                    return@click
                }
                toast(getString(R.string.no_next_chapter))
            }
            //  layout_ad_bottom.gone()
            //  setMarginsInDp(recyclerView!!, 0, 0, 0, 0)

            VM.getBalance()
            tv_nums.visibility = View.INVISIBLE
        }
        menuPrev.click {
            val prevChapterId = currentChapterBean?.previd ?: 0
            if (prevChapterId > 0) {
                chapterId = prevChapterId
                VM.getNewContent(this, chapterId)
                //  layout_ad_bottom.show()
            } else {
                toast(getString(R.string.no_prev_chapter))
            }
            //  layout_ad_bottom.gone()
            //  setMarginsInDp(recyclerView!!, 0, 0, 0, 0)
            VM.getBalance()
            tv_nums.visibility = View.INVISIBLE
        }
        menuCatalog.click {
            // ARouterManager.goComicCatalogActivity(this, bookid)
            ARouterManager.goComicDetailActivity(this, bookid, true, "")

        }
        menuFav.click {
            VM.addToShelf(bookid)
        }

//        lightPlus.click {
//            val p = seekLight.progress + 2
//            if (p > seekLight.max) return@click
//            handler.postDelayed(UpdateBrightnessRunnable(p), 200)
//        }
//        lightMinus.click {
//            val p = seekLight.progress - 2
//            if (p < 0) return@click
//            handler.postDelayed(UpdateBrightnessRunnable(p), 200)
//        }
        autoLight.click {
            setAutoBrightness()
        }
        seekLight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (!fromUser) return
                val p = seekBar.progress
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(UpdateBrightnessRunnable(p), 200)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        tv_recharge.click {
            ARouterManager.goRechargeActivity(
                    this,
                    "app://comic.hkzy.com/comic/read?id=${bookid}&chapterId=${chapterId}", 0
            )
        }
        tv_vip_tip.click {
            ARouterManager.goRechargeActivity(
                    this,
                    "app://comic.hkzy.com/comic/read?id=${bookid}&chapterId=${chapterId}", 0
            )
        }

        btn_home.click {
            RxBus.getInstance().post(EventReadToHome())
            finish()
        }

        seekbar_speed.configBuilder
                .min(0f)
                .max(4f)
                .progress(0f)
                .sectionCount(4)
                .sectionTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                .sectionTextPosition(SignSeekBar.TextPosition.BELOW_SECTION_MARK)
                .build()

        seekbar_speed.setOnProgressChangedListener(object : SignSeekBar.OnProgressChangedListener {
            override fun onProgressChanged(signSeekBar: SignSeekBar, progress: Int, progressFloat: Float, fromUser: Boolean) {

            }

            override fun getProgressOnActionUp(signSeekBar: SignSeekBar, progress: Int, progressFloat: Float) {

            }

            override fun getProgressOnFinally(signSeekBar: SignSeekBar, progress: Int, progressFloat: Float, fromUser: Boolean) {
                autoSpeed = when(progress) {
                    0 -> -1f
                    1 -> 1f
                    2 -> 0.75f
                    3 -> 0.5f
                    else -> 0.3f
                }
                autoScrollTo(autoSpeed)
            }
        })

    }

    //keep上一章設定的速度
    var autoSpeed = 0f
    private fun autoScrollTo(speed:Float) {
        try {
            if (speed>0) {
                layoutManager.setSpeed(speed)
            } else {
                layoutManager.setSpeedSlow()
            }
            recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun showFav(fav: Boolean) {
        if (fav) {
            menuFav.text = getString(R.string.collected)
        } else {
            menuFav.text = getString(R.string.collect)
        }
    }

    private fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
                .subscribe {
                    if (it is EventRechargeSuccess) {
                        VM.getNewContent(this, chapterId)
                        VM.getBalance()
                    }
                }
    }

    private fun uploadReadTime() {
        if (validReadTime < 10) return
        var totalMinute = validReadTime / 60
        if (totalMinute == 0) totalMinute = 1
        VM.uploadReadTime(ReaderApplication.instance, bookid, readChapterCount, totalMinute)


    }

    override fun getPageName() = "漫画阅读页"

    private fun hideMenu() {
        if (layoutTopMenu.visibility != View.VISIBLE) return

        layoutTopMenu.startAnimation(mTopOutAnim)
        bottomMenu.startAnimation(mBottomOutAnim)
        layoutTopMenu.gone()
        bottomMenu.gone()
        layoutLight.gone()
        tv_nums.show()
        // btn_home.gone()

    }

    private fun showMenu() {
        if (layoutTopMenu.visibility == View.VISIBLE) return
//        if (isBottom)btnNextChapter.show()
        layoutTopMenu.startAnimation(mTopInAnim)
        bottomMenu.startAnimation(mBottomInAnim)
        layoutTopMenu.show()
        bottomMenu.show()
        layoutPlaySpeed.gone()
        tv_nums.gone()
    }

    private fun toggleMenu(boolean: Boolean) {
        if (!boolean) {
            return
        }
        if (isBottom) {
            return
        }
        if (layoutTopMenu.visibility == View.VISIBLE) {
            hideMenu()
        } else {
            showMenu()
            layoutLight.show()
        }
    }

    private fun setAutoBrightness() {
        kv.putBoolean("autoBrightness", true)

        val autoBrightnessValue = BrightnessUtils.getScreenBrightness(this)
        seekLight.progress = autoBrightnessValue
        BrightnessUtils.setBrightness(this, autoBrightnessValue)

        autoLight.setTextColor(Color.parseColor("#4FACF7"))
        autoLight.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.check_auto_light_s, 0, 0, 0)
    }


    override fun onPause() {
        super.onPause()
        currentChapterBean?.let {
            val position = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() // 当前阅读位置
            ReadRecordManager.saveReadRecord(
                    ReadRecordBean(
                            it.bookid,
                            it.id,
                            it.name,
                            position,
                            System.currentTimeMillis()
                    )
            )
        }
        currentChapterBean?.let {
            //   VM.addHistory(it.bookid, it.id)
            CommonDataProvider.instance.saveLastRead(
                    LocalRecordBean(
                            it.bookid,
                            it.book?.title ?: "",
                            it.id,
                            it.name ?: ""
                    )
            )
        }
        uploadReadTime()
    }


    var validReadTime = 5
    var readPaused = true

    @SuppressLint("CheckResult")
    override fun onResume() {
        super.onResume()
        Observable.interval(1, 3, TimeUnit.SECONDS)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(this, Lifecycle.Event.ON_DESTROY)
                .subscribe({
                    if (readPaused) {
                        XLog.st(1).e("阅读暂停 不加时长")
                        return@subscribe
                    }
                    validReadTime += 3
                    XLog.st(1).e("正常阅读 +3s 总时长 ${validReadTime}秒")
                }, {

                })
    }

    /**
     * 手动调屏幕亮度
     */
    private inner class UpdateBrightnessRunnable(private val p: Int) : Runnable {
        override fun run() {
            kv.putBoolean("autoBrightness", false)
            kv.putInt("brightness", p)
            BrightnessUtils.setBrightness(this@ComicReadActivity, p)
            seekLight.progress = p

            autoLight.setTextColor(Color.WHITE)
            autoLight.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.check_auto_light, 0, 0, 0)
        }
    }

    private var isBottom = false

    private inner class ComicOnScrollListener : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            isBottom = false
            if (!recyclerView.canScrollVertically(1)) {
                try {
                    if (adapter.itemCount > 1/* && !adapter.isLoading*/) {
//                        btnNextChapter.show()
                        showMenu() // 滑动到底部
                        isBottom = true
                        if (bottomMenu.visibility == View.VISIBLE) {
                            // setMarginsInDp(recyclerView!!, 0, 0, 0, 212)
                            // layout_ad_bottom.gone()

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (adapter.isLoading) {
//                    toast("努力加载中....")
                    // showLoading()
                }
                return
            }
            if (!recyclerView.canScrollVertically(-1)) {
                showMenu() // 滑动到顶部

                return
            }
            val lastScrollPosition = layoutManager.findLastVisibleItemPosition()
            if (tv_nums.visibility == View.INVISIBLE) {
                tv_nums.visibility = View.VISIBLE
            }
            tv_nums.text = "${mBookChapter}" + " " + "${lastScrollPosition + 1}/${adapter.itemCount}"
//            Log.e("ComicReadActivity:", lastScrollPosition.toString())

            hideMenu()
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                readPaused = false
                XLog.st(1).e("滚动，激活")
//                Log.e("ComicReadActivity:", "滚动，激活")
                mHandler.removeCallbacksAndMessages(null)
                if (layoutTopMenu.visibility == View.GONE) {
                    // setMarginsInDp(recyclerView!!, 0, 0, 0, 0)
                    // layout_ad_bottom.gone()
                }

            }
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                mHandler.postDelayed({
                    readPaused = true
                    XLog.st(1).e("10秒没动，暂停计时")
                }, 10 * 1000)

            }

        }
    }

    private var readChapterCount = 0
    private var mHandler = Handler()

    private fun getAD(url: String) {
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body?.string()
                    val json = JSONObject(resStr)
                    Log.d("tag", "json:$json")
//                    Log.d("ComicReadActivity", "返回資料：" + resStr.toString())
//                    Log.d("RechargeActivity", "返回值：" + rtnCode)
                    var clickUrl = json.getString("clickurl")
                    var imgUrl = json.getString("imgurl")
                    var callbackUrl = json.getString("callbackurl")
//                    Log.d("ComicReadActivity", "返回值：" + imgUrl)
                    runOnUiThread {

                        adCallback(callbackUrl)
                        setGif(imgUrl, clickUrl)


                    }
                } catch (e: JSONException) {

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("tag", "err:" + ErrorResponse.toString())
            }
        })
        web.Get_Data(url)
    }

    fun setGif(imgUrl: String, clickUrl: String) {
        Ion.with(ad_comicread_bottom).load(imgUrl)
        // layout_ad_bottom.show()
//        iv_ad.setImageResource(R.drawable.sample)
        ad_comicread_bottom.click {
            val uri = Uri.parse(clickUrl)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    fun adCallback(Url: String) {
        val url = Url.replace("&amp;", "&")
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {

            }

            override fun onErrorResponse(ErrorResponse: IOException?) {}
        })
        web.Get_Data(url)
    }

    fun setMarginsInDp(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is ViewGroup.MarginLayoutParams) {
            val screenDesity: Float = view.context.resources.displayMetrics.density
            val params: ViewGroup.MarginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(left * screenDesity.toInt(), top * screenDesity.toInt(), right * screenDesity.toInt(), bottom * screenDesity.toInt())
            view.requestLayout()
        }
    }
}


