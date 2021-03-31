package com.aliee.quei.mo.ui.comic.activity

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.comic.adapter.MoreRankAdapter
import com.aliee.quei.mo.ui.comic.vm.MoreVModel
import com.aliee.quei.mo.ui.common.ShopItemDecoration
import com.aliee.quei.mo.utils.extention.click
import kotlinx.android.synthetic.main.activity_more.*
import kotlinx.android.synthetic.main.layout_title.*

@Route(path = Path.PATH_COMIC_MORE)
class MoreActivity : BaseActivity(){
    private val VM = MoreVModel()
    private val adapter = MoreRankAdapter()

    @Autowired
    @JvmField var title : String = ""

    @Autowired
    @JvmField var rid : String = ""

    override fun getLayoutId() = R.layout.activity_more

    override fun initData() {
        VM.getByRid(this,rid)
    }

    override fun initView() {
        initTitle()
        initRecyclerView()
        initVM()
        initRefresh()
    }

    private fun initRefresh() {
        refreshLayout.setOnLoadMoreListener {
            VM.loadMore(this)
        }
        refreshLayout.setOnRefreshListener {
            VM.getByRid(this,rid)
        }
    }

    private fun initVM() {
        VM.listLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Start -> showLoading()
                Status.Success -> {
                    adapter.setData(it.data)
                }
                Status.Complete -> disLoading()
            }
        })
        VM.comicListLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    adapter.add(it.data?.list)
                }
                Status.Complete -> {
                    refreshLayout.finishRefresh()
                    refreshLayout.finishLoadMore()
                }
                Status.Start -> {
                    refreshLayout.setNoMoreData(false)
                }
                Status.Empty -> {
                    refreshLayout.setNoMoreData(true)
                }
            }
        })
    }

    private fun initRecyclerView() {
        recyclerView.adapter = adapter
        val layoutManager = GridLayoutManager(this, 6)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = adapter.getItemViewType(position)
                return when (viewType) {
                    ShopItemDecoration.VIEW_TYPE_COMIC_GRID_2 -> return 3
                    ShopItemDecoration.VIEW_TYPE_COMIC_GRID_3 -> return 2
                    else -> return 6
                }
            }
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(ShopItemDecoration())
        adapter.itemClick = {
            ARouterManager.goComicDetailActivity(this,it.id)
        }
    }

    private fun initTitle() {
        titleText.text = title
        titleBack.click { onBackPressed() }
    }

    override fun getPageName(): String? {
        return "更多:$title"
    }
}