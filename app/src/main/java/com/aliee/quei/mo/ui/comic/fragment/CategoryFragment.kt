package com.aliee.quei.mo.ui.comic.fragment

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.comic.adapter.MoreRankAdapter
import com.aliee.quei.mo.ui.comic.vm.CategoryVModel
import com.aliee.quei.mo.ui.common.ShopItemDecoration
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.layout_common_list.*

@Route(path = Path.PATH_COMIC_CATEGORY_FRAGMENT)
class CategoryFragment : BaseFragment(){
    @Autowired
    @JvmField var tid : Int = 0

    @Autowired
    @JvmField var status = 0

    @Autowired
    @JvmField var sex = 0

    private val adapter = MoreRankAdapter()
    private val VM = CategoryVModel()
    override fun getLayoutId() = R.layout.fragment_category

    override fun initView() {
        initVM()
        initRecyclerView()
        initRefresh()
    }

    private fun initRefresh() {
        refreshLayout.setOnRefreshListener {
            VM.getList( tid = tid, sex = sex, status = status)
        }
        refreshLayout.setOnLoadMoreListener {
            VM.loadMore(tid = tid, sex = sex, status = status)
        }
    }

    private fun initRecyclerView() {
        recyclerView.adapter = adapter
        val layoutManager = GridLayoutManager(activity, 6)
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
            ARouterManager.goComicDetailActivity(activity!!,it.id,true)
        }
    }

    private fun initVM() {

        VM.morelistLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    adapter.add(it.data.map {
                        RecommendBookBean(it.author,it.thumb,it.id,it.description,it.status,it.thumbX,it.title,it.typename)
                    })
                }
                Status.Complete -> {
                    refreshLayout.finishRefresh()
                    refreshLayout.finishLoadMore()
                }
            }
        })

        VM.listLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    statuslayout.showContent()
                    adapter.setData(it.data.map {
                        RecommendBookBean(it.author,it.thumb,it.id,it.description,it.status,it.thumbX,it.title,it.typename)
                    })
                }
                Status.Complete -> {
                    refreshLayout.finishRefresh()
                    refreshLayout.finishLoadMore()
                }
                Status.Start -> {
                    if (isFirst)statuslayout.showLoading()
                    refreshLayout.isEnableLoadMore = true
                }
                Status.Empty -> {
                    statuslayout.showEmpty()
                }
                Status.Error -> statuslayout.showError {
                    VM.getList( tid = tid, sex=sex, status = status)
                }
                Status.NoMore -> {
//                    refreshLayout.isEnableLoadMore = false
                    refreshLayout.finishRefresh()
                    refreshLayout.finishLoadMore()
                }
            }
        })
    }

    fun filter (sex : Int = this.sex,status : Int = this.status) {
        this.sex = sex
        this.status = status
        VM.getList( tid = tid, sex = this.sex, status = this.status)
    }

    override fun initData() {
        VM.getList( tid)
    }

    override fun getPageName() = "漫画分类"
}