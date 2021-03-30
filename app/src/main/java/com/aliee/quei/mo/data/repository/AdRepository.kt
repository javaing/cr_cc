package com.aliee.quei.mo.data.repository

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.util.Log
import com.aliee.quei.mo.data.bean.AdBean
import com.aliee.quei.mo.data.bean.AdInfo
import com.aliee.quei.mo.data.bean.CheckInStatsBean
import com.aliee.quei.mo.data.service.AdService
import com.aliee.quei.mo.data.service.AuthService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.google.gson.Gson
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable
import okhttp3.ResponseBody

class AdRepository :BaseRepository() {
    private val service = RetrofitClient.createService(AdService::class.java)


    fun getAdList(lifecycleOwner: LifecycleOwner,groupId:Int =1) : Observable<MutableList<AdBean>> {
        return service.adList(groupId)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                  /*  it.forEach {
                        Log.d("tag","adBean:${it.toString()}")
                    }*/
                    it
                }
    }

    fun getAdInfo(lifecycleOwner: LifecycleOwner,url:String):Observable<AdInfo>{
        return service.adInfo(url)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .map {
                    val resp = it.string()
                    Log.d("tag","url:$url, resp:${resp}")
                   Gson().fromJson(resp,AdInfo::class.java)
                }

    }

}