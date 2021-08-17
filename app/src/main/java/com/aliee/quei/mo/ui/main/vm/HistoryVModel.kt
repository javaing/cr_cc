package com.aliee.quei.mo.ui.main.vm

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.bean.HistoryBean
import com.aliee.quei.mo.data.bean.ListBean
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.data.repository.HistoryRepository
import com.aliee.quei.mo.data.repository.RecommendRepository
import com.aliee.quei.mo.data.service.HistoryService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import kotlinx.coroutines.launch


/**
 * Created by Administrator on 2018/4/26 0026.
 */
class HistoryVModel : BaseViewModel(){
    private val historyRepository = HistoryRepository()
    private val recommendRepository = RecommendRepository()

    val recommendLiveData = MediatorLiveData<UIDataBean<ListBean<RecommendBookBean>>>()
    val loadHistoryLiveData = MediatorLiveData<UIDataBean<List<HistoryBean>>>()
    val delHistoryLiveData = MediatorLiveData<UIDataBean<String?>>()

    fun loadHistory() {
        viewModelScope.launch {
            loadHistoryLiveData.value = historyRepository.loadHistory()
        }
    }

    fun deleteHistory(bookid: Int) {
        viewModelScope.launch {
            delHistoryLiveData.value = historyRepository.delRecord(bookid)
        }
    }

    private var page = 1
    private val pageSize = 10
    fun loadRecommend() {
        viewModelScope.launch {
            page ++
            recommendLiveData.value = recommendRepository.getListByConversionRate(page,pageSize)
        }
    }

}