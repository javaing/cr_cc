package com.aliee.quei.mo.ui.video

import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.main.fragment.HistoryFragment
import com.aliee.quei.mo.ui.main.fragment.ShelfFragment
import com.aliee.quei.mo.ui.video.fragment.VideoRankFragment
import com.aliee.quei.mo.utils.extention.click
import kotlinx.android.synthetic.main.activity_video_rank.*
import kotlinx.android.synthetic.main.activity_video_rank.tabLayout
import kotlinx.android.synthetic.main.activity_video_rank.viewPager
import kotlinx.android.synthetic.main.fragment_mine_comic.*
import kotlinx.android.synthetic.main.layout_title_first.*


@Route(path = Path.PATH_VIDEO_RANKING)
class VideoRankActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_video_rank
    }

    override fun initData() {

    }

    override fun initView() {
        titleBack.click { finish() }
        titleEdit.visibility = View.GONE
        initFragment()
    }

    override fun getPageName(): String? {
        return "榜单"
    }

    private val fragments = mutableListOf<BaseFragment>()
    private fun initFragment() {
        if (fragments.isNotEmpty()) return
        fragments.add(VideoRankFragment.newInstance(0))
        fragments.add(VideoRankFragment.newInstance(1))
        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int) = fragments[position]
            override fun getCount() = fragments.size
        }
        tabLayout.setViewPager(viewPager, arrayOf("日榜单", "周榜单"))
    }
}