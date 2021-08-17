package com.aliee.quei.mo.ui.main.fragment

import androidx.lifecycle.Observer
import android.content.Context
import androidx.viewpager.widget.ViewPager
import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.dueeeke.videoplayer.player.VideoViewManager
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.data.bean.Tag
import com.aliee.quei.mo.data.bean.TagCount
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.main.adapter.VideoTabFragmentAdapter
import com.aliee.quei.mo.ui.main.vm.MainVideoModel
import com.aliee.quei.mo.utils.TagCountManager
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.widget.view.TabTitleView
import kotlinx.android.synthetic.main.fragment_video.*
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView


@Route(path = Path.PATH_MAIN_VIDEO_FRAGMENT)
class VideoFragment : BaseFragment() {
    //private val VM = MainVideoModel()
    var tags: MutableList<Tag> = mutableListOf()


    override fun getPageName(): String? = "视频"

    override fun getLayoutId(): Int {
        return R.layout.fragment_video
    }

    override fun initView() {
        tv_search.click {
            ARouterManager.goSearchVideoActivity(context!!)
        }
        btn_search.click {
            ARouterManager.goSearchVideoActivity(context!!)
        }

        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                    VideoViewManager.instance().releaseByTag("list")
            }
        })
    }

    override fun initData() {
        tags = CommonDataProvider.instance.getVideoTags()
        initIndicator()
        TagCountManager.init(tags)
        Log.d("tagcount", "tagCount:${TagCountManager.getTagCount().toString()}")
    }

    private fun initIndicator() {
        val myContentPagerAdapter = VideoTabFragmentAdapter(requireActivity(), tags)
        view_pager.adapter = myContentPagerAdapter

        val commonNavigator = CommonNavigator(context)
        commonNavigator.scrollPivotX = 0.2f
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                val tagView = TabTitleView(context)
                tagView.normalColor = resources.getColor(R.color.video_tags_nor_color)
                tagView.selectedColor = resources.getColor(R.color.video_tags_selected_color)
                tagView.text = tags[index].tag
                tagView.textSize = 20f
                tagView.setOnClickListener {
                    view_pager.currentItem = index
                    //点击每个Tag进行记录
                    val tag = tags[index]
                    if (tag.id == -1) {
                        return@setOnClickListener
                    }
                    TagCountManager.saveOnlyTagCount(tag)
                }
                return tagView
            }

            override fun getCount(): Int {
                return if (tags.isEmpty()) 0 else tags.size
            }

            override fun getIndicator(context: Context?): IPagerIndicator? {
                return null
            }

        }
        magic_indicator.navigator = commonNavigator
        bindbind(magic_indicator, view_pager)

    }

    private fun bindbind(magicIndicator: MagicIndicator, viewPager: ViewPager2) {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                magicIndicator.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                magicIndicator.onPageScrollStateChanged(state)
            }
        })
    }

//    private fun initVM() {
//        VM.mainVideoTags.observe(this, Observer {
//            when (it?.status) {
//                Status.Success -> {
//                    tags = it.data!!
//                    initIndicator()
//                }
//            }
//        })
//    }

    override fun onDetach() {
        super.onDetach()
        VideoViewManager.instance().releaseByTag("list")
    }

}