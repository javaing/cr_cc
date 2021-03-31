package com.aliee.quei.mo.data.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.aliee.quei.mo.component.EventReadHistoryUpdated
import com.aliee.quei.mo.data.bean.HistoryBean
import com.aliee.quei.mo.data.exception.RequestException
import com.aliee.quei.mo.data.service.HistoryService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.RxBus
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable

/**
 * Created by Administrator on 2018/4/26 0026.
 */
class HistoryRepository : BaseRepository(){
    private val service = RetrofitClient.createService(HistoryService::class.java)

    fun loadHistory(lifecycleOwner: LifecycleOwner) : Observable<List<HistoryBean>>{
        return service.loadHistory()
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
                .compose(handleList())
    }

    fun delRecord(lifecycleOwner: LifecycleOwner,bookId: Int) : Observable<String>{
        return service.delHistory(bookId)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
                .map {
                    if (it.code != 0) {
                        throw RequestException(it.code,it.msg.toString())
                    }
                    bookId.toString()
                }
//                .compose(handleBean())
    }

    fun addHistory(lifecycleOwner: LifecycleOwner,bookId : Int,chapterId: Int) : Observable<Any> {
        return service.addHistory(bookId,chapterId)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .map {
                RxBus.getInstance().post(EventReadHistoryUpdated(bookId))
                it
            }
            .compose(handleBean())
    }
}