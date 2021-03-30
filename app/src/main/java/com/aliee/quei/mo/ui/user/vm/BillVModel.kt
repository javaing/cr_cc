package com.aliee.quei.mo.ui.user.vm

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.ListStatusResourceObserver
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.base.response.UIListDataBean
import com.aliee.quei.mo.data.bean.BillRecordBean
import com.aliee.quei.mo.data.bean.BonusIDBean
import com.aliee.quei.mo.data.bean.BonusRecordBean
import com.aliee.quei.mo.data.repository.BillRepository

class BillVModel : BaseViewModel(){
    private val billRepository = BillRepository()

    val billListLiveData = MediatorLiveData<UIListDataBean<BillRecordBean>>()
    val bonusListLiveData = MediatorLiveData<UIDataBean<List<BonusRecordBean>>>()
    val bonusIDLiveData = MediatorLiveData<UIDataBean<BonusIDBean>>()
    val exchangeLiveData = MediatorLiveData<UIDataBean<Any>>()

    private var page = 1
    private val pageSize = 20

    fun getList(lifecycleOwner: LifecycleOwner){
        page = 1
        billRepository.getBillList(lifecycleOwner,page, pageSize)
            .subscribe(ListStatusResourceObserver(billListLiveData))
    }

    fun loadMore(lifecycleOwner: LifecycleOwner) {
        page ++
        billRepository.getBillList(lifecycleOwner,page, pageSize)
            .subscribe(ListStatusResourceObserver(billListLiveData))
    }

    fun getBonusId(lifecycleOwner: LifecycleOwner,recommendcode : String){
        billRepository.getBonusID(lifecycleOwner,recommendcode)
                .subscribe(StatusResourceObserver(bonusIDLiveData))
    }

    fun getBonusList(lifecycleOwner: LifecycleOwner){
        page = 1
        billRepository.getBonusList(lifecycleOwner)
                .subscribe(StatusResourceObserver(bonusListLiveData))
    }

    fun sendExange(lifecycleOwner: LifecycleOwner,exchangeCode: String){
        billRepository.sendExange(lifecycleOwner,exchangeCode)
                .subscribe(StatusResourceObserver(exchangeLiveData))
    }
}