package com.aliee.quei.mo.ui.comic.fragment

import android.support.v7.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.main.adapter.BulletinAdapter
import com.aliee.quei.mo.ui.comic.vm.RankVModel
import kotlinx.android.synthetic.main.layout_common_list.*

@Route(path = Path.PATH_BULLETIN_FRAGMENT)
class BulletinFragment : BaseFragment(){
    private val VM = RankVModel()

    private val adapter = BulletinAdapter()
    override fun getLayoutId(): Int {
        return R.layout.fragment_bulletin
    }

    override fun initView() {
        initVM()
        initRecyclerView()
        initRefresh()
    }

    private fun initRefresh() {
        refreshLayout.setOnRefreshListener {
            initData()
        }
        refreshLayout.isEnableLoadMore = false
    }

    private fun initRecyclerView() {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter.itemClick = {

        }
    }

    private fun initVM() {
//        VM.listLiveData.observe(this, Observer {
//            when (it?.status) {
//                Status.Start -> {
//                    refreshLayout.setNoMoreData(false)
//                }
//                Status.Success -> {
//                    adapter.setData(it.data)
//                    statuslayout.showContent()
//                    refreshLayout.finishLoadMore()
//                    refreshLayout.finishRefresh()
//                }
//                Status.Empty -> statuslayout.showEmpty()
//                Status.Error -> statuslayout.showError { initData() }
//                Status.NoNetwork -> statuslayout.showNoNetwork { initData() }
//                Status.NoMore -> {
//                    refreshLayout.finishLoadMoreWithNoMoreData()
//                }
//            }
//        })
    }

    override fun initData() {
//        VM.loadRank(this,rid)
    }

    override fun getPageName() = ""
}