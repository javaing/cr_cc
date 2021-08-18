package com.aliee.quei.mo.ui.search.fragment

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import android.graphics.Rect
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.config.AdEnum
import com.aliee.quei.mo.data.bean.Option
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.data.bean.SearchHistoryBean
import com.aliee.quei.mo.database.DatabaseProvider
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.search.adapter.SearchAdapter
import com.aliee.quei.mo.ui.search.adapter.SearchRecommendTitleAdapter
import com.aliee.quei.mo.ui.search.vm.SearchRecommendVModel
import com.aliee.quei.mo.utils.ScreenUtils
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.gone
import com.aliee.quei.mo.utils.extention.loadHtmlImg
import com.aliee.quei.mo.utils.extention.show
import com.google.gson.Gson
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import com.umeng.analytics.MobclickAgent
import com.zhy.view.flowlayout.TagFlowLayout
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.layout_common_list.recyclerView
import org.jetbrains.anko.runOnUiThread

@Route(path = Path.PATH_SEARCH_FRAGMENT)
class SearchFragment : BaseFragment() {
    private val VM = SearchRecommendVModel()
    private val adapter = SearchAdapter()
    lateinit var realm: Realm
    override fun getLayoutId(): Int {
        return R.layout.fragment_search
    }

    override fun initView() {
        realm = DatabaseProvider.getRealm()
        initRecyclerView()
        initVM()
        initFlowlayout()
    }

    private fun initFlowlayout() {
    }
    fun guessLikeAd(list : MutableList<RecommendBookBean>?) {
        val adBean = AdConfig.getAd(AdEnum.COMIC_GUESS_LIKE_RANK.zid)
        if (adBean==null){
//            if (list!!.size > 6) {
//                adapter.setRecommend(list.shuffled().subList(0, 6))
//            } else {
//                adapter.setRecommend(list)
//            }
            applyToUI(list)
            return
        }
        adBean.also { adBean ->
            AdConfig.getAdInfo(adBean, { adInfo->
                val option = Gson().fromJson<Option>(adInfo.optionstr, Option::class.java)
                adInfo.title = option.title
                adInfo.desc = option.desc
                Log.d("tag","猜你喜欢：interval:${adBean.interval},物料：$adInfo")
                val recommendBookBean = RecommendBookBean("", adInfo.imgurl, -321, adInfo.desc, 1, "", adInfo.title, "")
                recommendBookBean.adCallbackUrl = adInfo.callbackurl
                recommendBookBean.adClickUrl = adInfo.clickurl
                list?.add(0,recommendBookBean)
                applyToUI(list)
            }, {
                applyToUI(list)
            })
        }
    }

    private fun applyToUI(list: MutableList<RecommendBookBean>?) {
        context?.runOnUiThread {
            if (list!!.size > 6) {
                adapter.setRecommend(list.shuffled().subList(0, 6))
            } else {
                adapter.setRecommend(list)
            }
        }
    }

    private fun initVM() {
        VM.recommendLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Start -> showLoading()
                Status.Complete -> disLoading()
                Status.Success -> {
                    val list = it.data ?: return@Observer
                    showRecommend(list)
                   /* if (list.size > 6) {
                        adapter.setRecommend(list.shuffled().subList(0, 6))
                    } else {
                        adapter.setRecommend(list)
                    }*/
                    guessLikeAd(list)
                }
            }
        })
    }

    private fun showRecommend(list: List<RecommendBookBean>) {
        flowlayout.adapter = SearchRecommendTitleAdapter(list)
        flowlayout.setOnTagClickListener { view, position, parent ->
            parent as TagFlowLayout
            val bean = parent.adapter.getItem(position) as RecommendBookBean
            ARouterManager.goComicDetailActivity(view.context, bean.id)
            true
        }
    }

    private fun initRecyclerView() {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
                val viewType = adapter.getItemViewType(itemPosition)
                if (viewType == SearchAdapter.VIEW_TYPE_RECOMMEND_TITLE) {
                    outRect.top = ScreenUtils.dpToPx(10)
                }
            }
        })
        val hashMap: HashMap<String, String?> = HashMap<String, String?>() //define empty hashmap
        adapter.recommendClick = {
            if (it.bookcover!!.contains("http")){
                AdConfig.adClick(activity!!,it.adClickUrl!!)
            }else {
                hashMap.clear()
                hashMap.put("BookCollect", it.title)
                MobclickAgent.onEvent(activity!!, "1", hashMap)
                ARouterManager.goComicDetailActivity(activity!!, it.id)
            }
        }
        adapter.recordClick = {
            ARouterManager.goSearch(activity!!, it)
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
        adapter.shiftClick = {
            VM.shiftRecommend()
        }
    }

    override fun initData() {
        VM.loadRecommend()
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
        val adBean = AdConfig.getAd(AdEnum.COMIC_SEARCH.zid)
        adBean?.also { adBean ->
            AdConfig.getAdInfo(adBean, { adInfo ->
                context?.runOnUiThread {
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

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun getPageName(): String? = "搜索页"

}