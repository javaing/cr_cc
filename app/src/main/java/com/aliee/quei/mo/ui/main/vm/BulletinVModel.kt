package com.aliee.quei.mo.ui.main.vm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.bean.BaseResponse
import com.aliee.quei.mo.data.bean.BulletinDetailBean
import com.aliee.quei.mo.data.repository.CheckInRepository
import kotlinx.coroutines.launch


class BulletinVModel : BaseViewModel() {

    private val checkInRepository = CheckInRepository()

    val bulletinFullLiveData = MediatorLiveData<UIDataBean<BaseResponse<Any>>>()
    val bulletinDetailLiveData = MediatorLiveData<UIDataBean<BulletinDetailBean>>()

    private var page = 1
    private var status = 2
    private val pageSize = 2

    fun getBulletin() {
        viewModelScope.launch {
            bulletinFullLiveData.value = checkInRepository.getBulletinList(page,status,pageSize) }
    }

    fun getBulletinDetail(bulletinId:Int) {
        viewModelScope.launch {
            bulletinDetailLiveData.value = checkInRepository.getBulletinDetail(bulletinId)
        }
    }

}