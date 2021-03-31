package com.aliee.quei.mo.ui.main.fragment

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Route
import com.elvishew.xlog.XLog
import com.aliee.quei.mo.R
import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.component.*
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.config.AdEnum
import com.aliee.quei.mo.data.bean.Option
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.common.ShopItemDecoration
import com.aliee.quei.mo.ui.launch.vm.LaunchVModel
import com.aliee.quei.mo.ui.main.adapter.HistoryAdapter
import com.aliee.quei.mo.ui.main.vm.HistoryVModel
import com.aliee.quei.mo.utils.rxjava.RxBus
import com.aliee.quei.mo.widget.ConfirmDialog
import com.google.gson.Gson
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.layout_common_list.*
import org.jetbrains.anko.runOnUiThread
import retrofit2.HttpException

/**
 * Created by Administrator on 2018/4/26 0026.
 */
@Route(path = Path.PATH_HISTORY_FRAGMENT)
class HistoryFragment : BaseFragment() {
    override fun getPageName() = "阅读历史"

    var adapter = HistoryAdapter()
    var VM = HistoryVModel()
    val launchModel = LaunchVModel()

    companion object {
        fun newInstance(): HistoryFragment {
            val args = Bundle()
            val fragment = HistoryFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_history
    }

    override fun initView() {
        initRecycler()
        initVM()
        initRefresh()
        initEvent()
        initClick()
    }

    private fun initClick() {
    }

    @SuppressLint("CheckResult")
    private fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
                .subscribe({
                    XLog.st(1).e("event $it")
                    when (it) {
                        is EventLoginSuccess -> initData()
                        is EventLogout -> initData()
                        is EventReadHistoryUpdated -> initData()
                        is EventToReLogin -> initData()
                    }
                }, {})
    }

    private var isRefresh:Boolean = true
    private fun initRefresh() {
        refreshLayout.setOnRefreshListener {
            isRefresh = true
            VM.loadHistory(this)
        }
        refreshLayout.setOnLoadMoreListener {
            isRefresh = false
            VM.loadRecommend(this)
        }
        refreshLayout.setNoMoreData(false)
    }

    fun guessLikeAd(list : MutableList<RecommendBookBean>?) {
        val adBean = AdConfig.getAd(AdEnum.COMIC_GUESS_LIKE_RANK.zid)
        if (adBean==null){
            adapter.addRecommend(list)
            return
        }
        adBean?.also { adBean ->
            AdConfig.getAdInfo(adBean, {adInfo->
                val option = Gson().fromJson<Option>(adInfo.optionstr, Option::class.java)
                adInfo.title = option.title
                adInfo.desc = option.desc
                context!!.runOnUiThread {
                    Log.d("tag","猜你喜欢：interval:${adBean.interval},物料：${adInfo.toString()}")
                    val recommendBookBean = RecommendBookBean("", adInfo.imgurl, -321, adInfo.desc, 1, "", adInfo.title, "")
                    recommendBookBean.adCallbackUrl = adInfo.callbackurl
                    recommendBookBean.adClickUrl = adInfo.clickurl
                    list?.add(0,recommendBookBean)
                    adapter.addRecommend(list)
                }
            }, {
                adapter.addRecommend(list)
            })
        }
    }


    private fun initVM() {
        VM.recommendLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Start -> {
                    refreshLayout.setNoMoreData(false)
                }
                Status.Success -> {
                    if (!isRefresh){
                        //上拉加载时不添加广告
                        adapter.addRecommend(it.data?.list)
                    }else{
                        //首次进入时添加广告
                        guessLikeAd(it.data?.list)
                    }
                    statuslayout.showContent()
                }
                Status.Complete -> {
                    refreshLayout.finishLoadMore()
                    refreshLayout.finishRefresh()
                }
                Status.NoMore -> {
                    refreshLayout.setNoMoreData(true)
                    statuslayout.showContent()
                }
            }
        })
        VM.loadHistoryLiveData.observeForever {
            when (it?.status) {
                Status.Success -> {
                    VM.loadRecommend(this)
                    statuslayout.showContent()
                    val data = it.data
                    data ?: return@observeForever
                    adapter.setHistory(data)
                    var historyIds = ""
                    data.forEach {
                        historyIds += it.id.toString() + ","
                    }
                    CommonDataProvider.instance.saveHistory(historyIds)

                }
                Status.NoMore -> {
                    refreshLayout.finishLoadMoreWithNoMoreData()
                }
                Status.Empty -> {
                    statuslayout.setEmpty(R.layout.empty_shelf)
                            .setEmptyText(getString(R.string.msg_no_history))
                            .setEmptyRetryListener {
                                ARouterManager.goMainActivity(it.context, showPage = ARouterManager.TAB_SHOP)
                            }
                }
                Status.Complete -> {
                    refreshLayout.finishLoadMore()
                    refreshLayout.finishRefresh()
                }
                Status.Error -> {
                    isFirst = true
                    statuslayout.showError {
                        statuslayout.showLoading()
                        initData()
                    }
                    val e = it.e
                    if (e is HttpException) {
                        MobclickAgent.onEvent(requireContext(), "error_history")
                    }

                }
                Status.NoNetwork -> {
                    isFirst = true
                    statuslayout.showNoNetwork {
                        statuslayout.showLoading()
                        initData()
                    }
                }

                Status.TokenError -> {
                    launchModel.registerToken(this)
                }
            }
        }

        launchModel.registerTokenLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    VM.loadHistory(this)
                }
            }
        })
        VM.delHistoryLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Start -> showLoading()
                Status.Complete -> disLoading()
                Status.Success -> {
                    XLog.e("msg---删除成功")
                    adapter.removeItem(id)
                    if (adapter.itemCount == 0) {
                        statuslayout.showEmpty()
                    }
                }
                else -> {

                }
            }
        })
    }

    var id: Int? = 0
    private fun initRecycler() {
        val layoutManager = GridLayoutManager(activity, 6)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                when (adapter.getItemViewType(position)) {
                    ShopItemDecoration.VIEW_TYPE_HISTORY -> {
                        return 2
                    }
                    HistoryAdapter.VIEW_TYPE_GUESS_LIKE_HEADER -> {
                        return 6
                    }
                    HistoryAdapter.VIEW_TYPE_COMIC_LINEAR -> {
                        return 6
                    }
                    ShopItemDecoration.VIEW_TYPE_COMIC_GRID_3 -> {
                        return 2
                    }
                    ShopItemDecoration.VIEW_TYPE_COMIC_GRID_2 -> {
                        return 3
                    }
                    HistoryAdapter.VIEW_TYPE_HISTORY_HEADER -> {
                        return 6
                    }
                    else -> return 2
                }
            }
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(ShopItemDecoration())
        recyclerView.adapter = adapter
        adapter.removeClick = {
            val bean = it
            activity?.let {
                val confirm = ConfirmDialog.newInstance(getString(R.string.dialog_notice), getString(R.string.dialog_confirm_del))
                confirm.confirmClick = {
                    id = bean.id
                    VM.deleteHistory(this, bean.id)
                }
                confirm.show(childFragmentManager, confirm.javaClass.name)
            }
        }

        val hashMap: HashMap<String, String?> = HashMap<String, String?>() //define empty hashmap
        adapter.recommendClick = {
            if (it.bookcover!!.contains("http")){
                AdConfig.adClick(activity!!,it.adClickUrl!!)
            }else{
                hashMap.clear()
                hashMap.put("BookCollect", it.title)
                MobclickAgent.onEvent(activity!!, "1", hashMap)
                ARouterManager.goComicDetailActivity(activity!!, it.id, true)
            }

        }
    }

    override fun initData() {

        VM.loadHistory(this)
        statuslayout.showLoading()
    }

    override fun onDestroy() {
        super.onDestroy()
        VM.loadHistoryLiveData.removeObservers(this)
    }

    override fun scrollToTop() {
        try {
            recyclerView.smoothScrollToPosition(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}