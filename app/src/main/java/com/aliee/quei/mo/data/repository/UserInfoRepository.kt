package com.aliee.quei.mo.data.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.component.EventUserInfoUpdated
import com.aliee.quei.mo.data.bean.UserInfoBean
import com.aliee.quei.mo.data.bean.toDataBean
import com.aliee.quei.mo.data.service.UserService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.RxBus
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable
import java.lang.Exception


/**
 * Created by Administrator on 2018/4/20 0020.
 */
class UserInfoRepository : BaseRepository() {
    private val service = RetrofitClient.createService(UserService::class.java)
    private val videoService = RetrofitClient.createVideoService(UserService::class.java)

    suspend fun videoMemberInfo():UserInfoBean? {
        return try {
            videoService.getMemberInfo().data
        } catch (e: Exception){
             null
        }
    }

    suspend fun getUserInfo() : UserInfoBean?{
        return try {
            service.getUserInfo().data
        } catch (e: Exception){
            null
        }
    }

//    fun dailySign(lifecycleOwner: LifecycleOwner): Observable<Any> {
//        return service.dailySign()
//                .compose(SchedulersUtil.applySchedulers())
//                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
//                .map {
//                    if (it.code == 0) {
//                        RxBus.getInstance().post(EventUserInfoUpdated())
//                    }
//                    it
//                }
//                .compose(handleBean())
//    }
}