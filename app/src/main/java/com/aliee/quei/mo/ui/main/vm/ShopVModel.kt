package com.aliee.quei.mo.ui.main.vm

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.repository.ComicRepository
import com.aliee.quei.mo.data.repository.LaunchRepository
import com.aliee.quei.mo.data.repository.RecommendRepository
import com.aliee.quei.mo.data.repository.UserInfoRepository
import com.aliee.quei.mo.net.ApiConstants
import com.aliee.quei.mo.utils.SharedPreUtils
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable

/**
 * Created by Administrator on 2018/4/18 0018.
 */
class ShopVModel : BaseViewModel() {
    private val repository = RecommendRepository()
    private val comicRepository = ComicRepository()
    private var launchRepository = LaunchRepository()
    private var userInfoRepository = UserInfoRepository()

    val shopLiveData = MediatorLiveData<UIDataBean<MutableList<RecommendListBean>>>()
    val moreLiveData = MediatorLiveData<UIDataBean<ListBean<RecommendBookBean>>>()
    val comicDetailLiveData = MediatorLiveData<UIDataBean<ComicBookBean>>()
    val historyChapterLiveData = MediatorLiveData<UIDataBean<HistoryLastBean>>()
    val appupdateopLiveData = MediatorLiveData<UIDataBean<String>>()
    val appDrainageLiveData = MediatorLiveData<UIDataBean<String>>()
    val shareLinkLiveData = MediatorLiveData<UIDataBean<String>>()

    fun appUpdateOp(lifecycleOwner: LifecycleOwner, uid: Int, utemp: Int, opType: Int) {
        repository.appUpdateOp(lifecycleOwner, uid, utemp, opType).subscribe(StatusResourceObserver(appupdateopLiveData))
    }

    fun appDrainage(lifecycleOwner: LifecycleOwner, uid: Int, utemp: Int) {
        repository.appDrainage(lifecycleOwner, uid, utemp).subscribe(StatusResourceObserver(appDrainageLiveData))
    }

    fun getComicDetailRand(lifecycleOwner: LifecycleOwner, bookid: Int, rid: String) {
        comicRepository.getRandComic(lifecycleOwner, bookid, rid)
                .subscribe(StatusResourceObserver(comicDetailLiveData))
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
    fun loadMore(lifecycleOwner: LifecycleOwner) {
        rPage++
        if (rPage >= 4) {
            rPage = 1
        }
        repository.getListByConversionRate(lifecycleOwner, rPage, rPageSize)
                .subscribe(StatusResourceObserver(moreLiveData, silent = true))
    }


    private var tokenRetryTime = 0

    @SuppressLint("CheckResult")
    fun retryRegisterToken(lifecycleOwner: LifecycleOwner) {
        launchRepository.registerToken(lifecycleOwner).subscribe({}, {
            it.printStackTrace()
            tokenRetryTime++
            if (tokenRetryTime < 5) {
                retryRegisterToken(lifecycleOwner)
            }
        })
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
        /* val token = CommonDataProvider.instance.getToken()
         if (token.isEmpty()) {
             launchRepository.registerToken(lifecycleOwner)
         }
         val imageDomain = CommonDataProvider.instance.getImgDomain()
         if (imageDomain.isEmpty()) {
             launchRepository.getImgDomain(lifecycleOwner)
         }*/

        val preTasks = mutableListOf<Observable<*>>()
        preTasks.add(launchRepository.getImgDomain(lifecycleOwner))
        preTasks.add(launchRepository.registerToken(lifecycleOwner))
        preTasks.add(userInfoRepository.getUserInfo(lifecycleOwner))
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