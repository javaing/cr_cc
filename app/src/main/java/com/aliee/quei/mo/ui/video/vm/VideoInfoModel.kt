package com.aliee.quei.mo.ui.video.vm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.repository.VideoInfoRepository
import kotlinx.coroutines.launch

class VideoInfoModel : BaseViewModel() {
    private val videoInfoRepository = VideoInfoRepository()

    val guessLikes = MediatorLiveData<UIDataBean<MutableList<VideoBean>>>()

    val videoPath = MediatorLiveData<UIDataBean<Any>>()

    val videoDomainType = MediatorLiveData<UIDataBean<VideoDomainType>>()


    val addMyVideo = MediatorLiveData<UIDataBean<BaseResponse<String>>>()

    val videoShare = MediatorLiveData<UIDataBean<String>>()

    fun loadGuessLike(id:Int?) {
        viewModelScope.launch {
            guessLikes.value = videoInfoRepository.getGuessLikes(id)
        }
    }

    //图片域名
    fun getVideoDomainType() {
        viewModelScope.launch {
            videoDomainType.value = videoInfoRepository.getVideoDomainType()
        }
    }

    //视频域名
    fun getVideoPath(vid: Int, tname: String) {
        viewModelScope.launch {
            videoPath.value = videoInfoRepository.getVideoPath(vid, tname)
        }
    }


    fun addMyVideo(vid:Int){
        viewModelScope.launch {
            addMyVideo.value = videoInfoRepository.addMyVideo(vid)
        }
    }

    fun videoShare(lifecycleOwner: LifecycleOwner,vid: Int){
        videoInfoRepository.videoShare(lifecycleOwner,vid)
                .subscribe(StatusResourceObserver(videoShare))
    }
}