package com.aliee.quei.mo.data.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.component.EventReadHistoryUpdated
import com.aliee.quei.mo.data.bean.HistoryBean
import com.aliee.quei.mo.data.bean.toDataBean
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

    suspend fun loadHistory():UIDataBean<List<HistoryBean>> {
        return try {
            service.loadHistory().toDataBean()
        } catch (e: Exception){
            UIDataBean(Status.Error)
        }
    }

    suspend fun delRecord(bookId: Int) : UIDataBean<String?>{
        try {
            service.delHistory(bookId)
        } catch (e: Exception) {
            return UIDataBean(Status.Error)
        }
        return UIDataBean(Status.Success,bookId.toString())
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