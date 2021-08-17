package com.aliee.quei.mo.ui.user.vm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.data.bean.BaseResponse
import com.aliee.quei.mo.data.bean.UserInfoBean
import com.aliee.quei.mo.data.bean.toDataBean
import com.aliee.quei.mo.data.repository.UserInfoRepository
import com.aliee.quei.mo.data.service.UserService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import kotlinx.coroutines.launch

/**
 * Created by Administrator on 2018/4/20 0020.
 */
class UserInfoVModel : BaseViewModel(){
    private val userService = RetrofitClient.createService(UserService::class.java)
    //注意此為Video API
    private val userVideoService = RetrofitClient.createVideoService(UserService::class.java)

    val getUserInfoLiveData = MediatorLiveData<UIDataBean<UserInfoBean>>()
    val getMemberInfoLiveData = MediatorLiveData<UIDataBean<UserInfoBean>>()
    fun loadUseInfo(){
        viewModelLaunch ({
            getUserInfoLiveData.value=userService.getUserInfo().toDataBean()
        }, {})
    }

    fun getMemberInfo(){
        viewModelLaunch ({
            val b : BaseResponse<UserInfoBean> = userVideoService.getMemberInfo()
            val it = b.data
            CommonDataProvider.instance.saveFreeTime(it?.freetime.toString())
            getMemberInfoLiveData.value = b.toDataBean()
        }, {
            getMemberInfoLiveData.value = UIDataBean(Status.Error)
        })
    }

}