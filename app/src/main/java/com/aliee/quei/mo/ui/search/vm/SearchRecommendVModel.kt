package com.aliee.quei.mo.ui.search.vm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.data.bean.getByRid
import com.aliee.quei.mo.data.repository.RecommendRepository
import com.aliee.quei.mo.data.service.RecommendService
import com.aliee.quei.mo.net.retrofit.RetrofitClient

class SearchRecommendVModel : BaseViewModel(){
    private val recommendService = RetrofitClient.createService(RecommendService::class.java)

    private val rids = BeanConstants.RecommendPosition.values().map {
        it.rid
    }
    private var recIndex = 0

    val recommendLiveData = MediatorLiveData<UIDataBean<MutableList<RecommendBookBean>>>()
    fun loadRecommend() {
        recIndex = 0
        viewModelLaunch ({
            val id = rids[recIndex]
            recommendLiveData.value =  recommendService.getRecommendK(id).data?.getByRid(id)
        },{
            recommendLiveData.value = UIDataBean(Status.Error)
        })
    }

    fun shiftRecommend() {
        recIndex ++
        if (recIndex > rids.size - 1)recIndex = 0
        viewModelLaunch ({
            val id = rids[recIndex]
            recommendLiveData.value =  recommendService.getRecommendK(id).data?.getByRid(id)
        },{
            recommendLiveData.value = UIDataBean(Status.Error)
        })
    }
}