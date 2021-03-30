package com.aliee.quei.mo.data.repository

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.component.EventUserInfoUpdated
import com.aliee.quei.mo.data.bean.UserInfoBean
import com.aliee.quei.mo.data.service.UserService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.RxBus
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable


/**
 * Created by Administrator on 2018/4/20 0020.
 */
class UserInfoRepository : BaseRepository() {
    private val service = RetrofitClient.createService(UserService::class.java)

    fun getUserInfo(lifecycleOwner: LifecycleOwner): Observable<UserInfoBean> {
        return service.getUserInfo()
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    CommonDataProvider.instance.saveUserInfo(it)
                    it
                }
    }

    fun getMemberInfo(lifecycleOwner: LifecycleOwner): Observable<UserInfoBean> {
        return service.getMemberInfo()
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    CommonDataProvider.instance.saveFreeTime(it.freetime.toString())
                    it
                }
    }


    /* fun getUserConfig(lifecycleOwner: LifecycleOwner) : Observable<UserConfigBean>{
        return service.getUserConfig(createRequestBody(mutableMapOf<String,String>()))
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
                .compose(handleBean(UserConfigBean::class.java))
                .map {
                    SharedPreUtils.getInstance().dailyShareCount = it.shareDisplay
                    it
                }
    }*/

    fun dailySign(lifecycleOwner: LifecycleOwner): Observable<Any> {
        return service.dailySign()
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .map {
                    if (it.code == 0) {
                        RxBus.getInstance().post(EventUserInfoUpdated())
                    }
                    it
                }
                .compose(handleBean())
    }
}