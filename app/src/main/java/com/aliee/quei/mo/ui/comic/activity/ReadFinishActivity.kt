package com.aliee.quei.mo.ui.comic.activity

import android.arch.lifecycle.Observer
import android.support.v7.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.comic.adapter.ReadFinishAdapter
import com.aliee.quei.mo.ui.comic.vm.ReadFinishVModel
import com.aliee.quei.mo.utils.extention.click
import kotlinx.android.synthetic.main.activity_read_finish.*
import kotlinx.android.synthetic.main.layout_title.*

@Route(path = Path.PATH_READ_FINISH)
class ReadFinishActivity : BaseActivity(){
    @Autowired
    @JvmField var bookName : String = ""

    @Autowired
    @JvmField var bookStatus : Int = BeanConstants.STATUS_FINISH

    private val adapter = ReadFinishAdapter()
    private val VM = ReadFinishVModel()

    override fun getLayoutId(): Int {
        return R.layout.activity_read_finish
    }

    override fun initData() {
        VM.getGuessLike(this)
    }

    override fun initView() {
        initVM()
        initRecyclerView()
        initTitle()
    }

    private fun initRecyclerView() {
        recyclerView.adapter = adapter
        val layoutManager = GridLayoutManager(this,3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                if (position == 0)return 3
                return 1
            }
        }
        recyclerView.layoutManager = layoutManager
        adapter.setStatus(bookStatus)
        adapter.recommendClick = {
            ARouterManager.goComicDetailActivity(this,it.id)
        }
    }

    private fun initTitle() {
        titleText.text = bookName
        titleBack.click { onBackPressed() }
    }

    private fun initVM() {
        VM.guessLikeLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    adapter.setGuessLike(it.data)
                }
            }
        })
    }

    override fun getPageName() = "阅读完成页"
}