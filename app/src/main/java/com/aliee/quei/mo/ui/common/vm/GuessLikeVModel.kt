package com.aliee.quei.mo.ui.common.vm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.ListStatusResourceObserver
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.base.response.UIListDataBean
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.ComicBookBean
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.data.repository.CategoryRepository
import com.aliee.quei.mo.data.repository.RecommendRepository

class GuessLikeVModel : BaseViewModel() {
    private val recommendRunnable = RecommendRepository()
    private val categoryRepository = CategoryRepository()
    val guessLikeLiveData = MediatorLiveData<UIDataBean<List<RecommendBookBean>>>()
    val sameCategoryLiveData = MediatorLiveData<UIListDataBean<ComicBookBean>>()
    val hotRecommendLiveData = MediatorLiveData<UIDataBean<List<RecommendBookBean>>>()

    fun getGuessLike(
        lifecycleOwner: LifecycleOwner,
        bookid: Int,
        typename: String = "") {
        val categoryBean = CommonDataProvider.instance.categoryConfig?.find {
            it.typename == typename
        }
        if (categoryBean != null) {
            categoryRepository.getList(lifecycleOwner,categoryBean.id,
                BeanConstants.SEX_ALL,
                BeanConstants.STATUS_ALL,1,20)
                .subscribe(ListStatusResourceObserver(sameCategoryLiveData))
        }
        recommendRunnable.getRecommend(lifecycleOwner, BeanConstants.RecommendPosition.GUESS_LIKE.rid)
            .subscribe(StatusResourceObserver(guessLikeLiveData))
    }

    fun getHotRecommend(
        lifecycleOwner: LifecycleOwner,
        bookid: Int,
        typename: String = "") {
        val categoryBean = CommonDataProvider.instance.categoryConfig?.find {
            it.typename == typename
        }
        if (categoryBean != null) {
            categoryRepository.getList(lifecycleOwner,categoryBean.id,
                BeanConstants.SEX_ALL,
                BeanConstants.STATUS_ALL,1,20)
                .subscribe(ListStatusResourceObserver(sameCategoryLiveData))
        }
        recommendRunnable.getRecommend(lifecycleOwner, BeanConstants.RecommendPosition.HOT_RECOMMEND.rid)
            .subscribe(StatusResourceObserver(hotRecommendLiveData))
    }
}