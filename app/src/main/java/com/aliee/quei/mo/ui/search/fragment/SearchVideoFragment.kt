package com.aliee.quei.mo.ui.search.fragment

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import android.graphics.Rect
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.config.AdEnum
import com.aliee.quei.mo.data.bean.Option
import com.aliee.quei.mo.data.bean.SearchHistoryBean
import com.aliee.quei.mo.data.bean.Video
import com.aliee.quei.mo.data.bean.VideoBean
import com.aliee.quei.mo.database.DatabaseProvider
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.main.vm.MainVideoModel
import com.aliee.quei.mo.ui.search.adapter.SearchVideoAdapter
import com.aliee.quei.mo.ui.search.adapter.SearchVideoHotTagAdapter
import com.aliee.quei.mo.utils.ScreenUtils
import com.aliee.quei.mo.utils.TagCountManager
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.gone
import com.aliee.quei.mo.utils.extention.loadHtmlImg
import com.aliee.quei.mo.utils.extention.show
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import com.zhy.view.flowlayout.TagFlowLayout
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_search.*
import org.jetbrains.anko.runOnUiThread

@Route(path = Path.PATH_VIDEO_SEARCH_VIDEO_FRAGMENT)
class SearchVideoFragment : BaseFragment() {
    private val VM = MainVideoModel()
    private val adapter = SearchVideoAdapter()
    lateinit var realm: Realm

    private var tagsId: Int? = 6
    private var page: Int = 1
    override fun getLayoutId(): Int {
        return R.layout.fragment_search
    }

    override fun initView() {
        realm = DatabaseProvider.getRealm()
        initRecyclerView()
        initVM()
    }


    private fun initVM() {

        VM.searchHotTag.observe(this, Observer {
            when (it?.status) {
                Status.Start -> showLoading()
                Status.Complete -> disLoading()
                Status.Success -> {
                    val hotTags = it.data ?: return@Observer
                    showHot(hotTags)
                }
            }
        })
        VM.guessLikes.observe(this, Observer {
            when (it?.status) {
                Status.Start -> showLoading()
                Status.Complete -> disLoading()
                Status.Success -> {

                    guessLikeAd(it.data!!)
                }
            }
        })
    }

    /**
     * 热门搜索
     */
    private fun showHot(list: Array<String>) {
        flowlayout.adapter = SearchVideoHotTagAdapter(list)
        flowlayout.setOnTagClickListener { view, position, parent ->
            parent as TagFlowLayout
            val keywords = parent.adapter.getItem(position) as String
            ARouterManager.goVideoSearch(activity!!, keywords)
            true
        }
    }

    private fun initRecyclerView() {
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
                val viewType = adapter.getItemViewType(itemPosition)
                if (viewType == SearchVideoAdapter.VIEW_TYPE_RECOMMEND_TITLE) {
                    outRect?.top = ScreenUtils.dpToPx(10)
                } else if (viewType == SearchVideoAdapter.VIEW_TYPE_RECOMMEND) {
                    outRect?.set(10, 10, 10, 10)
                }
            }
        })
        val layoutManager = GridLayoutManager(activity, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    SearchVideoAdapter.VIEW_TYPE_RECORD_TITLE -> 2
                    SearchVideoAdapter.VIEW_TYPE_RECORD_ITEM -> 2
                    SearchVideoAdapter.VIEW_TYPE_RECOMMEND_TITLE -> 2
                    else -> 1
                }
            }
        }
        recyclerView.layoutManager = layoutManager


        adapter.shiftClick = {
            page += 1
            VM.guessLike(this, tagsId, page)
        }
        adapter.videoClick = {
            if (it.thumbImg!!.contains("http")){
                AdConfig.adClick(context!!,it.adClickUrl!!)
            }else{
                val videoInfoJson = Gson().toJson(it)
                ARouterManager.goVideoInfoActivity(activity!!, videoInfoJson)
            }
        }
        adapter.recordDelClick = {
            val keyword = it
            try {
                realm.executeTransaction {
                    it.where(SearchHistoryBean::class.java).equalTo("keyword", keyword).findAll().deleteAllFromRealm()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        adapter.recordClick = {
            ARouterManager.goVideoSearch(activity!!, it)
        }
    }

    override fun initData() {

        tagsId = TagCountManager.getTagCount()?.tagId
        //获取热门搜索
        VM.getHotSearch(this)
        VM.guessLike(this, tagsId, page)
        //获取搜索记录
        realm.where(SearchHistoryBean::class.java)
                .limit(5)
                .sort("time", Sort.DESCENDING)
                .findAll().asFlowable()
                .bindUntilEvent(this, Lifecycle.Event.ON_DESTROY)
                .subscribe({
                    adapter.setSearchHistory(realm.copyFromRealm(it))
                }, {
                    it.printStackTrace()
                })

        searchAd()
    }

    private fun searchAd() {
        val adBean = AdConfig.getAd(AdEnum.VIDEO_SEARCH.zid)
        adBean?.also { adBean ->
            AdConfig.getAdInfo(adBean, { adInfo ->
                context!!.runOnUiThread {
                    if (AdConfig.isClosed(adBean.close)) {
                        search_ad_iv_close.show()
                        search_ad_iv_close.click {
                            search_ad.gone()
                        }
                    } else {
                        search_ad_iv_close.show()
                    }
                    search_ad.show()
                    iv_search_ad.loadHtmlImg(adInfo.imgurl)
                    AdConfig.adPreview(adInfo.callbackurl)
                    iv_search_ad.click {
                        AdConfig.adClick(iv_search_ad.context, adInfo.clickurl)
                    }
                }
            }, {

            })
        }
    }
    fun guessLikeAd(list : MutableList<Video>) {
        val adBean = AdConfig.getAd(AdEnum.VIDEO_GUESS_LIKE_RANK.zid)
        if (adBean==null){
            adapter.setData(list)
            return
        }
        adBean?.also { adBean ->
            AdConfig.getAdInfo(adBean, { adInfo->
                val option = Gson().fromJson<Option>(adInfo.optionstr, Option::class.java)
                adInfo.title = option.title
                adInfo.desc = option.desc
                activity!!.runOnUiThread {
                    Log.d("tag","视频详情猜你喜欢：interval:${adBean.interval},物料：${adInfo.toString()}")
                    val videoBean = Video(adInfo.title!!,"",-321,-1,adInfo.desc!!,"",-1, mutableListOf(),"",adInfo.imgurl,
                            "",1,"")
                    videoBean.adCallbackUrl = adInfo.callbackurl
                    videoBean.adClickUrl = adInfo.clickurl
                    list?.add(0,videoBean)
                    adapter.setData(list)

                }
            }, {
                adapter.setData(list)
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun getPageName(): String? = "视频搜索页"
}