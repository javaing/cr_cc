package com.aliee.quei.mo.data.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.aliee.quei.mo.data.bean.AppUpdate
import com.aliee.quei.mo.data.bean.VersionInfoBean
import com.aliee.quei.mo.data.service.VersionService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable
import retrofit2.http.Field

class VersionRepository : BaseRepository() {
    private val service = RetrofitClient.createService(VersionService::class.java)

    fun checkVersion(lifecycleOwner: LifecycleOwner,appid : String,version : String) : Observable<VersionInfoBean>{
        return service.getVersionInfo(1,1,appid,version)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
    }

    fun appop(lifecycleOwner: LifecycleOwner,opType:Int,uid:Int,utemp:Int):Observable<String>{
        return service.updateAppop(opType, uid, utemp)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
    }

    fun appget(lifecycleOwner: LifecycleOwner,uid:Int,utemp:Int):Observable<AppUpdate>{
        return service.updateAppget(uid,utemp)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
    }
}