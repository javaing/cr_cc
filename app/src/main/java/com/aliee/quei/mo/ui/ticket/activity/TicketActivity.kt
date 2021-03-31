package com.aliee.quei.mo.ui.ticket.activity

import androidx.lifecycle.Observer
import android.graphics.Rect
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.data.bean.TicketBean
import com.aliee.quei.mo.data.exception.RequestException
import com.aliee.quei.mo.database.DatabaseProvider
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.ticket.adapter.TickerAdapter
import com.aliee.quei.mo.ui.ticket.vm.TicketVModel
import com.aliee.quei.mo.utils.ScreenUtils
import com.aliee.quei.mo.utils.extention.click
import kotlinx.android.synthetic.main.layout_common_list.*
import kotlinx.android.synthetic.main.layout_title.*

@Route(path = Path.PATH_TICKET_LIST)
class TicketActivity : BaseActivity() {
    private val VM = TicketVModel()
    private val realm = DatabaseProvider.getRealm()
    private val adapter = TickerAdapter(realm)

    override fun getLayoutId(): Int {
        return R.layout.activity_tickets
    }

    override fun initData() {
        VM.getTicketList(this)
    }

    override fun initView() {
        initTitle()
        initVM()
        initRecyclerView()
        initRefresh()
    }

    private fun initRefresh() {
        refreshLayout.isEnableRefresh = false
        refreshLayout.isEnableLoadMore = false
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
                super.getItemOffsets(outRect, itemPosition, parent)
                outRect?.top = ScreenUtils.dpToPx(20)
            }
        })
        adapter.itemClick = {bean: TicketBean,status ->
            if (status == 0) {
                VM.ticketReceive(this,bean)
            }
            if (status == 1) {
                ARouterManager.goComicDetailActivity(this,bean.cartoon_id,false)
            }
            if (status == 2) {
                VM.claimReward(this,bean.complete_reward,"书券奖励",bean.id)
            }
        }
    }

    private fun initVM() {
        VM.ticketLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Start -> showLoading()
                Status.Complete -> disLoading()
                Status.Success -> {
                    adapter.setData(it.data?.list)
                }
            }
        })
        VM.claimReadLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    toast("领取成功+${it.data}金币")
                    adapter.notifyDataSetChanged()
                }
            }
        })
        VM.ticketReceiveLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {

                }
                Status.Error -> {
                    val e = it.e
                    if (e is RequestException) {
                        if (e.code == 3004 || e.code == 3005) {
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        })
    }

    private fun initTitle() {
        titleText.text = "书券"
        titleBack.click { onBackPressed() }
    }

    override fun getPageName() = "书券"

    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }

    private var paused = false
    override fun onPause() {
        super.onPause()
        paused = true
    }

    override fun onResume() {
        super.onResume()
        if (paused)initData()
    }
}