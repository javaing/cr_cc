package com.aliee.quei.mo.data.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.service.BillService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable

class BillRepository : BaseRepository(){
    private val service = RetrofitClient.createService(BillService::class.java)

    fun getBillList(lifecycleOwner: LifecycleOwner,page : Int,pageSize : Int) : Observable<ListBean<BillRecordBean>> {
        return service.getBillList(page, pageSize)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
    }

    fun getBonusList(lifecycleOwner: LifecycleOwner) : Observable<List<BonusRecordBean>> {
        return service.getBonusList()
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
    }

    fun getBonusID(lifecycleOwner: LifecycleOwner,recommendcode : String) : Observable<BonusIDBean> {
        return service.getBonusID(recommendcode)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
    }

    fun sendExange(lifecycleOwner: LifecycleOwner,exchangecode : String) : Observable<Any> {
        return service.sendExchange(exchangecode)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
    }

}