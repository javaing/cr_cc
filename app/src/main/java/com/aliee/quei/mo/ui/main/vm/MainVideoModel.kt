package com.aliee.quei.mo.ui.main.vm

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.repository.MainVideoRepository
import com.aliee.quei.mo.data.repository.VideoInfoRepository
import kotlinx.coroutines.launch

class MainVideoModel : BaseViewModel() {

    private val repository = MainVideoRepository()
    private val videoInfoRepository = VideoInfoRepository()

    val videoDomainType = MediatorLiveData<UIDataBean<VideoDomainType>>()
    val mainVideoLiveData = MediatorLiveData<UIDataBean<MutableList<Video>>>()
    val mainVideoLoadMoreLiveData = MediatorLiveData<UIDataBean<MutableList<Video>>>()
    val mainVideoTags = MediatorLiveData<UIDataBean<MutableList<Tag>>>()
    val videoPath = MediatorLiveData<UIDataBean<Any>>()
    val addMyVideo = MediatorLiveData<UIDataBean<BaseResponse<String>>>()
    val videoRankingLiveData = MediatorLiveData<UIDataBean<MutableList<Video>>>()
    val videoRankingLoadMoreLiveData = MediatorLiveData<UIDataBean<MutableList<Video>>>()
    val videoRecommendLiveData = MediatorLiveData<UIDataBean<MutableList<Video>>>()
    val videoRecommendLoadMoreLiveData = MediatorLiveData<UIDataBean<MutableList<Video>>>()
    val delVideoRecommendLiveData = MediatorLiveData<UIDataBean<String>>()
    val delMyVideoLiveData = MediatorLiveData<UIDataBean<String>>()
    val myVideoListLiveData = MediatorLiveData<UIDataBean<MutableList<Video>>>()
    val myVideoListLoadMoreLiveData = MediatorLiveData<UIDataBean<MutableList<Video>>>()
    val guessLikes = MediatorLiveData<UIDataBean<MutableList<Video>>>()
    val searchVideoLiveData = MediatorLiveData<UIDataBean<MutableList<Video>>>()
    val searchVideoLoadMoreLiveData = MediatorLiveData<UIDataBean<MutableList<Video>>>()
    val searchHotTag = MediatorLiveData<UIDataBean<Array<String>>>()
    val analyticsLiveData = MediatorLiveData<UIDataBean<Any>>()
    val longVideoLiveData = MediatorLiveData<UIDataBean<MutableList<Video>>>()
    val longVideoLoadMoreLiveData = MediatorLiveData<UIDataBean<MutableList<Video>>>()
    val shareDomainLiveData = MediatorLiveData<UIDataBean<String>>()
    val mainRandomVideoLoadMoreLiveData = MediatorLiveData<UIDataBean<MutableList<Video>>>()
    val videoPreViewLiveData = MediatorLiveData<UIDataBean<VideoPreview>>()
    val videoAutoPlayLiveData = MediatorLiveData<UIDataBean<String>>()



    var page: Int = 1
    var pageCount: Int = 20


    fun randomList(){
        viewModelScope.launch {
            mainRandomVideoLoadMoreLiveData.value = repository.randomList()
        }
    }

    //长视频
    fun getLongVideoList() {
        viewModelScope.launch {
            longVideoLiveData.value = repository.getLongVideo()
        }
    }

    //长视频加载更多
    fun getLongVideoLoadMore() {
        viewModelScope.launch {
            longVideoLoadMoreLiveData.value = repository.getLongVideo()
        }
    }

    //图片域名
    fun getVideoDomainType() {
        viewModelScope.launch {
            videoDomainType.value = videoInfoRepository.getVideoDomainType()
        }
    }

    fun mainVideoList(totalsec: String = "") {
        viewModelScope.launch {
            page = 1
            mainVideoLiveData.value = repository.mainVideoList(page, pageCount, totalsec)
        }
    }


    //视频域名
    fun getVideoPath(vid: Int, tname: String) {
        viewModelScope.launch {
            videoPath.value = videoInfoRepository.getVideoPath(vid, tname)
        }
    }

    fun addMyVideo(vid: Int) {
        viewModelScope.launch {
            addMyVideo.value = videoInfoRepository.addMyVideo(vid)
        }
    }

    fun videoRankingList(type: Int = 2, tag: Int) {
        viewModelScope.launch {
            page = 1
            videoRankingLiveData.value = repository.rankingList(type, tag, page, pageCount)
        }
    }

    fun videoRankingLoadMoreList(type: Int = 2, tag: Int) {
        viewModelScope.launch {
            page++
            videoRankingLoadMoreLiveData.value = repository.rankingList(type, tag, page, pageCount)
        }
    }

    fun getMyVideo() {
        viewModelScope.launch {
            page = 1
            myVideoListLiveData.value = repository.getMyVideoList(page, pageCount)
        }
    }

    fun getLoadMoreMyVideo() {
        viewModelScope.launch {
            page++
            myVideoListLoadMoreLiveData.value = repository.getMyVideoList(page, pageCount)
        }
    }

    fun delMyVideo(vid: Int) {
        viewModelScope.launch {
            delMyVideoLiveData.value = videoInfoRepository.delMyVideo(vid)
        }
    }

    fun videoRecommend() {
        viewModelScope.launch {
            page = 1
            videoRecommendLiveData.value = repository.videoRecommend(page, pageCount)
        }
    }

    fun videoRecommendLoadMore() {
        viewModelScope.launch {
            page++
            videoRecommendLoadMoreLiveData.value = repository.videoRecommend(page, pageCount)
        }
    }

    fun delVideoRecommend(hisId: Int) {
        viewModelScope.launch {
            delVideoRecommendLiveData.value = repository.delVideoRecommend(hisId)
        }
    }

    fun guessLike(id: Int?, page: Int) {
        viewModelScope.launch {
            guessLikes.value = repository.getGuessLikes(id, page)
        }
    }


    fun getSearchVideo(keywords: String) {
        viewModelScope.launch {
            page = 1
            searchVideoLiveData.value = repository.getSearchVideo(keywords, page, pageCount)
        }
    }

    fun getSearchVideoLoadMore(keywords: String) {
        viewModelScope.launch {
            page++
            searchVideoLoadMoreLiveData.value = repository.getSearchVideo(keywords, page, pageCount)
        }
    }

    fun getHotSearch() {
        viewModelScope.launch {
            searchHotTag.value = repository.getHotSearchTag()
        }
    }

    /**
     * 视频tag点击数统计
     */
    fun analyticsVideoTag(tag: Int) {
        viewModelScope.launch {
            analyticsLiveData.value = repository.analyticsVideoTag(tag)
        }
    }

    /**
     * 视频完播数统计
     */
    fun videoEndPlay(vid: Int, minsec: String = "", done: Int) {
        viewModelScope.launch {
            val it= repository.videoEndPlay(vid, minsec, done)
            Log.e("tag", "视频完播数统计:$it")
        }
    }

    fun getShareDomain() {
        viewModelScope.launch {
            shareDomainLiveData.value = repository.getShareDomain()
        }
    }

    fun getVideoPreView(vid:Int, tname:String){
        viewModelScope.launch {
            videoPreViewLiveData.value = videoInfoRepository.videoPreView(vid, tname)
        }
    }
}