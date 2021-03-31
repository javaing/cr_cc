package com.aliee.quei.mo.ui.main.vm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.bean.HistoryBean
import com.aliee.quei.mo.data.bean.ListBean
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.data.repository.HistoryRepository
import com.aliee.quei.mo.data.repository.RecommendRepository


/**
 * Created by Administrator on 2018/4/26 0026.
 */
class HistoryVModel : BaseViewModel(){
    private val historyRepository = HistoryRepository()
    private val recommendRepository = RecommendRepository()

    val recommendLiveData = MediatorLiveData<UIDataBean<ListBean<RecommendBookBean>>>()
    val loadHistoryLiveData = MediatorLiveData<UIDataBean<List<HistoryBean>>>()
    val delHistoryLiveData = MediatorLiveData<UIDataBean<String?>>()
    fun loadHistory(lifecycleOwner: LifecycleOwner){
        historyRepository.loadHistory(lifecycleOwner)
                .subscribe(StatusResourceObserver(loadHistoryLiveData))
    }

    fun deleteHistory(lifecycleOwner: LifecycleOwner,bookid: Int) {
        historyRepository.delRecord(lifecycleOwner,bookid)
                .subscribe(StatusResourceObserver(delHistoryLiveData))
    }

    private var page = 1
    private val pageSize = 10
    fun loadRecommend(lifecycleOwner: LifecycleOwner) {
        page ++
        recommendRepository.getListByConversionRate(lifecycleOwner,page,pageSize)
            .subscribe(StatusResourceObserver(recommendLiveData,silent = true))
    }

}