package com.aliee.quei.mo.ui.reserve

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.welfare.adapter.CoinListAdapter
import com.aliee.quei.mo.ui.welfare.vm.CoinListVModel
import com.aliee.quei.mo.utils.extention.click
import kotlinx.android.synthetic.main.activity_bill.*
import kotlinx.android.synthetic.main.layout_common_list.*
import kotlinx.android.synthetic.main.layout_title.*

@Route(path = Path.PATH_WELFARE_COIN_LIST)
class Reserve3Activity : BaseActivity() {
    private val VM = CoinListVModel()
    private val adapter = CoinListAdapter()

    override fun getLayoutId(): Int {
        return R.layout.activity_welfare_coin_list
    }

    override fun initData() {
        VM.loadList(this)
    }

    override fun initView() {
        initTitle()
        initRecyclerView()
        initVM()
        initRefresh()
    }

    private fun initRefresh() {
        refreshLayout.setOnRefreshListener {
            VM.loadList(this)
        }
        refreshLayout.setOnLoadMoreListener {
            VM.loadMore(this)
        }
    }

    private fun initVM() {
        VM.listLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Error -> {
                    statuslayout.showError {
                        initData()
                    }
                }
                Status.Start -> {
                    refreshLayout.setNoMoreData(false)
                }
                Status.Success -> {
                    adapter.setData(it.data)
                    statuslayout.showContent()
                }
                Status.Complete -> {
                    refreshLayout.finishLoadMore()
                    refreshLayout.finishRefresh()
                }
                Status.NoMore -> {
                    refreshLayout.setNoMoreData(true)
                }
                Status.Empty -> {
                    statuslayout.showEmpty()
                }
            }
        })
    }

    private fun initRecyclerView() {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, OrientationHelper.VERTICAL))
    }

    private fun initTitle() {
        titleBack.click { finish() }
        titleText.text = "????????????"
    }

    override fun getPageName() = "????????????"
}