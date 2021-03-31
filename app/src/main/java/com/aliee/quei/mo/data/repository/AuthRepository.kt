package com.aliee.quei.mo.data.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.component.EventLoginSuccess
import com.aliee.quei.mo.data.bean.BaseResponse
import com.aliee.quei.mo.data.bean.UserRecoverBean
import com.aliee.quei.mo.data.service.AuthService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.RxBus
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable

class AuthRepository : BaseRepository(){
    private val service = RetrofitClient.createService(AuthService::class.java)

    fun register(lifecycleOwner: LifecycleOwner,phone : String,password : String) : Observable<Any> {
        return service.register(phone,password)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
    }

    fun login(lifecycleOwner: LifecycleOwner,phone : String,password: String) : Observable<Any>{
        return service.login(phone,password)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .map {
                if (it.code == 0) {
                    RxBus.getInstance().post(EventLoginSuccess())
                    CommonDataProvider.instance.setHasLogin(true)
                    it.data = 0
                }
                it
            }
            .compose(handleBean())
    }

    fun reset(lifecycleOwner: LifecycleOwner,phone : String,password_orig: String,password: String) : Observable<Any>{
        return service.reset(phone,password_orig,password)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
    }


    fun recoverUser(lifecycleOwner: LifecycleOwner,tradeNo : String) : Observable<UserRecoverBean> {
        return service.recoverUser(tradeNo)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
    }


    fun registerToken(lifecycleOwner: LifecycleOwner): Observable<Any> {
        return service.registerToken(ReaderApplication.jpushid)
            .compose(SchedulersUtil.applySchedulers())

    }
}