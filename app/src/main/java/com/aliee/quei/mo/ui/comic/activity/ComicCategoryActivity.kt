package com.aliee.quei.mo.ui.comic.activity

import android.arch.lifecycle.Observer
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.data.bean.CategoryBean
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.comic.fragment.CategoryFragment
import com.aliee.quei.mo.ui.comic.vm.CategoryVModel
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.disable
import com.aliee.quei.mo.utils.extention.enable
import com.aliee.quei.mo.utils.extention.show
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.layout_title.*

@Route(path = Path.PATH_COMIC_CATEGORY)
class ComicCategoryActivity : BaseActivity(){
    private val VM = CategoryVModel()

    private var status = 0
    private var sex = 0
    override fun getLayoutId(): Int {
        return R.layout.activity_category
    }

    override fun initData() {
        VM.getCategory(this)
    }

    override fun initView() {
        initTitle()
        initVM()
        initClick()
    }

    private fun initClick() {
        statusAll.click { status = 0 ;update(it)}
        status1.click   { status = 1 ;update(it)}
        status2.click   { status = 2 ;update(it)}
        sexAll.click    { sex = 0 ;update(it)}
        sex1.click      { sex = 1 ;update(it)}
        sex2.click      { sex = 2 ;update(it)}
    }

    private fun update(view : View) {
        if (view.parent is ViewGroup) {
            (view.parent as ViewGroup).enable()
            view.disable()
        }
        fragments.getOrNull(pager.currentItem)?.filter(sex = this.sex,status = this.status)
    }

    private fun initTitle() {
        titleText.text = getString(R.string.category)
        titleBack.click { onBackPressed() }
        titleRightBtn.show()
        titleRightBtn.setImageResource(R.mipmap.ic_search_dl)
        titleRightBtn.click { ARouterManager.goSearch(this) }
    }

    private fun initVM() {
        VM.categoryLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Start -> showLoading()
                Status.Complete -> disLoading()
                Status.Success -> {
                    Log.d("ComicCategoryActivity","ComicCategory "+it.data)
                    showTab(it.data)
                }
            }
        })
    }

    lateinit var fragments : List<CategoryFragment>
    private fun showTab(data: List<CategoryBean>?) {
        data?:return
        val category = mutableListOf(CategoryBean(0,getString(R.string.category_all)))
        category.addAll(data)
        fragments = category.map {
            ARouterManager.getCategoryFragment(this,it.id,status, sex)
        }
        pager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int) = fragments[position]
            override fun getCount() = category.size
        }
        tabLayout.setViewPager(pager, Array(category.size) { i->
            category[i].typename
        })
    }

    override fun getPageName() = "分类"
}