package com.aliee.quei.mo.ui.comic.activity

import android.support.v4.app.FragmentPagerAdapter
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.utils.extention.click
import kotlinx.android.synthetic.main.activity_rank.*
import kotlinx.android.synthetic.main.layout_title.*

@Route(path = Path.PATH_RANK_ACTIVITY)
class RankingActivity : BaseActivity(){
    override fun getLayoutId() = R.layout.activity_rank

    override fun initData() {
    }

    override fun initView() {
        initTitle()
        initPage()
    }

    private fun initPage() {
        val category = mutableListOf(BeanConstants.RecommendPosition.MOST_POPULAR,
            BeanConstants.RecommendPosition.NEW,
            BeanConstants.RecommendPosition.RANK_FINISH,
            BeanConstants.RecommendPosition.FREE,
            BeanConstants.RecommendPosition.RANK_MALE,
            BeanConstants.RecommendPosition.RANK_FEMALE)
        val fragments = category.map {
            ARouterManager.getRankingFragment(this,it.rid)
        }
        pager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int) = fragments[position]
            override fun getCount() = category.size
        }
        tabLayout.setViewPager(pager, Array(category.size) { i->
            val title = category[i].title
            title
        })
    }

    private fun initTitle() {
        titleBack.click { onBackPressed() }
        titleText.text = getString(R.string.title_ranking)
    }

    override fun getPageName() = "排行页面"
}