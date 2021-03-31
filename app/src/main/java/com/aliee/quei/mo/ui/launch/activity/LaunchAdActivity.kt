package com.aliee.quei.mo.ui.launch.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.aliee.quei.mo.BuildConfig
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.data.bean.AdBean
import com.aliee.quei.mo.data.bean.AdInfo
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.ui.main.vm.AdVModel
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.inflate
import com.aliee.quei.mo.utils.extention.loadHtmlImg
import com.aliee.quei.mo.utils.extention.loadNovelCover
import com.tmall.ultraviewpager.UltraViewPager
import com.tmall.ultraviewpager.UltraViewPagerAdapter
import kotlinx.android.synthetic.main.activity_ad_launch.*
import kotlinx.android.synthetic.main.activity_comic_read.view.*
import kotlinx.android.synthetic.main.activity_launch.*
import kotlinx.android.synthetic.main.activity_video_rank.*
import org.jetbrains.anko.find

/**
 * 开屏广告
 */
class LaunchAdActivity : BaseActivity() {
    private var adBeans = arrayListOf<AdBean>()
    private val adVm = AdVModel()
    var adInfos = mutableListOf<AdInfo>()
    private val fragments = mutableListOf<BaseFragment>()

    companion object {
        fun toThis(context: Context, adBeans: ArrayList<AdBean>) {
            val bundle = Bundle()
            bundle.putParcelableArrayList("adBeans", adBeans)
            val intent = Intent(context, LaunchAdActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_ad_launch
    }

    val newsAdBeans = mutableListOf<AdBean>()
    override fun initData() {
        val bundle = intent.extras
        if (bundle != null) {
            adBeans = bundle.getParcelableArrayList("adBeans")
            adBeans.forEach {
                if (AdConfig.interceptorAd(it)){
                    newsAdBeans.add(it)
                }
            }
        }
        if (newsAdBeans.size<=0){
          //  enterApp()
        }
        adVm.multipleLaunchAd(this, newsAdBeans, {
            val adMap = it
            adMap.forEach { entry ->
                adInfos.add(entry.value!!)
            }
            initViewPager()
        }, {
            enterApp()
        })
    }

    private fun initViewPager() {
        Log.d("tag", "viewpager adInfo:${adInfos.toString()}")
        ad_viewpager.adapter = UltraViewPagerAdapter(AdAdapter(adInfos))
        ad_viewpager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL)
        ad_viewpager.setInfiniteLoop(false)
        ad_viewpager.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                Log.d("tag", "viewPage onPageSelected: $position")
                AdConfig.adPreview(adInfos[position].callbackurl)
                timer(adInfos[position].sec!!)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
        timer(adInfos[0].sec!!)
        AdConfig.adPreview(adInfos[0].callbackurl)
    }

    var count = 0
    fun timer(sec: Int) {
        doDelays(sec, {
            btn_skip.text = "$it"
        }, {
            count++
            if (count == adInfos.size) {
                btn_skip.text = "跳过"
                btn_skip.click {
                    enterApp()
                }
                return@doDelays
            }
            ad_viewpager.setCurrentItem(count, true)
        })
    }

    inner class AdAdapter(val list: List<AdInfo>) : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val bean = list[position]
            val v = container.context.inflate(R.layout.launch_ad_item, container, false)
            val cover = v.find<ImageView>(R.id.iv)
            cover.loadHtmlImg(bean.imgurl)
            container.addView(v)
            v.click {
                AdConfig.adClick(v.context, bean.clickurl!!)
            }
            return v
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            `object` as View
            container.removeView(`object`)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun getCount(): Int {
            return if (list.size > 6) 6 else list.size
        }
    }
    /*private fun initFragment(adInfos: MutableList<AdInfo>) {
        val myContentPagerAdapter = LaunchAdFragmentAdapter(supportFragmentManager, adInfos)
        ad_viewPager.adapter = myContentPagerAdapter
    }*/
    /**
     * 进入应用
     */
    private fun enterApp() {
        if (isFinishing) return

        if (BuildConfig.needLogin && !CommonDataProvider.instance.hasLogin()) {
            ARouterManager.goLoginActivity(this, url = "app://comic.hkzy.com/main/activity?showPage=0")
            finish()
            return
        }

//        ARouterManager.goMainActivity(this, showPage = ARouterManager.TAB_SHOP)
//        ARouterManager.goReserveActivity(this)
        ARouterManager.goContentActivity(this, showPage = ARouterManager.TAB_SHOP)
        finish()
        return
    }

    override fun initView() {

    }


    override fun getPageName(): String? {
        return "开屏广告页"
    }


}