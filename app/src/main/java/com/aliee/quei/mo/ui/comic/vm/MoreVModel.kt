package com.aliee.quei.mo.ui.comic.vm

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.bean.ListBean
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.data.bean.getByRid
import com.aliee.quei.mo.data.repository.RecommendRepository
import com.aliee.quei.mo.data.service.RecommendService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import kotlinx.coroutines.launch

class  MoreVModel : BaseViewModel(){
    private val repository = RecommendRepository()
    private val recommendService = RetrofitClient.createService(RecommendService::class.java)
    val listLiveData = MediatorLiveData<UIDataBean<MutableList<RecommendBookBean>>>()
    val comicListLiveData = MediatorLiveData<UIDataBean<ListBean<RecommendBookBean>>>()

    var page = 0
    fun getByRid(rid : String) {
        page = 0
        viewModelLaunch ({
            listLiveData.value =  recommendService.getRecommendK(rid).data?.getByRid(rid)
        },{
            listLiveData.value = UIDataBean(Status.Error)
        })
    }



    fun loadMore() {
        viewModelScope.launch {
            page ++
            comicListLiveData.value = repository.getListByConversionRate(page,10)
        }
    }
}