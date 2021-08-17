package com.aliee.quei.mo.ui.comic.vm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.*
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.repository.*
import com.aliee.quei.mo.data.service.CategoryService
import com.aliee.quei.mo.data.service.RecommendService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import kotlinx.coroutines.launch

class ComicDetailVModel : BaseViewModel(){
    private val comicRepository = ComicRepository()
    private val catalogRepository = CatalogRepository()
    private val shelfRepository = ShelfRepository()
    //private val recommendRunnable = RecommendRepository()
    //private val categoryRepository = CategoryRepository()
    private val historyRepository = HistoryRepository()

    private val categoryService = RetrofitClient.createService(CategoryService::class.java)
    private val recommendService = RetrofitClient.createService(RecommendService::class.java)

    val comicDetailLiveData = MediatorLiveData<UIDataBean<ComicBookBean>>()
    val catalogLiveData = MediatorLiveData<UIListDataBean<CatalogItemBean>>()
    val addShelfLiveData = MediatorLiveData<UIDataBean<Any>>()
    val isInShelfLiveData = MediatorLiveData<UIDataBean<Boolean>>()
    val guessLikeLiveData = MediatorLiveData<UIDataBean<MutableList<RecommendBookBean>>>()
    val sameCategoryLiveData = MediatorLiveData<UIListDataBean<ComicBookBean>>()
    val addHistoryLiveData = MediatorLiveData<UIDataBean<Any>>()

    fun getComicDetail(lifecycleOwner: LifecycleOwner,bookid : Int){
        comicRepository.getComicDetail(lifecycleOwner,bookid)
            .subscribe(StatusResourceObserver(comicDetailLiveData))
    }

    fun getComicDetailRand(lifecycleOwner: LifecycleOwner,bookid: Int,rid : String) {
        comicRepository.getRandComic(lifecycleOwner, bookid, rid)
            .subscribe(StatusResourceObserver(comicDetailLiveData))
    }

    fun getCatalog(lifecycleOwner: LifecycleOwner,bookid: Int,sort : Int = BeanConstants.SORT_DEFAULT) {
        catalogRepository.getCatalog(lifecycleOwner,bookid,1,10,sort)
            .subscribe(ListStatusResourceObserver(catalogLiveData))
    }

    fun addToShelf(bookid: Int) {
        viewModelScope.launch {
            addShelfLiveData.value = shelfRepository.addToShelf(bookid)
        }
    }

    fun getGuessLike(typename: String) {
        val categoryBean = CommonDataProvider.instance.categoryConfig?.find {
            it.typename == typename
        }
        if (categoryBean != null) {
            viewModelLaunch({
                val list = ListBean<ComicBookBean>()
                list.pageSize = 20
                list.page = 1
                list.list = categoryService.getList(categoryBean.id,BeanConstants.SEX_ALL,BeanConstants.STATUS_ALL,1,20).dataConvert()

                sameCategoryLiveData.value = UIListDataBean(Status.Success, list.list)
            },{
                sameCategoryLiveData.value = UIListDataBean(Status.Error, mutableListOf())
            })
        }
        viewModelLaunch ({
            val id = BeanConstants.RecommendPosition.GUESS_LIKE.rid
            guessLikeLiveData.value =  recommendService.getRecommendK(id).data?.getByRid(id)
        },{
            guessLikeLiveData.value = UIDataBean(Status.Error)
        })
    }

    fun isInShelf(lifecycleOwner: LifecycleOwner,bookid: Int) {
        comicRepository.isInShelf(lifecycleOwner, bookid)
            .subscribe(StatusResourceObserver(isInShelfLiveData))
    }

    fun addHistory(bookid: Int,chapterId: Int) {
        viewModelScope.launch {
            addHistoryLiveData.value=historyRepository.addHistory(bookid,chapterId)
        }
    }
}