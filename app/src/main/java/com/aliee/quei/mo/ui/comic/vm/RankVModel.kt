package com.aliee.quei.mo.ui.comic.vm

import androidx.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.data.bean.getByRid
import com.aliee.quei.mo.data.service.RecommendService
import com.aliee.quei.mo.net.retrofit.RetrofitClient

class RankVModel : BaseViewModel(){
    private val recommendService = RetrofitClient.createService(RecommendService::class.java)
    val listLiveData = MediatorLiveData<UIDataBean<MutableList<RecommendBookBean>>>()

    /**
     * 6	|	[int]	|		|	周排行
     * 7	|	[int]	|		|	月排行
     * 8	|	[int]	|		|	总排行
     */
    fun loadRank(rid : String) {
        listLiveData.value = UIDataBean(Status.Start)
        viewModelLaunch ({
            val bean =  recommendService.getRecommendK(rid).data?.getByRid(rid)
            if (bean?.data?.size!! >0) {
                listLiveData.value = UIDataBean(Status.Success, bean.data)
            } else {
                listLiveData.value = UIDataBean(Status.Empty)
            }
            listLiveData.value = UIDataBean(Status.Complete)
        },{
            listLiveData.value = UIDataBean(Status.Error)
        })
    }
}