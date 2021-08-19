package com.aliee.quei.mo.ui.main.vm

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.repository.ComicRepository
import com.aliee.quei.mo.data.repository.LaunchRepository
import com.aliee.quei.mo.data.repository.RecommendRepository
import com.aliee.quei.mo.data.repository.UserInfoRepository
import com.aliee.quei.mo.data.service.RecommendService
import com.aliee.quei.mo.data.service.UserService
import com.aliee.quei.mo.net.ApiConstants
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.SharedPreUtils
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable
import kotlinx.coroutines.launch

/**
 * Created by Administrator on 2018/4/18 0018.
 */
class ShopVModel : BaseViewModel() {
    private val repository = RecommendRepository()
    private val comicRepository = ComicRepository()
    private val recommendRepository = RecommendRepository()

    val shopLiveData = MediatorLiveData<UIDataBean<MutableList<RecommendListBean>>>()
    val moreLiveData = MediatorLiveData<UIDataBean<ListBean<RecommendBookBean>>>()
    val comicDetailLiveData = MediatorLiveData<UIDataBean<ComicBookBean>>()
    val historyChapterLiveData = MediatorLiveData<UIDataBean<HistoryLastBean>>()
    val appupdateopLiveData = MediatorLiveData<UIDataBean<String>>()
    val appDrainageLiveData = MediatorLiveData<UIDataBean<String>>()
    val shareLinkLiveData = MediatorLiveData<UIDataBean<String>>()

    fun appDrainage(uid: Int, utemp: Int) {
        viewModelScope.launch {
            appDrainageLiveData.value = recommendRepository.appDrainage(uid, utemp)
        }
    }

    fun getHistoryChapter(lifecycleOwner: LifecycleOwner, bookid: Int) {
        comicRepository.getHistoryChapter(lifecycleOwner, bookid)
                .subscribe(StatusResourceObserver(historyChapterLiveData))
    }

    fun loadShop() {
        viewModelScope.launch {
            shopLiveData.value = repository.getRecommendBatch("${BeanConstants.RecommendPosition.BANNER.rid}," + //banner 17
                        "${BeanConstants.RecommendPosition.HOT_RECOMMEND.rid}," + //推荐 //18
                        "${BeanConstants.RecommendPosition.LATELY_UPDATE.rid}," + //更新 //19
                        "${BeanConstants.RecommendPosition.WEEK_POPULAR.rid}," + //本周人气 //20
                        "${BeanConstants.RecommendPosition.FREE.rid}," + //免费 //21
                        BeanConstants.RecommendPosition.WEEK_TOP10.rid
            )  //猜你喜欢 // 45
        }
    }

    var rPage = 0
    private var rPageSize = 20
    fun loadMore() {
        viewModelScope.launch {
            rPage++
            if (rPage >= 4) {
                rPage = 1
            }
            moreLiveData.value = repository.getListByConversionRate(rPage, rPageSize)
        }
    }

}