package com.aliee.quei.mo.ui.catalog.activity

import android.arch.lifecycle.Observer
import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.data.bean.CatalogItemBean
import com.aliee.quei.mo.database.DatabaseProvider
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.catalog.adapter.CatalogGridAdapter
import com.aliee.quei.mo.ui.catalog.adapter.CatalogListAdapter
import com.aliee.quei.mo.ui.catalog.vm.CatalogVModel
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.gone
import com.aliee.quei.mo.utils.extention.show
import com.aliee.quei.mo.widget.CommonDivider
import com.scwang.smartrefresh.layout.util.DensityUtil
import kotlinx.android.synthetic.main.activity_catalog.*
import kotlinx.android.synthetic.main.layout_title.*


@Route(path = Path.PATH_COMIC_CATALOG)
class CatalogActivity : BaseActivity(){

    var VM = CatalogVModel()
    lateinit var listAdapter: CatalogListAdapter
    lateinit var gridAdapter: CatalogGridAdapter

    @Autowired
    @JvmField
    var bookid : Int = 0
    var realm = DatabaseProvider.getRealm()
    override fun getLayoutId(): Int {
        return R.layout.activity_catalog
    }

    override fun initData() {
        VM.loadAllCatalogAndCacheWithRealm(this,bookid)
        initRecyclerView()
    }

    override fun initView() {
        initTitle()
        statuslayout.showContent()
        initVM()
    }

    private fun initTitle() {
        titleBack.click {
            finish()
        }
        titleText.text = getString(R.string.catalog)
        titleRightBtn.setImageResource(R.mipmap.ic_expand_catalog)
        titleRightBtn.show()
        titleRightBtn.click {
            toggleRecycler()
        }
    }

    private fun doRetry(){
        statuslayout.showLoading()
//        VM.loadCatalog(this,novelId)
        VM.loadAllCatalogAndCacheWithRealm(this,bookid)
    }

    private var hasMore = true
    private fun initVM() {
        VM.getCatalogLiveData.observe(this, Observer {
            when(it?.status){
                Status.NoNetwork -> {
                    statuslayout.showNoNetwork {
                       doRetry()
                    }
                }
                Status.Error -> {
                    disLoading()
                    statuslayout.showError {
                        doRetry()
                    }
                }
                Status.NoMore -> {
                    hasMore = false
                }
                Status.Success -> {

                    disLoading()
                    statuslayout.showContent()
                }
                else -> {
                }
            }
        })
    }

    private fun toggleRecycler() {
        if(grid.visibility == View.GONE){
            grid.show()
            recyclerView.gone()
            titleRightBtn.rotation = 180f
        } else {
            grid.gone()
            recyclerView.show()
            titleRightBtn.rotation = 0f
        }
    }

    private fun initRecyclerView() {
        val list = realm.where(CatalogItemBean::class.java).equalTo("bookid",bookid).findAll().sort("sort")
        listAdapter = CatalogListAdapter(list)
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        recyclerView.addItemDecoration(CommonDivider())
        recyclerView.adapter = listAdapter
        listAdapter.setCurrent(-1)
        listAdapter.onClick = {bean,position->
//            openUrl(RouterManager.getReadActivityUrl(bookBean.bid,bookBean.id, select(bookBean.sort - 1 < 0,0,bookBean.sort - 1)))
            ARouterManager.goReadActivity(this,bean.bookid,bean.id)
        }

        gridAdapter = CatalogGridAdapter(list)
        grid.layoutManager = GridLayoutManager(this,3)
        grid.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect?, itemPosition: Int, parent: RecyclerView?) {
                super.getItemOffsets(outRect, itemPosition, parent)
                val column = itemPosition % 3
                when(column){
                    0 -> outRect?.left = DensityUtil.dp2px(15f)
                    1 -> {
                        outRect?.left = DensityUtil.dp2px(10f)
                        outRect?.right = DensityUtil.dp2px(10f)
                    }
                    2 -> outRect?.right = DensityUtil.dp2px(15f)
                }
                outRect?.top = DensityUtil.dp2px(15f)
            }
        })
        grid.adapter = gridAdapter
        gridAdapter.listener = {start,end->
            (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(start,0)
            toggleRecycler()
        }
    }

    override fun getPageName(): String? {
        return "书籍目录"
    }

    override fun onDestroy() {
        super.onDestroy()
        recyclerView.adapter = null
        grid.adapter = null
        realm.close()
    }
}