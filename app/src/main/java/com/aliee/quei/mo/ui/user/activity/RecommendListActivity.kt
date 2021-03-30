package com.aliee.quei.mo.ui.user.activity

import android.arch.lifecycle.Observer
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.user.adapter.BonusAdapter
import com.aliee.quei.mo.ui.user.vm.BillVModel
import com.aliee.quei.mo.utils.extention.click
import kotlinx.android.synthetic.main.activity_recommend_bonus.*



@Route(path = Path.PATH_USER_RECOMMEND_BONUS)
class RecommendListActivity : BaseActivity(){
    private val VM = BillVModel()
    override fun getLayoutId() = R.layout.activity_recommend_bonus
    private val adapter = BonusAdapter()

    override fun initData() {
        VM.getBonusList(this)
    }

    override fun initView() {
        initTitle()
        initVM()
        initRecyclerView()
//        initRefresh()
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun initRefresh(){

    }

    private fun initVM() {
        VM.bonusListLiveData.observe(this, Observer {
            when (it?.status) {

                Status.Success -> {
                    Log.d("RecommendListActivity", "RecommendListData:" + it.data)
                    adapter.setData(it.data)
                }
                Status.Empty -> {

                }
                Status.NoMore -> {

                }
            }
        })
    }

    private fun initTitle() {
//        titleText.text = ""
        titleBack.click { onBackPressed() }
    }

    override fun getPageName() = ""
}