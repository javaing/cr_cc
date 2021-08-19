package com.aliee.quei.mo.data.repository

import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.bean.UserInfoBean
import com.aliee.quei.mo.data.bean.toDataBean
import com.aliee.quei.mo.data.service.UserService
import com.aliee.quei.mo.net.retrofit.RetrofitClient


/**
 * Created by Administrator on 2018/4/20 0020.
 */
class UserInfoRepository : BaseRepository() {
    private val service = RetrofitClient.createService(UserService::class.java)
    private val videoService = RetrofitClient.createVideoService(UserService::class.java)

    suspend fun videoMemberInfo():UIDataBean<UserInfoBean> {
        return try {
            videoService.getMemberInfo().toDataBean()
        } catch (e: Exception){
            UIDataBean(Status.Error)
        }
    }

    suspend fun getUserInfo() : UIDataBean<UserInfoBean>{
        return try {
            service.getUserInfo().toDataBean()
        } catch (e: Exception){
            UIDataBean(Status.Error)
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