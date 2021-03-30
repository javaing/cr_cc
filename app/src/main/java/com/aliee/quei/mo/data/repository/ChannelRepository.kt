package com.aliee.quei.mo.data.repository

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import com.aliee.quei.mo.data.bean.ChannelInfoBean
import com.aliee.quei.mo.data.service.ChannelService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable

class ChannelRepository : BaseRepository() {
    private var service = RetrofitClient.createService(ChannelService::class.java)

    fun getChannelInfo (lifecycleOwner: LifecycleOwner,id : String) : Observable<ChannelInfoBean> {
        return service.getUserChannelInfo(id)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
    }
}