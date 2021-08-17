package com.aliee.quei.mo.ui.main.vm

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.repository.*
import kotlinx.coroutines.launch

class MainVModel : BaseViewModel() {
    private val channelRepository = ChannelRepository()
    private val versionRepository = VersionRepository()
    private val launchRepository = LaunchRepository()
    private val checkInRepository = CheckInRepository()
    private val recommendRepository = RecommendRepository()
    private val welfareRepository = WelfareRepository()
    private val userInfoRepository = UserInfoRepository()
    private val historyRepository = HistoryRepository()
    private val mainVideoRepository = MainVideoRepository()


    val videoDomainData = MediatorLiveData<UIDataBean<VideoDomainType1>>()
    val updateAppgetLiveData = MediatorLiveData<UIDataBean<AppUpdate>>()
    val versionLiveData = MediatorLiveData<UIDataBean<VersionInfoBean>>()
    val checkInLiveData = MediatorLiveData<UIDataBean<CoinBean>>()
    val bulletinLiveData = MediatorLiveData<UIDataBean<BulletinBean>>()
    val channelInfoLiveData = MediatorLiveData<UIDataBean<ChannelInfoBean>>()
    val historyLiveData = MediatorLiveData<UIDataBean<List<HistoryBean>>>()
    val wxNumberLiveData = MediatorLiveData<UIDataBean<WeixinAdBean>>()
    val wxAttentionLiveData = MediatorLiveData<UIDataBean<WeixinAttentionBean>>()
    val signLiveData = MediatorLiveData<UIDataBean<SignAdBean>>()
    val recommendLiveData = MediatorLiveData<UIDataBean<MutableList<RecommendBookBean>>>()
    val adZoneLiveData = MediatorLiveData<UIDataBean<List<AdZoneBean>>>()
    val appupdateopLiveData = MediatorLiveData<UIDataBean<String>>()
    val appDrainageLiveData = MediatorLiveData<UIDataBean<String>>()
    val channelHideLiveData = MediatorLiveData<UIDataBean<List<ChannelHideBean>>>()


    fun getUserChannelHide() {
        viewModelScope.launch {
            channelHideLiveData.value = channelRepository.getChannelHide()
        }
    }

    fun appUpdateOp(uid: Int, utemp: Int, opType: Int) {
        viewModelScope.launch {
            appupdateopLiveData.value = recommendRepository.appUpdateOp(uid, utemp, opType)
        }
    }

    fun appDrainage(uid: Int, utemp: Int) {
        viewModelScope.launch {
            appDrainageLiveData.value = recommendRepository.appDrainage(uid, utemp)
        }
    }


    fun getAdZone( status: Int) {
        viewModelScope.launch {
            adZoneLiveData.value = recommendRepository.getAdZone(status)
        }
    }

    fun getRecommend() {
        viewModelScope.launch {
            val id = BeanConstants.RecommendPosition.OPEN_APP_RECOMMEND.rid
            recommendLiveData.value =  recommendRepository.getRecommend(id)
        }
    }

    fun getSignAd() {
        viewModelScope.launch {
            signLiveData.value = welfareRepository.getSignAd()
        }
    }

    fun getWxAd() {
        viewModelScope.launch {
            wxNumberLiveData.value = welfareRepository.getWxAd()
        }
    }

    fun getWxAttention(installTime: Long, chapter: String) {
        viewModelScope.launch {
            wxAttentionLiveData.value = welfareRepository.getWxAttention(installTime, chapter)
        }
    }

    fun videoMemberInfo() {
        viewModelScope.launch {
            val it = userInfoRepository.videoMemberInfo()
            CommonDataProvider.instance.saveFreeTime(it?.freetime.toString())
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            historyLiveData.value = historyRepository.loadHistory()
        }
    }

    fun getUserChannelInfo() {
        viewModelScope.launch {
            channelInfoLiveData.value = channelRepository.getUserChannelInfo()
        }
    }

    fun getVideoDomain() {
        viewModelScope.launch {
            videoDomainData.value = launchRepository.getVideoDomain()
        }
    }

    fun updateAppop(opType: Int, uid: Int, utemp: Int) {
        viewModelScope.launch {
            val data = versionRepository.updateAppop(opType, uid, utemp)
        }
    }

    fun updateAppget(uid: Int, utemp: Int) {
        viewModelScope.launch {
            updateAppgetLiveData.value = versionRepository.updateAppget( uid, utemp)
        }
    }

    fun versionCheck(appid: String, version: String) {
        viewModelScope.launch {
            versionLiveData.value = versionRepository.getVersionInfo(appid, version)
        }
    }

    fun autoCheckIn(date: String = "") {
        viewModelScope.launch {
            checkInLiveData.value = checkInRepository.autoCheckIn(date)
        }
    }

    fun getBulletin() {
        viewModelScope.launch {
            bulletinLiveData.value = checkInRepository.getBulletin()
        }
    }

    fun mainTags() {
        viewModelScope.launch {
            mainVideoRepository.mainTags()
        }
    }

    fun autoPlay(){
        viewModelScope.launch {
            mainVideoRepository.autoPlay()
        }
    }

}