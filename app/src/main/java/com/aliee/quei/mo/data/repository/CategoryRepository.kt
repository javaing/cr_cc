package com.aliee.quei.mo.data.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.CategoryBean
import com.aliee.quei.mo.data.bean.ComicBookBean
import com.aliee.quei.mo.data.bean.ListBean
import com.aliee.quei.mo.data.bean.toDataBean
import com.aliee.quei.mo.data.service.CategoryService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable

class CategoryRepository : BaseRepository(){
    private val service = RetrofitClient.createService(CategoryService::class.java)

    suspend fun getCategory(sort : Int = BeanConstants.SORT_ASC) : UIDataBean<List<CategoryBean>> {
        return try {
             service.getCategory(sort).toDataBean()
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }
    }

//    fun getList(lifecycleOwner: LifecycleOwner,id : Int,sex : Int,status : Int,page : Int,pageSize : Int) : Observable<ListBean<ComicBookBean>> {
//        return service.getList(id,sex, status, page, pageSize)
//            .compose(SchedulersUtil.applySchedulers())
//            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
//            .compose(handleBean())
//            .map {
//                val list = ListBean<ComicBookBean>()
//                list.pageSize = pageSize
//                list.page = page
//                list.list = it
//                list
//            }
//    }
}