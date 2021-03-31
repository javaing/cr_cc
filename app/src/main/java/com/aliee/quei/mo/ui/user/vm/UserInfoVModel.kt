package com.aliee.quei.mo.ui.user.vm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.bean.UserInfoBean
import com.aliee.quei.mo.data.repository.UserInfoRepository

/**
 * Created by Administrator on 2018/4/20 0020.
 */
class UserInfoVModel : BaseViewModel(){
    private val userInfoRepository = UserInfoRepository()

    val getUserInfoLiveData = MediatorLiveData<UIDataBean<UserInfoBean>>()
    val getMemberInfoLiveData = MediatorLiveData<UIDataBean<UserInfoBean>>()
    fun loadUseInfo(lifecycleOwner: LifecycleOwner){
        userInfoRepository.getUserInfo(lifecycleOwner)
                .subscribe(StatusResourceObserver(getUserInfoLiveData))
    }

    fun getMemberInfo(lifecycleOwner: LifecycleOwner){
        userInfoRepository.getMemberInfo(lifecycleOwner)
                .subscribe(StatusResourceObserver(getMemberInfoLiveData))
    }

//    fun dailySign(lifecycleOwner: LifecycleOwner) {
//        userInfoRepository.dailySign(lifecycleOwner)
//            .map {
//
//                it
//            }
//            .subscribe(StatusResourceObserver(dailySignLiveData))
//    }

}