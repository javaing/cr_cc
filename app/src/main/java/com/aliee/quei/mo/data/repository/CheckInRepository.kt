package com.aliee.quei.mo.data.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.service.CheckInService
import com.aliee.quei.mo.net.ApiConstants
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

    suspend fun getBulletinList(status : Int,page : Int,pageSize : Int) : UIDataBean<BaseResponse<Any>> {
        val data = service.getBulletinList(page,status,pageSize)
        return UIDataBean(Status.Success, data)
    }

    suspend fun getBulletinDetail(bulletinId : Int) : UIDataBean<BulletinDetailBean> {
        return service.getBulletinDetail(bulletinId).toDataBean()
    }


    suspend fun autoCheckIn(date: String = ""):UIDataBean<CoinBean> {
        return try {
            service.checkInK(date).toDataBean()
        } catch (e:Exception) {
            UIDataBean(Status.Error)
        }
    }

    suspend fun getBulletin():UIDataBean<BulletinBean> {
        return try {
            service.getBulletinK().toDataBean()
        } catch (e:Exception) {
            UIDataBean(Status.Error)
        }
    }
}