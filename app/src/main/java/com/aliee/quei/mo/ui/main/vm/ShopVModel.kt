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
    private var launchRepository = LaunchRepository()
    //private var userInfoRepository = UserInfoRepository()
    private val recommendService = RetrofitClient.createService(RecommendService::class.java)
    private val userService = RetrofitClient.createService(UserService::class.java)

    val shopLiveData = MediatorLiveData<UIDataBean<MutableList<RecommendListBean>>>()
    val moreLiveData = MediatorLiveData<UIDataBean<ListBean<RecommendBookBean>>>()
    val comicDetailLiveData = MediatorLiveData<UIDataBean<ComicBookBean>>()
    val historyChapterLiveData = MediatorLiveData<UIDataBean<HistoryLastBean>>()
    val appupdateopLiveData = MediatorLiveData<UIDataBean<String>>()
    val appDrainageLiveData = MediatorLiveData<UIDataBean<String>>()
    val shareLinkLiveData = MediatorLiveData<UIDataBean<String>>()

    fun appDrainage(uid: Int, utemp: Int) {
        viewModelLaunch ({
            appDrainageLiveData.value = recommendService.appDrainage(uid, utemp).toDataBean()
        },{
            appDrainageLiveData.value = UIDataBean(Status.Error)
        })
    }

    fun getHistoryChapter(lifecycleOwner: LifecycleOwner, bookid: Int) {
        comicRepository.getHistoryChapter(lifecycleOwner, bookid)
                .subscribe(StatusResourceObserver(historyChapterLiveData))
    }

    fun loadShop(lifecycleOwner: LifecycleOwner) {
        repository.getRecommendBatch(
                lifecycleOwner, "${BeanConstants.RecommendPosition.BANNER.rid}," + //banner 17
                "${BeanConstants.RecommendPosition.HOT_RECOMMEND.rid}," + //推荐 //18
                "${BeanConstants.RecommendPosition.LATELY_UPDATE.rid}," + //更新 //19
                "${BeanConstants.RecommendPosition.WEEK_POPULAR.rid}," + //本周人气 //20
                "${BeanConstants.RecommendPosition.FREE.rid}," + //免费 //21
                BeanConstants.RecommendPosition.WEEK_TOP10.rid
        )  //猜你喜欢 // 45
                .subscribe(StatusResourceObserver(shopLiveData))
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

    private var imgRetryTime = 0
    @SuppressLint("CheckResult")
    fun retryImgDomain(lifecycleOwner: LifecycleOwner) {
        launchRepository.getImgDomain(lifecycleOwner).subscribe({}, {
            it.printStackTrace()
            imgRetryTime++
            if (imgRetryTime < 5) {
                retryImgDomain(lifecycleOwner)
            }
        })
    }

    /**
     * 重试
     */
    private var retryTime = 0

    @SuppressLint("CheckResult")
    fun retryInitData(lifecycleOwner: LifecycleOwner) {
        viewModelLaunch ({
            CommonDataProvider.instance.saveUserInfo( userService.getUserInfo().dataConvert() )
        }, {})

        val preTasks = mutableListOf<Observable<*>>()
        preTasks.add(launchRepository.getImgDomain(lifecycleOwner))
        preTasks.add(launchRepository.registerToken(lifecycleOwner))
        //preTasks.add(userInfoRepository.getUserInfo())
        Observable.concat(preTasks)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .subscribe({
                }, {
                    it.printStackTrace()
                    retryTime++
                    if (retryTime < 5) {
                        retryInitData(lifecycleOwner)
                    }
                })
    }
}