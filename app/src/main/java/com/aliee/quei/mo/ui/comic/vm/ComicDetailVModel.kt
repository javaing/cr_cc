package com.aliee.quei.mo.ui.comic.vm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.*
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.repository.*
import com.aliee.quei.mo.data.service.CategoryService
import com.aliee.quei.mo.data.service.RecommendService
import com.aliee.quei.mo.net.retrofit.RetrofitClient

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

    fun addToShelf(lifecycleOwner: LifecycleOwner,bookid: Int) {
        shelfRepository.addToShelf(lifecycleOwner,bookid)
            .subscribe(StatusResourceObserver(addShelfLiveData))
    }

    fun getGuessLike(lifecycleOwner: LifecycleOwner, bookid: Int, typename: String) {
        val categoryBean = CommonDataProvider.instance.categoryConfig?.find {
            it.typename == typename
        }
        if (categoryBean != null) {
//            categoryRepository.getList(lifecycleOwner,categoryBean.id,BeanConstants.SEX_ALL,BeanConstants.STATUS_ALL,1,20)
//                .subscribe(ListStatusResourceObserver(sameCategoryLiveData))
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
//        recommendRunnable.getRecommend(lifecycleOwner, BeanConstants.RecommendPosition.GUESS_LIKE.rid)
//            .subscribe(StatusResourceObserver(guessLikeLiveData))
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

    fun addHistory(lifecycleOwner: LifecycleOwner,bookid: Int,chapterId: Int) {
        historyRepository.addHistory(lifecycleOwner,bookid,chapterId)
            .subscribe(StatusResourceObserver(addHistoryLiveData))
    }
}