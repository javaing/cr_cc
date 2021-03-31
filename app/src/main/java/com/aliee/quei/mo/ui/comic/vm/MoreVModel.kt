package com.aliee.quei.mo.ui.comic.vm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.bean.ListBean
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.data.repository.RecommendRepository

class  MoreVModel : BaseViewModel(){
    private val repository = RecommendRepository()
    val listLiveData = MediatorLiveData<UIDataBean<List<RecommendBookBean>>>()
    val comicListLiveData = MediatorLiveData<UIDataBean<ListBean<RecommendBookBean>>>()

    var page = 0
    fun getByRid(lifecycleOwner: LifecycleOwner,rid : String) {
        page = 0
        repository.getRecommend(lifecycleOwner, rid)
            .subscribe(StatusResourceObserver(listLiveData))
    }



    fun loadMore(lifecycleOwner: LifecycleOwner) {
        page ++
        repository.getListByConversionRate(lifecycleOwner,page,10)
            .subscribe(StatusResourceObserver(comicListLiveData,silent = true))
    }
}