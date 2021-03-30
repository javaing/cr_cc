package com.aliee.quei.mo.ui.main.fragment

import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.main.activity.ContentActivity
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.gone
import com.aliee.quei.mo.utils.extention.show
import kotlinx.android.synthetic.main.fragment_mine_comic.*
import kotlinx.android.synthetic.main.layout_title_first.*

/**
 * Created by Administrator on 2018/4/18 0018.
 */
@Route(path = Path.PATH_MAIN_FIRST_FRAGMENT)
class MineComicFragment : BaseFragment(), ViewPager.OnPageChangeListener {
    @Autowired
    @JvmField
    var showTab : Int = 0

    override fun getPageName(): Nothing? = null

    override fun onPageScrollStateChanged(state: Int) = Unit
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

    override fun onPageSelected(position: Int) {
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_mine_comic
    }

    override fun initView() {
        search.click {
            ARouterManager.goSearch(it.context)
        }
    }

    fun showTab(index : Int){
        if (!isPrepared)return
        try {
            var i = index
            if (i < 0)i = 0
            if (i > 1)i = 1
            viewPager.currentItem = i
        } catch (e : java.lang.Exception){
            e.printStackTrace()
        }
    }

    private fun initEvent() {
    }

    private val fragments = mutableListOf<BaseFragment>()
    private fun initFragment() {
        if (fragments.isNotEmpty())return
        val historyFragment = HistoryFragment.newInstance()
        val shelfFragment = ShelfFragment.newInstance()

        fragments.add(historyFragment)
        fragments.add(shelfFragment)
        viewPager.adapter = object : FragmentPagerAdapter(childFragmentManager){
            override fun getItem(position: Int) =  fragments[position]
            override fun getCount() = fragments.size
        }
        viewPager.addOnPageChangeListener(this)
        tabLayout.setViewPager(viewPager, arrayOf(getString(R.string.tab_history),getString(R.string.tab_shelf)))
    }

    override fun initData() {
//        clean.click {
//            val url = "file:///android_asset/demo.html"
//            ARouterManager.goWebActivity(it.context,url,"")
//        }
        finish.click {
            clean.show()
            finish.gone()
            if(activity is ContentActivity){
                (activity as ContentActivity).showTab()
            }
        }
        initFragment()
        initEvent()

        viewPager.currentItem = showTab
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isPrepared) {
            fragments.getOrNull(viewPager.currentItem)?.userVisibleHint = isVisibleToUser
        }
    }

    override fun scrollToTop() {
        val curItem = viewPager.currentItem
        try {
            fragments[curItem].scrollToTop()
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }
}