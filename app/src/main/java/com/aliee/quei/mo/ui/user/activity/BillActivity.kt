package com.aliee.quei.mo.ui.user.activity

import android.arch.lifecycle.Observer
import android.support.v7.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.elvishew.xlog.XLog
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.launch.vm.LaunchVModel
import com.aliee.quei.mo.ui.user.adapter.BillAdapter
import com.aliee.quei.mo.ui.user.vm.BillVModel
import com.aliee.quei.mo.utils.extention.click
import kotlinx.android.synthetic.main.activity_bill.*
import kotlinx.android.synthetic.main.layout_common_list.*
import kotlinx.android.synthetic.main.layout_title.*

@Route(path = Path.PATH_USER_BILL)
class BillActivity : BaseActivity(){
    private val VM = BillVModel()
    private val launchVModel = LaunchVModel()
    override fun getLayoutId() = R.layout.activity_bill
    private val adapter = BillAdapter()
    private var isRefresh = true
    override fun initData() {
        VM.getList(this)
    }

    override fun initView() {
        initTitle()
        initVM()
        initRecyclerView()
        initRefresh()
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun initRefresh(){
        refreshLayout.setOnRefreshListener {
            isRefresh = true
            VM.getList(this)

        }
        refreshLayout.setOnLoadMoreListener {
            isRefresh = false
            VM.loadMore(this)
        }
    }

    private fun initVM() {
        VM.billListLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Before -> statuslayout.showLoading()
                Status.Start -> {
                    refreshLayout.setNoMoreData(false)
                }
                Status.Complete -> {
                    refreshLayout.finishLoadMore()
                    refreshLayout.finishRefresh()
                }
                Status.Success -> {
                    XLog.st(1).e(it.data)
                    statuslayout.showContent()
                    adapter.setData(it.data)
                }
                Status.Empty -> {
                    statuslayout.showEmpty()
                }
                Status.NoMore -> {
                    refreshLayout.setNoMoreData(true)
                }
                Status.TokenError->{
                    launchVModel.registerToken(this)
                }
            }
        })

        launchVModel.registerTokenLiveData.observe(this, Observer {
            when(it?.status){
                Status.Success->{
                    if (isRefresh){
                        VM.getList(this)
                    }else{
                        VM.loadMore(this)
                    }
                }
            }
        })


    }

    private fun initTitle() {
        titleText.text = getString(R.string.pay_history)
        titleBack.click { onBackPressed() }
    }

    override fun getPageName() = "消费记录"
}