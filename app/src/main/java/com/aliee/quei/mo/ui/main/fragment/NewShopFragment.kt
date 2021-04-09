package com.aliee.quei.mo.ui.main.fragment

import androidx.lifecycle.Lifecycle
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler
import com.aliee.quei.mo.R
import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.component.EventHideBottomBar
import com.aliee.quei.mo.component.EventReturnComic
import com.aliee.quei.mo.component.EventShowBottomBar
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.main.activity.ContentActivity
import com.aliee.quei.mo.ui.web.fragment.BaseWebFragment3
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.gone
import com.aliee.quei.mo.utils.extention.show
import com.aliee.quei.mo.utils.extention.toast
import com.aliee.quei.mo.utils.rxjava.RxBus
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_mine_comic.viewPager
import kotlinx.android.synthetic.main.fragment_new_shop.*
import kotlinx.android.synthetic.main.layout_title_first.*

/**
 * Created by Administrator on 2018/4/18 0018.
 */
@Route(path = Path.PATH_NEW_SHOP_FRAGMENT)
class NewShopFragment : BaseFragment(), FragmentBackHandler {
    @Autowired
    @JvmField
    var showTab : Int = 0

    override fun getPageName(): Nothing? = null

//    override fun onPageScrollStateChanged(state: Int) = Unit
//    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit
//
//    override fun onPageSelected(position: Int) {
//            if(position == 1) {
//                RxBus.getInstance().post(EventHideBottomBar())
//                search.gone()
//            }else{
//                RxBus.getInstance().post(EventShowBottomBar())
//                search.show()
//            }
//
//    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_new_shop
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
            mviewPager.currentItem = i
        } catch (e : java.lang.Exception){
            e.printStackTrace()
        }
    }

    private fun initEvent() {

        RxBus.getInstance().toMainThreadObservable(ReaderApplication.instance, Lifecycle.Event.ON_DESTROY).subscribe({
            when(it){
                is EventReturnComic -> {
                    if (mviewPager.currentItem !=0) {
                        mviewPager.currentItem = viewPager.currentItem - 1
                    }
                }

            }
        },{
            it.printStackTrace()
        })
    }

    private val fragments = mutableListOf<BaseFragment>()
    private fun initFragment() {
        if (fragments.isNotEmpty())return
        val ShopFragment = ShopFragment.newInstance()
        val VideoFragment = BaseWebFragment3.newInstance()

        fragments.add(ShopFragment)
        fragments.add(VideoFragment)
        mviewPager.adapter = object : FragmentStateAdapter(requireActivity()){
            override fun createFragment(position: Int) =  fragments[position]
            override fun getItemCount() = fragments.size
        }
        //mviewPager.setPagingEnabled(false)
        //mviewPager.addOnPageChangeListener(this)
        //tabLayout.setViewPager(mviewPager, arrayOf(getString(R.string.app_name),getString(R.string.videoname)))

        mviewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 1) {
                    RxBus.getInstance().post(EventHideBottomBar())
                    search.gone()
                } else {
                    RxBus.getInstance().post(EventShowBottomBar())
                    search.show()
                }
            }
        })
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.app_name)
                1 -> getString(R.string.videoname)
                else -> null
            }
        }.attach()

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

        mviewPager.currentItem = showTab
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isPrepared) {
            fragments.getOrNull(mviewPager.currentItem)?.userVisibleHint = isVisibleToUser
        }
    }

    override fun scrollToTop() {
        val curItem = mviewPager.currentItem
        try {
            fragments[curItem].scrollToTop()
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }
    private var exitTime = 0L
    override fun onBackPressed(): Boolean {
        if (!BackHandlerHelper.handleBackPress(this)) {
            if (mviewPager.currentItem !=0) {
                mviewPager.currentItem = mviewPager.currentItem - 1

            }else{
                if (System.currentTimeMillis() - exitTime > 2000) {
                    toast(getString(R.string.tap_again_to_exit))
                    exitTime = System.currentTimeMillis()
                }
            }
        }
        return false
    }
}