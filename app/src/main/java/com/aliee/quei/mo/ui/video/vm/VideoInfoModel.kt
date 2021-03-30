package com.aliee.quei.mo.ui.video.vm

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.bean.BaseResponse
import com.aliee.quei.mo.data.bean.VideoBean
import com.aliee.quei.mo.data.bean.VideoDomainType
import com.aliee.quei.mo.data.bean.VideoInfo
import com.aliee.quei.mo.data.repository.VideoInfoRepository
import okhttp3.*
import java.io.IOException

class VideoInfoModel : BaseViewModel() {
    private val videoInfoRepository = VideoInfoRepository()

    val guessLikes = MediatorLiveData<UIDataBean<MutableList<VideoBean>>>()

    val videoDomain = MediatorLiveData<UIDataBean<Any>>()

    val videoDomainType = MediatorLiveData<UIDataBean<VideoDomainType>>()

    val videoThumbData = MediatorLiveData<UIDataBean<String>>()

    val addMyVideo = MediatorLiveData<UIDataBean<BaseResponse<String>>>()

    val videoShare = MediatorLiveData<UIDataBean<String>>()

    fun loadGuessLike(lifecycleOwner: LifecycleOwner,id:Int?) {
        videoInfoRepository.getGuessLikes(lifecycleOwner,id = id)
                .subscribe(StatusResourceObserver(guessLikes))
    }

    //图片域名
    fun getVideoDomainType(lifecycleOwner: LifecycleOwner) {
        videoInfoRepository.getVideoDomain(lifecycleOwner)
                .subscribe(StatusResourceObserver(videoDomainType))
    }

    //视频域名
    fun getVideoDomain(lifecycleOwner: LifecycleOwner, vid: Int, tname: String) {
        videoInfoRepository.getVideoDomain(lifecycleOwner, vid, tname)
                .subscribe(StatusResourceObserver(videoDomain))
    }


    fun getVideoThumb(lifecycleOwner: LifecycleOwner, url: String) {
        videoInfoRepository.getVideoThumb(lifecycleOwner, url)
                .subscribe(StatusResourceObserver(videoThumbData))
    }

    fun addMyVideo(lifecycleOwner: LifecycleOwner,vid:Int){
        videoInfoRepository.addMyVideo(lifecycleOwner,vid)
                .subscribe(StatusResourceObserver(addMyVideo))
    }

    fun videoShare(lifecycleOwner: LifecycleOwner,vid: Int){
        videoInfoRepository.videoShare(lifecycleOwner,vid)
                .subscribe(StatusResourceObserver(videoShare))
    }
}