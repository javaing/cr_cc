package com.aliee.quei.mo.ui.comic.vm

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

class ReadFinishVModel : BaseViewModel(){
    private val recommendService = RetrofitClient.createService(RecommendService::class.java)

    val guessLikeLiveData = MediatorLiveData<UIDataBean<MutableList<RecommendBookBean>>>()

    fun getGuessLike(){
        viewModelLaunch ({
            val id = BeanConstants.RecommendPosition.GUESS_LIKE.rid
            guessLikeLiveData.value =  recommendService.getRecommendK(id).data?.getByRid(id)
        },{
            guessLikeLiveData.value = UIDataBean(Status.Error)
        })
    }
}