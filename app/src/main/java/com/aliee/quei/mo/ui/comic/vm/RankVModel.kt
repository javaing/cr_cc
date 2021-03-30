package com.aliee.quei.mo.ui.comic.vm

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.data.repository.RecommendRepository

class RankVModel : BaseViewModel(){
    private val repository = RecommendRepository()
    val listLiveData = MediatorLiveData<UIDataBean<MutableList<RecommendBookBean>>>()

    /**
     * 6	|	[int]	|		|	周排行
     * 7	|	[int]	|		|	月排行
     * 8	|	[int]	|		|	总排行
     */
    fun loadRank(lifecycleOwner: LifecycleOwner, rid : String) {
        repository.getRecommend(lifecycleOwner, rid)
            .subscribe(StatusResourceObserver(listLiveData))
    }
}