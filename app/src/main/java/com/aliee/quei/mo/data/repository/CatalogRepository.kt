package com.aliee.quei.mo.data.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.aliee.quei.mo.data.bean.CatalogItemBean
import com.aliee.quei.mo.data.bean.ListBean
import com.aliee.quei.mo.data.service.CatalogService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable

class CatalogRepository : BaseRepository(){
    private val service = RetrofitClient.createService(CatalogService::class.java)

    fun getCatalog(lifecycleOwner: LifecycleOwner,bookid : Int,page : Int,pageSize : Int,sort : Int) : Observable<ListBean<CatalogItemBean>>{
        return service.getCatalog(bookid, page, pageSize, sort)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
            .map {
                val listBean = ListBean<CatalogItemBean>()
                listBean.list = it.cChapterList
                listBean.count = it.count
                listBean.page = page
                listBean.pageSize = pageSize
                listBean
            }
    }
}