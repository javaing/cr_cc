package com.aliee.quei.mo.ui.search.vm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.bean.ComicBookBean
import com.aliee.quei.mo.data.repository.ComicRepository

/**
 * Created by Administrator on 2018/4/28 0028.
 */
class SearchVModel : BaseViewModel(){
    private val repository = ComicRepository()

    val searchListLiveData = MediatorLiveData<UIDataBean<List<ComicBookBean>>>()

    private var page = 1
    private var pageSize = 10
    private var key : String = ""
    fun search(lifecycleOwner: LifecycleOwner,text : String){
        key = text
        page = 1
        repository.searchByKey(lifecycleOwner, text)
                .subscribe(StatusResourceObserver(searchListLiveData))
    }

    fun loadMore(lifecycleOwner: LifecycleOwner){
        page++
        repository.searchByKey(lifecycleOwner, key)
                .subscribe(StatusResourceObserver(searchListLiveData))
    }
}