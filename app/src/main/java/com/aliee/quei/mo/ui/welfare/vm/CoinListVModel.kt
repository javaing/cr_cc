package com.aliee.quei.mo.ui.welfare.vm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.ListStatusResourceObserver
import com.aliee.quei.mo.base.response.UIListDataBean
import com.aliee.quei.mo.data.bean.WelfareCoinRecordBean
import com.aliee.quei.mo.data.repository.WelfareRepository

class CoinListVModel : BaseViewModel() {
    private val welfareRepository = WelfareRepository()

    val listLiveData = MediatorLiveData<UIListDataBean<WelfareCoinRecordBean>>()

    private var page = 1;
    private var pageSize = 20;
    fun loadList(lifecycleOwner: LifecycleOwner)  {
        page = 1
        welfareRepository.getCoinRecords(lifecycleOwner,page, pageSize)
            .subscribe(ListStatusResourceObserver(listLiveData))
    }

    fun loadMore(lifecycleOwner: LifecycleOwner) {
        page ++
        welfareRepository.getCoinRecords(lifecycleOwner,page, pageSize)
            .subscribe(ListStatusResourceObserver(listLiveData))
    }
}