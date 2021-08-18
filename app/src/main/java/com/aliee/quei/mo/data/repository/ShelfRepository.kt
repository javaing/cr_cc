package com.aliee.quei.mo.data.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.component.EventShelfChanged
import com.aliee.quei.mo.data.bean.ListBean
import com.aliee.quei.mo.data.bean.ShelfBean
import com.aliee.quei.mo.data.service.ShelfService
import com.aliee.quei.mo.database.ShelfDBManager
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.RxBus
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable


/**
 * Created by Administrator on 2018/4/26 0026.
 */
class ShelfRepository : BaseRepository(){
    private val service = RetrofitClient.createService(ShelfService::class.java)
//    fun addToShelf(lifecycleOwner: LifecycleOwner,bookid : Int): Observable<Any>{
//        return service.addToShelf(bookid)
//                .compose(SchedulersUtil.applySchedulers())
//                .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
//                .map {
//                    it.data = bookid
//                    RxBus.getInstance().post(EventShelfChanged())
//                    it
//                }
//                .compose(handleBean())
//    }

    suspend fun addToShelf(bookid : Int): UIDataBean<Any>{
       return try {
            val it= service.addToShelf(bookid)
            it.data = bookid
            RxBus.getInstance().post(EventShelfChanged())
            UIDataBean(Status.Success, it)
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }
    }


    fun getList(lifecycleOwner: LifecycleOwner, page: Int, pageSize: Int) : Observable<ListBean<ShelfBean>>{
        return service.getShelfList(page,pageSize)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    ShelfDBManager.saveAll(it)
                    val listBean = ListBean<ShelfBean>()
                    listBean.list = it
                    listBean.page = page
                    listBean.pageSize = pageSize
                    listBean
                }
    }
}