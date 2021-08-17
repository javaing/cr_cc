package com.aliee.quei.mo.ui.comic.activity

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.elvishew.xlog.XLog
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.component.EventReadHistoryUpdated
import com.aliee.quei.mo.component.EventReadToHome
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.config.AdEnum
import com.aliee.quei.mo.data.bean.CatalogItemBean
import com.aliee.quei.mo.data.bean.Option
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.data.exception.RequestException
import com.aliee.quei.mo.data.local.ReadRecordBean
import com.aliee.quei.mo.data.local.ReadRecordManager
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.comic.adapter.ComicDetailAdapter
import com.aliee.quei.mo.ui.comic.vm.ComicDetailVModel
import com.aliee.quei.mo.ui.launch.vm.LaunchVModel
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.orElse
import com.aliee.quei.mo.utils.rxjava.RxBus
import com.google.gson.Gson
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_comic_detail.*
import kotlinx.android.synthetic.main.layout_title.*
import org.jetbrains.anko.runOnUiThread
import retrofit2.HttpException

@Route(path = Path.PATH_COMIC_DETAIL)
class ComicDetailActivity : BaseActivity() {
    @Autowired
    @JvmField
    var bookid = 0

    @Autowired
    @JvmField
    var rid: String = ""

    @Autowired
    @JvmField
    var rand: Boolean = false

    private val VM = ComicDetailVModel()
    private val launchVModel = LaunchVModel()
    private val adapter = ComicDetailAdapter()
    private  lateinit var  catalogItem : CatalogItemBean
    override fun getLayoutId() = R.layout.activity_comic_detail

    private var record: ReadRecordBean? = null

    private var sort = 0

    private var isFirst = true
    private var firstChapter: CatalogItemBean? = null
    private var tokenflag = 0

    override fun initData() {
//        Log.d("ComicDetailActivity", "RID:" + rid)
        if (rand && rid.isNotEmpty()) {
            Log.e("tag", "get Rand:$rid")
            VM.getComicDetailRand(this, bookid, rid)
        } else {
            Log.e("tag", "getComic :$bookid")
            VM.getComicDetail(this, bookid)
        }
        doDelay({
            VM.isInShelf(this, bookid)
        },1000)

        statuslayout.showLoading()
    }

    override fun initView() {
        initVM()
        initRecyclerView()
        initWidget()
        initEvent()
        initTitle()


    }

    private fun initTitle() {
        titleBack.click { onBackPressed() }
    }

    @SuppressLint("CheckResult")
    private fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe {
                when (it) {
                    is EventReadHistoryUpdated -> {
                        if (it.bookId == this.bookid) {
                            adapter.updateHistory()
                        }
                    }
                    is EventReadToHome ->{
                        finish()
                    }
                }
            }



    }

    private fun initWidget() {
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        adapter.chapterClick = { it ->
            ARouterManager.goReadActivity(this, it.bookid, it.id)
        }
        adapter.guessLikeClick = { it ->
            if (it.bookcover!!.contains("http")){
                AdConfig.adClick(this,it.adClickUrl!!)
            }else{
                ARouterManager.goComicDetailActivity(this, it.id, true)
            }

        }
        adapter.sortClick = {
            VM.getCatalog(this, bookid, it)
            sort = it
        }
        adapter.addShelfClick = {
            if (it != null) {
                VM.addToShelf(this, it.id)
            }
        }
    }

    private fun initVM() {
        VM.comicDetailLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    val bean = it.data ?: return@Observer
                    statuslayout.showContent()
                    bookid = bean.id
                    adapter.setComic(bean)
                    titleText.text = bean.title
                    VM.getCatalog(this, bean.id)
                    VM.getGuessLike(this, bean.id, bean.typename)
                }
                Status.Error -> {
//                    statuslayout.showError {
//                        initData()
//                    }
//                    val e = it.e
//                    if (e is HttpException) {
//                        MobclickAgent.onEvent(this, "error_detail")
//                    }
                    //toast("Bingo!")
                    Log.e("tag", "{\"code\":Bingo!")
                    initData()
                }
                Status.NoNetwork -> {
                    statuslayout.showNoNetwork {
                        initData()
                    }
                }

                Status.TokenError->{
                    tokenflag = 4
                    launchVModel.registerToken(this)
                }
            }
        })
        VM.catalogLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Start -> showLoading()
                Status.Complete -> disLoading()
                Status.Success -> {
                    XLog.st(1).e(it.data)
                    Log.d("ComicDetailActivity","ComicDetail "+it.total)
                    adapter.setCatalog(it.data, it.total, sort)
                    if (isFirst) {
                        firstChapter = it.data.getOrNull(0)
                        if (record == null) {
//                            startChapter.text = it.data.getOrNull(0)?.name?:""
                        }
                    }
                    isFirst = false
                    val record = ReadRecordManager.getReadRecord(bookid)
                    if (record == null) {
                        catalogItem = it.data.getOrNull(0)?:return@Observer
                        val firstChapter = catalogItem
                        VM.addHistory(this, bookid, firstChapter.id)
                    } else {
                        VM.addHistory(this, bookid, record.chapterId)
                    }
                }
                Status.NoNetwork -> {
                    statuslayout.showNoNetwork {
                        initData()
                        statuslayout.showLoading()
                    }
                }
                Status.Error -> {
                    statuslayout.showError {
                        initData()
                        statuslayout.showLoading()
                    }
                }
            }
        })

        VM.addHistoryLiveData.observe(this, Observer {
            when(it?.status){
                Status.TokenError->{
                    tokenflag = 1
                    launchVModel.registerToken(this)
                }
            }
        })

        VM.addShelfLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Start -> showLoading()
                Status.Complete -> disLoading()
                Status.Success -> {
//                    addShelf.isActivated = false
//                    addShelf.text = getString(R.string.collected)
                    adapter.setIsInShelf(true)
                }
                Status.Error -> {
                    val e = it.e
                    if (e is RequestException) {
                        if (e.code == 1008) {
//                            addShelf.isActivated = true
//                            addShelf.text = "+收藏"
                        }
                    }
                }
                Status.TokenError->{
                    tokenflag = 2
                    launchVModel.registerToken(this)
                }
            }
        })
        VM.guessLikeLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    Log.e("TAG", "initVM: ${it.data}" )
                    val list = it.data?.filterNot { data->
                        val historyIds = CommonDataProvider.instance.getHistoryBookIds()
                        historyIds.contains(data.id.toString())
                    }  as MutableList

                    guessLikeAd(list)

                }
            }
        })
        VM.sameCategoryLiveData.observe(this, Observer { bean ->
            when (bean?.status) {
                Status.Success -> {
                    val list = bean.data.map {
                        RecommendBookBean(
                            it.author,
                            it.thumb,
                            it.id,
                            it.description,
                            it.status,
                            it.thumb,
                            it.title,
                            it.typename
                        )
                    }

                    adapter.setSameCategory(list.filterNot {
                        val historyIds = CommonDataProvider.instance.getHistoryBookIds()
                        historyIds.contains(it.id.toString())
                    })
                }
            }
        })
        VM.isInShelfLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    if (it.data == true) {
                        adapter.setIsInShelf(true)
                    } else {
                        adapter.setIsInShelf(false)
                    }
                }
                Status.TokenError ->{
                    tokenflag = 3
                    launchVModel.registerToken(this)
                }
            }
        })
        launchVModel.registerTokenLiveData.observe(this, Observer {
            when(it?.status){
                Status.Success->{
                    if (tokenflag == 1){
                        val record = ReadRecordManager.getReadRecord(bookid)
                        if (record == null) {
                            val firstChapter = catalogItem
                            VM.addHistory(this, bookid, firstChapter.id)
                        } else {
                            VM.addHistory(this, bookid, record.chapterId)
                        }
                    }else if (tokenflag ==2 ){
                        VM.addToShelf(this,bookid)
                    }else if (tokenflag == 3){
                        VM.isInShelf(this,bookid)
                    }else if (tokenflag == 4){
                        VM.getComicDetailRand(this, bookid, rid)
                    }
                }
            }
        })
    }
    fun guessLikeAd(list : MutableList<RecommendBookBean>?) {
        val adBean = AdConfig.getAd(AdEnum.COMIC_GUESS_LIKE_RANK.zid)
        if (adBean==null){
            runOnUiThread { adapter.setGuessLike(list) }
            return
        }
        adBean.also { adBean ->
            AdConfig.getAdInfo(adBean, { adInfo ->
                val option = Gson().fromJson<Option>(adInfo.optionstr, Option::class.java)
                adInfo.title = option.title
                adInfo.desc = option.desc

                Log.d("tag", "猜你喜欢：interval:${adBean.interval},物料：$adInfo")
                val recommendBookBean = RecommendBookBean("", adInfo.imgurl, -321, adInfo.desc, 1, "", adInfo.title, "")
                recommendBookBean.adCallbackUrl = adInfo.callbackurl
                recommendBookBean.adClickUrl = adInfo.clickurl
                list?.shuffled()
                list?.add(0, recommendBookBean)
                runOnUiThread { adapter.setGuessLike(list) }

            }, {
                runOnUiThread { adapter.setGuessLike(list) }
            })
        }
    }
    override fun getPageName() = "漫画详情页"
}