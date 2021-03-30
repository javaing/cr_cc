package com.aliee.quei.mo.ui.comic.vm

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.ListStatusResourceObserver
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.base.response.UIListDataBean
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.CategoryBean
import com.aliee.quei.mo.data.bean.ComicBookBean
import com.aliee.quei.mo.data.repository.CategoryRepository

/**
 * Created by Administrator on 2018/4/28 0028.
 */
class CategoryVModel : BaseViewModel(){
    private val repository = CategoryRepository()

    val categoryLiveData = MediatorLiveData<UIDataBean<List<CategoryBean>>>()
    val listLiveData = MediatorLiveData<UIListDataBean<ComicBookBean>>()
    val morelistLiveData = MediatorLiveData<UIListDataBean<ComicBookBean>>()
    fun getCategory(lifecycleOwner: LifecycleOwner, sort: Int = BeanConstants.SORT_ASC) {
        repository.getCategory(lifecycleOwner, sort)
            .subscribe(StatusResourceObserver(categoryLiveData))
    }

    private var page = 1
    private val pageSize = 20

    fun getList(
        lifecycleOwner: LifecycleOwner,
        tid: Int,
        sex: Int = BeanConstants.SEX_ALL,
        status: Int = BeanConstants.STATUS_ALL
    ) {
        page = 1
        repository.getList(lifecycleOwner,tid,sex,status,page, pageSize)
            .subscribe(ListStatusResourceObserver(listLiveData))
    }

    var rPage = 2
    private var rPageSize = 60
    fun loadMore(
            lifecycleOwner: LifecycleOwner,
            tid: Int,
            sex: Int = BeanConstants.SEX_ALL,
            status: Int = BeanConstants.STATUS_ALL
    ) {
        page ++
        repository.getList(lifecycleOwner,tid,sex,status,rPage, rPageSize)
                .subscribe(ListStatusResourceObserver(morelistLiveData))
    }
}