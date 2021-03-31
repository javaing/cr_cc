package com.aliee.quei.mo.ui.video

import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.ViewGroup
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.video.fragment.VideoRecommendFragment
import com.aliee.quei.mo.utils.extention.click
import kotlinx.android.synthetic.main.activity_video_rank.*
import java.util.*

@Route(path = Path.PATH_VIDEO_RECOMMEND)
class VideoRecommendActivity : BaseActivity() {
    private var mCurrentPosition = 0
    private val mBooleans: MutableList<Boolean> = ArrayList() //记录每个fragment的编辑状态


    private lateinit var recommendFragment: VideoRecommendFragment
    override fun getLayoutId(): Int {
        return R.layout.activity_video_rank
    }

    override fun initData() {

    }

    override fun initView() {
        titleBack.click { finish() }
        titleEdit.click { editStatus() }
        initFragment()
    }

    override fun getPageName(): String? {
        return "记录"
    }

    private val fragments = mutableListOf<BaseFragment>()
    private fun initFragment() {
        if (fragments.isNotEmpty()) return
        for (i in 0..1) {
            fragments.add(VideoRecommendFragment.newInstance(i))
            mBooleans.add(false)
        }

        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int) = fragments[position]
            override fun getCount() = fragments.size
            override fun setPrimaryItem(container: ViewGroup, position: Int, any: Any) {
                recommendFragment = any as VideoRecommendFragment
                super.setPrimaryItem(container, position, any)
            }
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                /**
                 * 记录滑动的位置
                 * 判断当前是否是编辑状态
                 */
                mCurrentPosition = position
                val isEdit: Boolean = mBooleans[mCurrentPosition]
                titleEdit.text = if (isEdit) "完成" else "编辑"
            }

        })
        tabLayout.setViewPager(viewPager, arrayOf("我的视频","观看记录"))
    }

    var isEdit = false
    private fun editStatus() {
        isEdit = !mBooleans[mCurrentPosition]
        if (isEdit) {
            titleEdit.text = "完成"
        } else {
            titleEdit.text = "编辑"
        }
        recommendFragment.setEdit(isEdit)
        //记录当前fragment为编辑状态
        mBooleans[mCurrentPosition] = isEdit
    }
}