package com.aliee.quei.mo.ui.main.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.component.EventLoginSuccess
import com.aliee.quei.mo.component.EventLogout
import com.aliee.quei.mo.component.EventShelfChanged
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.config.AdEnum
import com.aliee.quei.mo.data.bean.Option
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.data.exception.RequestException
import com.aliee.quei.mo.data.local.ReadRecordManager
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.common.ShopItemDecoration
import com.aliee.quei.mo.ui.launch.vm.LaunchVModel
import com.aliee.quei.mo.ui.main.adapter.ShelfAdapter2
import com.aliee.quei.mo.ui.main.vm.ShelfVModel
import com.aliee.quei.mo.utils.rxjava.RxBus
import com.aliee.quei.mo.widget.ConfirmDialog
import com.google.gson.Gson
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.fragment_shelf.*
import org.jetbrains.anko.runOnUiThread

/**
 * Created by Administrator on 2018/4/26 0026.
 */
@Route(path = Path.PATH_MAIN_SECOND_FRAGMENT)
class ShelfFragment : BaseFragment() {
    override fun getPageName() = "影城页"

    var VM = ShelfVModel()
    val launchVModel = LaunchVModel()
    var adapter = ShelfAdapter2()
    var bookId = 0
    var isRefresh: Boolean = true

    companion object {
        fun newInstance(): ShelfFragment {
            val arg = Bundle()
            val fragment = ShelfFragment()
            fragment.arguments = arg
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_shelf
    }

    override fun initView() {
        initRefresh()
        initVM()
        initEvent()
        initRecyclerView()
        initClick()
    }

    private fun initClick() {
    }

    private fun initRecyclerView() {
        val layoutManager = GridLayoutManager(activity, 6)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = adapter.getItemViewType(position)
                when (viewType) {
                    ShopItemDecoration.VIEW_TYPE_SHELF -> {
                        return 2
                    }
                    ShelfAdapter2.VIEW_TYPE_GUESS_TITLE -> {
                        return 6
                    }
                    ShelfAdapter2.VIEW_TYPE_COMIC_LINEAR -> {
                        return 6
                    }
                    ShopItemDecoration.VIEW_TYPE_COMIC_GRID_3 -> {
                        return 2
                    }
                    ShopItemDecoration.VIEW_TYPE_COMIC_GRID_2 -> {
                        return 3
                    }
                    ShelfAdapter2.VIEW_TYPE_SHELF_HEADER -> {
                        return 6
                    }
                    else -> return 2
                }
            }
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        val hashMap: HashMap<String, String?> = HashMap<String, String?>() //define empty hashmap
        adapter.recommendClick = {
            if (it.bookcover!!.contains("http")) {
                AdConfig.adClick(activity!!, it.adClickUrl!!)
            } else {
                hashMap.clear()
                hashMap.put("BookCollect", it.title)
                MobclickAgent.onEvent(activity!!, "1", hashMap)
                ARouterManager.goComicDetailActivity(activity!!, it.id)
            }
        }

        recyclerView.addItemDecoration(ShopItemDecoration())

        adapter.itemClick = { bean ->
            val record = ReadRecordManager.getReadRecord(bookId = bean.bookid)
            if (record != null) {
                ARouterManager.goReadActivity(activity!!, record.bookId, record.chapterId, record.page)
            } else {
                ARouterManager.goReadActivity(activity!!, bean.bookid, 0, 0)
            }
        }

        adapter.removeClick = { bean ->
            val dialog = ConfirmDialog.newInstance(getString(R.string.dialog_notice), getString(R.string.confirm_remove_book_from_shelf, bean.bookInfo?.title))
            dialog.cancelClick = {}
            dialog.confirmClick = {
                bookId = bean.bookid
                VM.delFromShelf(this, bean.bookid)
            }
            dialog.show(childFragmentManager, dialog.javaClass.name)
        }
    }

    @SuppressLint("CheckResult")
    private fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY).subscribe({
            when (it) {
                is EventLoginSuccess -> initData()
                is EventLogout -> initData()
                is EventShelfChanged -> {

                    VM.loadList(this)
                    refreshLayout.setNoMoreData(false)
                }
            }
        }, {
            it.printStackTrace()
        })
    }

    private fun initRefresh() {
        refreshLayout.setOnLoadMoreListener {
            isRefresh = false
            VM.loadMore(this)
        }
        refreshLayout.setOnRefreshListener {
            isRefresh = true
            VM.loadList(this)
            it.setNoMoreData(false)
        }
    }

    fun guessLikeAd(list: MutableList<RecommendBookBean>?) {
        val adBean = AdConfig.getAd(AdEnum.COMIC_GUESS_LIKE_RANK.zid)
        if (adBean == null) {
            adapter.addRecommend(list)
            return
        }
        adBean?.also { adBean ->
            AdConfig.getAdInfo(adBean, { adInfo ->
                val option = Gson().fromJson<Option>(adInfo.optionstr, Option::class.java)
                adInfo.title = option.title
                adInfo.desc = option.desc
                context!!.runOnUiThread {
                    Log.d("tag", "猜你喜欢：interval:${adBean.interval},物料：${adInfo.toString()}")
                    val recommendBookBean = RecommendBookBean("", adInfo.imgurl, -321, adInfo.desc, 1, "", adInfo.title, "")
                    recommendBookBean.adCallbackUrl = adInfo.callbackurl
                    recommendBookBean.adClickUrl = adInfo.clickurl
                    list?.add(0, recommendBookBean)
                    adapter.addRecommend(list)
                }
            }, {
                adapter.addRecommend(list)
            })
        }
    }

    private fun initVM() {
        VM.delFromShelfLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Start -> showLoading()
                Status.Success -> VM.loadList(this)
                Status.Complete -> {
                    disLoading()
                }
                Status.TokenError -> {
                    launchVModel.registerToken(this)
                }
            }
        })

        launchVModel.registerTokenLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    VM.delFromShelf(this, bookId)
                }
            }
        })

        VM.recommendLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    if (!isRefresh) {
                        //上拉加载时不添加广告
                        adapter.addRecommend(it.data?.list)
                    } else {
                        //首次进入时添加广告
                        guessLikeAd(it.data?.list)
                    }
                }
                Status.Complete -> {
                    refreshLayout.finishRefresh()
                    refreshLayout.finishLoadMore()
                }
            }
        })
        VM.getShelfListLiveData.observeForever {
            when (it?.status) {
                Status.Before -> statuslayout.showLoading()
                Status.Success -> {
                    statuslayout.showContent()
                    adapter.setShelfItem(it.data)
                    VM.loadRecommend(this)
                }
                Status.NoNetwork -> {
                    isFirst = true
                    statuslayout.showNoNetwork {
                        statuslayout.showLoading()
                        VM.loadList(this)
                    }
                }
                Status.Complete -> {
                    refreshLayout.finishRefresh()
                    refreshLayout.finishLoadMore()
                }
                Status.Error -> {
                    isFirst = true
                    var msg = ""
                    val e = it.e
                    if (it.e is RequestException) msg = (it.e as RequestException).msg
                    if (e is RequestException) {
//                        if (e.code == RequestException.ERROR_CODE_LOGIN) {
                        statuslayout.setErrorText(getString(R.string.please_login))
                                .showError {
                                    ARouterManager.goLoginActivity(it.context)
                                }
                        return@observeForever
//                        }
                    }
                    statuslayout.setErrorText(msg)
                            .setErrorImage(R.mipmap.img_error)
                            .setErrorRetryText(getString(R.string.reload))
                            .showError {
                                statuslayout.showLoading()
                                VM.loadList(this)
                            }
                }
                Status.TokenError -> {
                    launchVModel.registerToken(this)
                }
            }
        }

        launchVModel.registerTokenLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    if (isRefresh) {
                        VM.loadList(this)
                    } else {
                        VM.loadMore(this)
                    }
                }
            }
        })
    }

    override fun initData() {
        VM.loadList(this)
        statuslayout.showLoading()
    }

    override fun onDestroy() {
        super.onDestroy()
        VM.getShelfListLiveData.removeObservers(this)
    }

    override fun scrollToTop() {
        try {
            recyclerView.smoothScrollToPosition(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}