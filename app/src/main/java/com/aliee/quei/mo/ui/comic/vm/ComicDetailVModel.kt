package com.aliee.quei.mo.ui.comic.vm

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.ListStatusResourceObserver
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.base.response.UIListDataBean
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.CatalogItemBean
import com.aliee.quei.mo.data.bean.ComicBookBean
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.data.repository.*

class ComicDetailVModel : BaseViewModel(){
    private val comicRepository = ComicRepository()
    private val catalogRepository = CatalogRepository()
    private val shelfRepository = ShelfRepository()
    private val recommendRunnable = RecommendRepository()
    private val categoryRepository = CategoryRepository()
    private val historyRepository = HistoryRepository()

    val comicDetailLiveData = MediatorLiveData<UIDataBean<ComicBookBean>>()
    val catalogLiveData = MediatorLiveData<UIListDataBean<CatalogItemBean>>()
    val addShelfLiveData = MediatorLiveData<UIDataBean<Any>>()
    val isInShelfLiveData = MediatorLiveData<UIDataBean<Boolean>>()
    val guessLikeLiveData = MediatorLiveData<UIDataBean<List<RecommendBookBean>>>()
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
            categoryRepository.getList(lifecycleOwner,categoryBean.id,BeanConstants.SEX_ALL,BeanConstants.STATUS_ALL,1,20)
                .subscribe(ListStatusResourceObserver(sameCategoryLiveData))
        }
        recommendRunnable.getRecommend(lifecycleOwner, BeanConstants.RecommendPosition.GUESS_LIKE.rid)
            .subscribe(StatusResourceObserver(guessLikeLiveData))
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