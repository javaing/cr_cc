package com.aliee.quei.mo.ui.search.fragment

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.search.adapter.SearchResultAdapter
import com.aliee.quei.mo.ui.search.vm.SearchVModel
import com.aliee.quei.mo.widget.CommonDivider
import kotlinx.android.synthetic.main.fragment_search_result.statuslayout
import kotlinx.android.synthetic.main.layout_common_list.recyclerView
import kotlinx.android.synthetic.main.layout_common_list.refreshLayout

@Route(path = Path.PATH_SEARCH_RESULT_FRAGMENT)
class SearchResultFragment : BaseFragment(){
    var adapter = SearchResultAdapter()
    var VM = SearchVModel()

    companion object {
        fun newInstance() : SearchResultFragment{
            val fragment = SearchResultFragment()
            return fragment
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
        refreshLayout.isEnableRefresh = false
        refreshLayout.setOnLoadMoreListener {
            VM.loadMore(this)
        }
    }

    private fun initRecycler() {
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(CommonDivider())
    }

    private fun initVM() {
        VM.searchListLiveData.observe(this, Observer {
            when(it?.status){
                Status.Success -> {
                    statuslayout.showContent()
                    adapter.setData(it.data)
                }
                Status.Error -> {
                    statuslayout.showError{
                        doSearch(key)
                    }
                }

                Status.Complete -> {
                    refreshLayout.finishLoadMore()
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
    fun doSearch(key : String){
        this.key = key
        VM.search(this,key)
    }

    override fun initData() {
    }

    override fun getPageName(): String? = "搜索结果"
}