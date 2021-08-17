package com.aliee.quei.mo.ui.common.vm

import androidx.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.*
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.service.CategoryService
import com.aliee.quei.mo.data.service.RecommendService
import com.aliee.quei.mo.net.retrofit.RetrofitClient

class GuessLikeVModel : BaseViewModel() {
    private val recommendService = RetrofitClient.createService(RecommendService::class.java)
    private val categoryService = RetrofitClient.createService(CategoryService::class.java)

    val guessLikeLiveData = MediatorLiveData<UIDataBean<MutableList<RecommendBookBean>>>()
    val sameCategoryLiveData = MediatorLiveData<UIListDataBean<ComicBookBean>>()


    fun getGuessLike(typename: String = "") {
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
//        recommendRunnable.getRecommend(lifecycleOwner, BeanConstants.RecommendPosition.GUESS_LIKE.rid)
//            .subscribe(StatusResourceObserver(guessLikeLiveData))
        viewModelLaunch ({
            val id = BeanConstants.RecommendPosition.GUESS_LIKE.rid
            guessLikeLiveData.value =  recommendService.getRecommendK(id).data?.getByRid(id)
        },{
            guessLikeLiveData.value = UIDataBean(Status.Error)
        })
    }
}