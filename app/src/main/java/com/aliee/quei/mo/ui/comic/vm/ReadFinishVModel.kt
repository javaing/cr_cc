package com.aliee.quei.mo.ui.comic.vm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.data.repository.RecommendRepository

class ReadFinishVModel : BaseViewModel(){
    private val repository = RecommendRepository()

    val guessLikeLiveData = MediatorLiveData<UIDataBean<List<RecommendBookBean>>>()

    fun getGuessLike(lifecycleOwner: LifecycleOwner){
        repository.getRecommend(lifecycleOwner, BeanConstants.RecommendPosition.GUESS_LIKE.rid)
            .subscribe(StatusResourceObserver(guessLikeLiveData))
    }
}