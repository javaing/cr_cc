package com.aliee.quei.mo.ui.search.fragment

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.main.vm.MainVideoModel
import com.aliee.quei.mo.ui.video.RecycleGridDivider
import com.aliee.quei.mo.ui.video.adapter.RecommendAdapter
import com.aliee.quei.mo.ui.video.adapter.VideoGuessLikeAdapter
import com.aliee.quei.mo.ui.video.adapter.VideoListAdapter
import com.aliee.quei.mo.ui.video.fragment.VideoRecommendFragment
import com.aliee.quei.mo.widget.CommonDivider
import kotlinx.android.synthetic.main.fragment_search_result.statuslayout
import kotlinx.android.synthetic.main.layout_common_list.recyclerView
import kotlinx.android.synthetic.main.layout_common_list.refreshLayout

@Route(path = Path.PATH_VIDEO_SEARCH_VIDEO_RESULT_FRAGMENT)
class SearchVideoResultFragment : BaseFragment() {
    var adapter = RecommendAdapter()
    var VM = MainVideoModel()

    companion object {
        fun newInstance(): SearchVideoResultFragment {
            return SearchVideoResultFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_search_result
    }

    override fun initView() {
        initVM()
        initRecycler()
        initRefresh()
    }

    private fun initRefresh() {
        refreshLayout.setOnRefreshListener {
            VM.getSearchVideo(key)
        }
        refreshLayout.setOnLoadMoreListener {
            VM.getSearchVideoLoadMore(key)
        }
    }

    private fun initRecycler() {
        recyclerView.layoutManager = GridLayoutManager(activity, 2)
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.addItemDecoration(RecycleGridDivider())
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        adapter.onItemClick = {
            val videoInfoJson = Gson().toJson(it)
            ARouterManager.goVideoInfoActivity(activity!!, videoInfoJson)
        }
    }

    private fun initVM() {
        VM.searchVideoLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Start -> {
                    refreshLayout.setNoMoreData(false)
                }
                Status.Success -> {
                    statuslayout.showContent()
                    adapter.setData(it.data!!)
                }
                Status.Error -> {
                    statuslayout.showError {
                        doSearch(key)
                    }
                }
                Status.Complete -> {
                    refreshLayout.finishLoadMore()
                    refreshLayout.finishRefresh()
                }

                Status.Empty -> {
                    statuslayout.showEmpty()
                }
                Status.NoMore -> {
                    refreshLayout.finishLoadMoreWithNoMoreData()
                }
            }
        })
        VM.searchVideoLoadMoreLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    statuslayout.showContent()
                    adapter.loadMore(it.data!!)
                }
                Status.Error -> {
                    statuslayout.showError {
                        doSearch(key)
                    }
                }
                Status.Complete -> {
                    refreshLayout.finishLoadMore()
                    refreshLayout.finishRefresh()
                }

                Status.Empty -> {
                    statuslayout.showEmpty()
                }
                Status.NoMore -> {
                    refreshLayout.finishLoadMoreWithNoMoreData()
                }
            }
        })
    }


    private var key = ""
    fun doSearch(key: String) {
        this.key = key
        VM.getSearchVideo(key)
    }

    override fun initData() {
    }

    override fun getPageName(): String? = "搜索结果"
}