package com.aliee.quei.mo.data.repository

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import com.aliee.quei.mo.data.service.MissionService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable

class MissionRepository : BaseRepository() {
    private val service = RetrofitClient.createService(MissionService::class.java)
    fun dailyLogin(lifecycleOwner: LifecycleOwner) : Observable<Int> {
        return service.dailyLogin()
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
            .map {
                it.bean
            }
    }
}