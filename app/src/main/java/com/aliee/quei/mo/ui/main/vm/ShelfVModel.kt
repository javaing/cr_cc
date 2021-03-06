package com.aliee.quei.mo.ui.main.vm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.ListStatusResourceObserver
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.base.response.UIListDataBean
import com.aliee.quei.mo.data.bean.ListBean
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.data.bean.ShelfBean
import com.aliee.quei.mo.data.repository.RecommendRepository
import com.aliee.quei.mo.data.repository.ShelfRepository
import kotlinx.coroutines.launch

/**
 * Created by Administrator on 2018/4/26 0026.
 */
class ShelfVModel : BaseViewModel(){
    private val shelfRepository = ShelfRepository()
    private val recommendRepository = RecommendRepository()

    val recommendLiveData = MediatorLiveData<UIDataBean<ListBean<RecommendBookBean>>>()

    val delFromShelfLiveData = MediatorLiveData<UIDataBean<Any>>()
    val getShelfListLiveData = MediatorLiveData<UIListDataBean<ShelfBean>>()
    private var page = 1
    private var pageSize = 100

    fun loadList(lifecycleOwner: LifecycleOwner){
        page = 1
        shelfRepository.getList(lifecycleOwner,page,pageSize)
                .subscribe(ListStatusResourceObserver(getShelfListLiveData))
    }

    fun loadMore(lifecycleOwner: LifecycleOwner){
        page++
        shelfRepository.getList(lifecycleOwner,page,pageSize)
                .subscribe(ListStatusResourceObserver(getShelfListLiveData))
    }

    fun delFromShelf(bookid : Int){
        viewModelScope.launch {
            delFromShelfLiveData.value = shelfRepository.addToShelf(bookid)
        }
    }


    private var rPage = 1
    private val rPageSize = 10
    fun loadRecommend() {
        viewModelScope.launch {
            rPage ++
            recommendLiveData.value = recommendRepository.getListByConversionRate(rPage,rPageSize)
        }
    }
}