package com.aliee.quei.mo.ui.main.vm

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MediatorLiveData
import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.repository.*
import com.meituan.android.walle.WalleChannelReader
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable

class MainVModel : BaseViewModel() {
    private val mainVideoRepository = MainVideoRepository()
    private val recommendRepository = RecommendRepository()
    private val channelRepository = ChannelRepository()
    private val versionRepository = VersionRepository()
    private val checkInRepository = CheckInRepository()
    private val welfareRepository = WelfareRepository()
    private val historyRepository = HistoryRepository()
    private val repository = LaunchRepository()
    private val userInfoRepository = UserInfoRepository()

    val historyLiveData = MediatorLiveData<UIDataBean<List<HistoryBean>>>()
    val recommendLiveData = MediatorLiveData<UIDataBean<List<RecommendBookBean>>>()
    val channelInfoLiveData = MediatorLiveData<UIDataBean<ChannelInfoBean>>()
    val versionLiveData = MediatorLiveData<UIDataBean<VersionInfoBean>>()
    val checkInLiveData = MediatorLiveData<UIDataBean<Int>>()
    val bulletinLiveData = MediatorLiveData<UIDataBean<BulletinBean>>()
    val wxNumberLiveData = MediatorLiveData<UIDataBean<WeixinAdBean>>()
    val wxAttentionLiveData = MediatorLiveData<UIDataBean<WeixinAttentionBean>>()
    val signLiveData = MediatorLiveData<UIDataBean<SignAdBean>>()
    val appupdateopLiveData = MediatorLiveData<UIDataBean<String>>()
    val appDrainageLiveData = MediatorLiveData<UIDataBean<String>>()
    val adZoneLiveData = MediatorLiveData<UIDataBean<List<AdZoneBean>>>()
    val videoDomainData = MediatorLiveData<UIDataBean<VideoDomainType1>>()
    val updateAppgetLiveData = MediatorLiveData<UIDataBean<AppUpdate>>()


    fun appUpdateOp(lifecycleOwner: LifecycleOwner, uid: Int, utemp: Int, opType: Int) {
        recommendRepository.appUpdateOp(lifecycleOwner, uid, utemp, opType).subscribe(StatusResourceObserver(appupdateopLiveData))
    }

    fun appDrainage(lifecycleOwner: LifecycleOwner, uid: Int, utemp: Int) {
        recommendRepository.appDrainage(lifecycleOwner, uid, utemp).subscribe(StatusResourceObserver(appDrainageLiveData))
    }


    fun getRecommend(lifecycleOwner: LifecycleOwner) {
        recommendRepository.getRecommend(lifecycleOwner, BeanConstants.RecommendPosition.OPEN_APP_RECOMMEND.rid)
                .subscribe(StatusResourceObserver(recommendLiveData))
    }

    fun getUserChannelInfo(lifecycleOwner: LifecycleOwner) {
        val channel = WalleChannelReader.getChannel(ReaderApplication.instance, "1") ?: "1"
        channelRepository.getChannelInfo(lifecycleOwner, channel)
                .subscribe(StatusResourceObserver(channelInfoLiveData, silent = true))
    }

    fun versionCheck(lifecycleOwner: LifecycleOwner, appid: String, version: String) {
        versionRepository.checkVersion(lifecycleOwner, appid, version)
                .subscribe(StatusResourceObserver(versionLiveData, silent = true))
    }

    fun getReadHistory(lifecycleOwner: LifecycleOwner) {
        historyRepository.loadHistory(lifecycleOwner)
                .subscribe(StatusResourceObserver(historyLiveData))
    }

    fun autoCheckIn(lifecycleOwner: LifecycleOwner, date: String = "") {
        checkInRepository.checkIn(lifecycleOwner, date)
                .subscribe(StatusResourceObserver(checkInLiveData, silent = true))
    }

    fun getBulletin(lifecycleOwner: LifecycleOwner) {
        checkInRepository.getBulletin(lifecycleOwner)
                .subscribe(StatusResourceObserver(bulletinLiveData, silent = true))
    }

    fun getSignAd(lifecycleOwner: LifecycleOwner) {
        welfareRepository.getSignAd(lifecycleOwner)
                .subscribe(StatusResourceObserver(signLiveData))
    }

    fun getWxAd(lifecycleOwner: LifecycleOwner) {
        welfareRepository.getWxAd(lifecycleOwner)
                .subscribe(StatusResourceObserver(wxNumberLiveData))
    }

    fun getWxAttention(lifecycleOwner: LifecycleOwner, installTime: Long, chapter: String) {
        welfareRepository.getWxAttention(lifecycleOwner, installTime, chapter)
                .subscribe(StatusResourceObserver(wxAttentionLiveData))
    }

    fun getAdZone(lifecycleOwner: LifecycleOwner, status: Int) {
        recommendRepository.adZone(lifecycleOwner, status)
                .subscribe(StatusResourceObserver(adZoneLiveData))
    }

    fun getVideoDomain(lifecycleOwner: LifecycleOwner) {
        repository.getVideoDomain(lifecycleOwner)
                .subscribe(StatusResourceObserver(videoDomainData))
    }


    fun updateAppop(lifecycleOwner: LifecycleOwner, opType: Int, uid: Int, utemp: Int) {
        versionRepository.appop(lifecycleOwner, opType, uid, utemp)
                .subscribe()
    }

    fun updateAppget(lifecycleOwner: LifecycleOwner, uid: Int, utemp: Int) {
        versionRepository.appget(lifecycleOwner, uid, utemp)
                .subscribe(StatusResourceObserver(updateAppgetLiveData))
    }

    fun videoMemberInfo(lifecycleOwner: LifecycleOwner) {
        userInfoRepository.getMemberInfo(lifecycleOwner)
                .subscribe()
    }

    fun mainTags(lifecycleOwner: LifecycleOwner) {
        mainVideoRepository.mainTags(lifecycleOwner)
                .subscribe()
    }

    fun autoPlay(lifecycleOwner: LifecycleOwner){
        mainVideoRepository.getAutoPlay(lifecycleOwner)
                .subscribe()
    }
}