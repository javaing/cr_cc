package com.aliee.quei.mo.ui.main.vm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.repository.*


class BulletinVModel : BaseViewModel() {

    private val checkInRepository = CheckInRepository()

    val bulletinFullLiveData = MediatorLiveData<UIDataBean<BaseResponse<Object>>>()
    val bulletinDetailLiveData = MediatorLiveData<UIDataBean<BulletinDetailBean>>()

    private var page = 1
    private var status = 2
    private val pageSize = 2

    fun getBulletin(lifecycleOwner: LifecycleOwner) {
        checkInRepository.getBulletinList(lifecycleOwner,page,status,pageSize)
                .subscribe(StatusResourceObserver(bulletinFullLiveData,silent = true))
    }

    fun getBulletinDetail(lifecycleOwner: LifecycleOwner,bulletinId:Int) {
        checkInRepository.getBulletinDetail(lifecycleOwner,bulletinId)
                .subscribe(StatusResourceObserver(bulletinDetailLiveData,silent = true))
    }



}