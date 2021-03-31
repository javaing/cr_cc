package com.aliee.quei.mo.ui.comic.fragment

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.config.AdEnum
import com.aliee.quei.mo.data.bean.Option
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.comic.adapter.MoreAdapter
import com.aliee.quei.mo.ui.comic.vm.RankVModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_rank.*
import kotlinx.android.synthetic.main.layout_common_list.*
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.support.v4.toast

@Route(path = Path.PATH_RANK_FRAGMENT)
class RankFragment : BaseFragment() {
    private val VM = RankVModel()

    @Autowired
    @JvmField
    var rid: String = "6"

    private val adapter = MoreAdapter()
    override fun getLayoutId(): Int {
        return R.layout.fragment_rank
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
            if (it.bookcover!!.contains("http")){
                AdConfig.adClick(activity!!,it.adClickUrl!!)
            }else{
                ARouterManager.goComicDetailActivity(activity!!, it.id, true)
            }
        }
    }

    private fun initVM() {
        VM.listLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Start -> {
                    refreshLayout.setNoMoreData(false)
                }
                Status.Success -> {
                    adapter.setData(it.data)
                    statuslayout.showContent()
                    refreshLayout.finishLoadMore()
                    refreshLayout.finishRefresh()

                    rankAd()
                }
                Status.Empty -> statuslayout.showEmpty()
                Status.Error -> statuslayout.showError { initData() }
                Status.NoNetwork -> statuslayout.showNoNetwork { initData() }
                Status.NoMore -> {
                    refreshLayout.finishLoadMoreWithNoMoreData()
                }
            }
        })
    }

    override fun initData() {
        VM.loadRank(this, rid)
    }


    fun rankAd() {
        val adBean = AdConfig.getAd(AdEnum.COMIC_RANK.zid)
        adBean?.also { adBean ->
            AdConfig.getAdInfo(adBean, {
                val option = Gson().fromJson<Option>(it.optionstr,Option::class.java)
                it.title = option.title
                it.desc = option.desc
                context!!.runOnUiThread {
                  adapter.insertAd(adBean.interval,it)
                }
            }, {

            })
        }
    }

    override fun getPageName() = ""
}