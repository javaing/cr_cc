package com.aliee.quei.mo.ui.comic.fragment

import android.support.v7.widget.GridLayoutManager
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseDialogFragment
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.ui.comic.adapter.ChpaterEndRecommendAdapter
import com.aliee.quei.mo.utils.extention.click
import kotlinx.android.synthetic.main.dialog_recommend.*

class TwoChapterRecommendDialog : BaseDialogFragment(){
    private val adapter = ChpaterEndRecommendAdapter()
    override fun getLayoutId(): Int {
        return R.layout.dialog_recommend
    }

    override fun initView() {
        recyclerView.layoutManager = GridLayoutManager(activity,2)
        recyclerView.adapter = adapter
        btnDismiss.click { dismiss() }
    }

    fun setData(list : List<RecommendBookBean>) {
        adapter.setData(list)
    }

    override fun initData() {
    }
}