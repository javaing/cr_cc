package com.aliee.quei.mo.ui.search.vm

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.data.repository.RecommendRepository

class SearchRecommendVModel : BaseViewModel(){
    private val repository = RecommendRepository()

    private val rids = BeanConstants.RecommendPosition.values().map {
        it.rid
    }
    private var recIndex = 0

    val recommendLiveData = MediatorLiveData<UIDataBean<MutableList<RecommendBookBean>>>()
    fun loadRecommend(lifecycleOwner: LifecycleOwner) {
        recIndex = 0
        repository.getRecommend(lifecycleOwner, rids[recIndex])
            .subscribe(StatusResourceObserver(recommendLiveData))
    }

    fun shiftRecommend(lifecycleOwner: LifecycleOwner) {
        recIndex ++
        if (recIndex > rids.size - 1)recIndex = 0
        repository.getRecommend(lifecycleOwner, rids[recIndex])
            .subscribe(StatusResourceObserver(recommendLiveData))
    }
}