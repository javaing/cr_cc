package com.aliee.quei.mo.data.repository

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import com.aliee.quei.mo.data.bean.BaseResponse
import com.aliee.quei.mo.data.bean.BulletinBean
import com.aliee.quei.mo.data.bean.BulletinDetailBean
import com.aliee.quei.mo.data.bean.CheckInStatsBean
import com.aliee.quei.mo.data.service.CheckInService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable

class CheckInRepository : BaseRepository() {
    private val service = RetrofitClient.createService(CheckInService::class.java)

    fun getDailyCheckInStats(lifecycleOwner: LifecycleOwner) : Observable<List<CheckInStatsBean>> {
        return service.getWeekCheckStats()
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
    }

    fun checkIn(lifecycleOwner: LifecycleOwner,date : String = "") : Observable<Int> {
        return service.checkIn(date)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
            .map {
                it.bookbean
            }
    }

    fun getBulletin(lifecycleOwner: LifecycleOwner) : Observable<BulletinBean> {
        return service.getBulletin()
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())

    }

    fun getBulletinList(lifecycleOwner: LifecycleOwner,status : Int,page : Int,pageSize : Int) : Observable<BaseResponse<Object>> {
        return service.getBulletinList(page,status,pageSize)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
                .map {
                    //  CommonDataProvider.instance.currentReading = it[0].book
                    it
                }

    }

    fun getBulletinDetail(lifecycleOwner: LifecycleOwner,bulletinId : Int) : Observable<BulletinDetailBean> {
        return service.getBulletinDetail(bulletinId)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())



    }
}