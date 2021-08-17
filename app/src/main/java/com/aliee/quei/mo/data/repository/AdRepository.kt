package com.aliee.quei.mo.data.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import android.util.Log
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.bean.*
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

    suspend fun getAdList(groupId:Int =1) : UIDataBean<MutableList<AdBean>> {
        return try {
            service.adList(groupId).toDataBean()
        } catch (e: Exception) {
            UIDataBean(Status.Error)
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

    suspend fun getAdInfo(url:String):UIDataBean<AdInfo>{
        val resp = service.adInfoK(url).string()
        //Log.d("tag","AdRepository:$resp")
        return UIDataBean(Status.Success, Gson().fromJson(resp,AdInfo::class.java))
    }

}