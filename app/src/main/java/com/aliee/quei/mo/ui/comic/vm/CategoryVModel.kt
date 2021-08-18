package com.aliee.quei.mo.ui.comic.vm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.*
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.repository.CategoryRepository
import com.aliee.quei.mo.data.service.CategoryService
import com.aliee.quei.mo.net.retrofit.RetrofitClient

/**
 * Created by Administrator on 2018/4/28 0028.
 */
class CategoryVModel : BaseViewModel(){
    private val repository = CategoryRepository()
    private val categoryService = RetrofitClient.createService(CategoryService::class.java)

    val categoryLiveData = MediatorLiveData<UIDataBean<List<CategoryBean>>>()
    val listLiveData = MediatorLiveData<UIListDataBean<ComicBookBean>>()
    val morelistLiveData = MediatorLiveData<UIListDataBean<ComicBookBean>>()
    fun getCategory(sort: Int = BeanConstants.SORT_ASC) {
        categoryLiveData.value = UIDataBean(Status.Start)
        viewModelLaunch ({
            categoryLiveData.value = categoryService.getCategory(sort).toDataBean()
        }, {})
    }

    private var page = 1
    private val pageSize = 20

    fun getList(tid: Int,
        sex: Int = BeanConstants.SEX_ALL,
        status: Int = BeanConstants.STATUS_ALL
    ) {
        page = 1
//        repository.getList(lifecycleOwner,tid,sex,status,page, pageSize)
//            .subscribe(ListStatusResourceObserver(listLiveData))
        listLiveData.value = UIListDataBean(Status.Start, mutableListOf())
        viewModelLaunch({
            val list = ListBean<ComicBookBean>()
            list.pageSize = pageSize
            list.page = page
            list.list = categoryService.getList(tid,sex,status,page, pageSize).dataConvert()

            if(list.list.size==0)
                listLiveData.value = UIListDataBean(Status.Empty, mutableListOf())
            else
                listLiveData.value = UIListDataBean(Status.Success, list.list)
            listLiveData.value = UIListDataBean(Status.Complete, mutableListOf())
        },{
            listLiveData.value = UIListDataBean(Status.Error, mutableListOf())
        })
    }

    var rPage = 2
    private var rPageSize = 60
    fun loadMore(tid: Int,
            sex: Int = BeanConstants.SEX_ALL,
            status: Int = BeanConstants.STATUS_ALL
    ) {
        page ++
//        repository.getList(lifecycleOwner,tid,sex,status,rPage, rPageSize)
//                .subscribe(ListStatusResourceObserver(morelistLiveData))
        morelistLiveData.value = UIListDataBean(Status.Start, mutableListOf())
        viewModelLaunch({
            val list = ListBean<ComicBookBean>()
            list.pageSize = rPageSize
            list.page = rPage
            list.list = categoryService.getList(tid,sex,status,rPage, rPageSize).dataConvert()
            if(list.list.size==0)
                morelistLiveData.value = UIListDataBean(Status.Empty, mutableListOf())
            else
                morelistLiveData.value = UIListDataBean(Status.Success, list.list)
            morelistLiveData.value = UIListDataBean(Status.Complete, mutableListOf())
        },{
            morelistLiveData.value = UIListDataBean(Status.Error, mutableListOf())
        })
    }
}