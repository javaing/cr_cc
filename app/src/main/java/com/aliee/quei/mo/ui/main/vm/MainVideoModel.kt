package com.aliee.quei.mo.ui.main.vm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.repository.MainVideoRepository
import com.aliee.quei.mo.data.repository.VideoInfoRepository

class MainVideoModel : BaseViewModel() {

    private val repository = MainVideoRepository()
    private val videoInfoRepository = VideoInfoRepository()

    val videoDomainType = MediatorLiveData<UIDataBean<VideoDomainType>>()

    val mainVideoLiveData = MediatorLiveData<UIDataBean<MutableList<Video>>>()
    val mainVideoLoadMoreLiveData = MediatorLiveData<UIDataBean<MutableList<Video>>>()

    val mainVideoTags = MediatorLiveData<UIDataBean<MutableList<Tag>>>()

    val videoDomain = MediatorLiveData<UIDataBean<Any>>()

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
    val videoPreViewImgLiveData = MediatorLiveData<UIDataBean<String>>()

    val videoAutoPlayLiveData = MediatorLiveData<UIDataBean<String>>()



    var page: Int = 1
    var pageCount: Int = 20


    fun randomList(lifecycleOwner: LifecycleOwner){
        repository.randomList(lifecycleOwner)
                .subscribe(StatusResourceObserver(mainRandomVideoLoadMoreLiveData))
    }

    //长视频
    fun getLongVideoList(lifecycleOwner: LifecycleOwner) {
        repository.getLongVideo(lifecycleOwner)
                .subscribe(StatusResourceObserver(longVideoLiveData))
    }

    //长视频加载更多
    fun getLongVideoLoadMore(lifecycleOwner: LifecycleOwner) {
        repository.getLongVideo(lifecycleOwner)
                .subscribe(StatusResourceObserver(longVideoLoadMoreLiveData))
    }

    //图片域名
    fun getVideoDomainType(lifecycleOwner: LifecycleOwner) {
        videoInfoRepository.getVideoDomain(lifecycleOwner)
                .subscribe(StatusResourceObserver(videoDomainType))
    }

    fun mainVideoList(lifecycleOwner: LifecycleOwner, totalsec: String = "") {
        page = 1
        repository.mainVideoList(lifecycleOwner, page, pageCount, totalsec)
                .subscribe(StatusResourceObserver(mainVideoLiveData))
    }

    fun mainVideoLoadMore(lifecycleOwner: LifecycleOwner, totalsec: String = "") {
        page++
        repository.mainVideoList(lifecycleOwner, page, pageCount, totalsec)
                .subscribe(StatusResourceObserver(mainVideoLoadMoreLiveData))
    }

    //视频域名
    fun getVideoDomain(lifecycleOwner: LifecycleOwner, vid: Int, tname: String) {
        videoInfoRepository.getVideoDomain(lifecycleOwner, vid, tname)
                .subscribe(StatusResourceObserver(videoDomain))
    }

    fun mainVideoTags(lifecycleOwner: LifecycleOwner) {
        repository.mainTags(lifecycleOwner)
                .subscribe(StatusResourceObserver(mainVideoTags))
    }

    fun addMyVideo(lifecycleOwner: LifecycleOwner, vid: Int) {
        videoInfoRepository.addMyVideo(lifecycleOwner, vid)
                .subscribe(StatusResourceObserver(addMyVideo))
    }

    fun videoRankingList(lifecycleOwner: LifecycleOwner, type: Int = 2, tag: Int) {
        page = 1
        repository.rankingList(lifecycleOwner, type, tag, page, pageCount)
                .subscribe(StatusResourceObserver(videoRankingLiveData))
    }

    fun videoRankingLoadMoreList(lifecycleOwner: LifecycleOwner, type: Int = 2, tag: Int) {
        page++
        repository.rankingList(lifecycleOwner, type, tag, page, pageCount)
                .subscribe(StatusResourceObserver(videoRankingLoadMoreLiveData))
    }

    fun getMyVideo(lifecycleOwner: LifecycleOwner) {
        page = 1
        repository.getMyVideoList(lifecycleOwner, page, pageCount)
                .subscribe(StatusResourceObserver(myVideoListLiveData))
    }

    fun getLoadMoreMyVideo(lifecycleOwner: LifecycleOwner) {
        page++
        repository.getMyVideoList(lifecycleOwner, page, pageCount)
                .subscribe(StatusResourceObserver(myVideoListLoadMoreLiveData))
    }

    fun delMyVideo(lifecycleOwner: LifecycleOwner, vid: Int) {
        videoInfoRepository.delMyVideo(lifecycleOwner, vid)
                .subscribe(StatusResourceObserver(delMyVideoLiveData))
    }

    fun videoRecommend(lifecycleOwner: LifecycleOwner) {
        page = 1
        repository.videoRecommend(lifecycleOwner, page, pageCount)
                .subscribe(StatusResourceObserver(videoRecommendLiveData))
    }

    fun videoRecommendLoadMore(lifecycleOwner: LifecycleOwner) {
        page++
        repository.videoRecommend(lifecycleOwner, page, pageCount)
                .subscribe(StatusResourceObserver(videoRecommendLoadMoreLiveData))
    }

    fun delVideoRecommend(lifecycleOwner: LifecycleOwner, hisId: Int) {
        repository.delVideoRecommend(lifecycleOwner, hisId)
                .subscribe(StatusResourceObserver(delVideoRecommendLiveData))
    }

    fun guessLike(lifecycleOwner: LifecycleOwner, id: Int?, page: Int) {
        repository.getGuessLikes(lifecycleOwner, id, page)
                .subscribe(StatusResourceObserver(guessLikes))
    }


    fun getSearchVideo(lifecycleOwner: LifecycleOwner, keywords: String) {
        page = 1
        repository.getSearchVideo(lifecycleOwner, keywords, page, pageCount)
                .subscribe(StatusResourceObserver(searchVideoLiveData))
    }

    fun getSearchVideoLoadMore(lifecycleOwner: LifecycleOwner, keywords: String) {
        page++
        repository.getSearchVideo(lifecycleOwner, keywords, page, pageCount)
                .subscribe(StatusResourceObserver(searchVideoLoadMoreLiveData))
    }

    fun getHotSearch(lifecycleOwner: LifecycleOwner) {
        repository.getHotSearchTag(lifecycleOwner)
                .subscribe(StatusResourceObserver(searchHotTag))
    }

    /**
     * 视频tag点击数统计
     */
    fun analyticsVideoTag(lifecycleOwner: LifecycleOwner, tag: Int) {
        repository.analyticsVideoTag(lifecycleOwner, tag)
                .subscribe(StatusResourceObserver(analyticsLiveData))
    }

    /**
     * 视频完播数统计
     */
    fun videoEndPlay(lifecycleOwner: LifecycleOwner, vid: Int, minsec: String = "", done: Int) {
        repository.videoEndPlay(lifecycleOwner, vid, minsec, done)
                .subscribe(StatusResourceObserver(analyticsLiveData))
    }

    fun getShareDomain(lifecycleOwner: LifecycleOwner) {
        repository.getShareDomain(lifecycleOwner)
                .subscribe(StatusResourceObserver(shareDomainLiveData))
    }

    fun getVideoPreViewImg(lifecycleOwner: LifecycleOwner,vid:Int,tname:String){
        videoInfoRepository.videoPreViewImg(lifecycleOwner,vid, tname)
                .subscribe(StatusResourceObserver(videoPreViewImgLiveData))
    }
    fun getVideoPreView(lifecycleOwner: LifecycleOwner,vid:Int,tname:String){
        videoInfoRepository.videoPreView(lifecycleOwner,vid, tname)
                .subscribe(StatusResourceObserver(videoPreViewLiveData))
    }
}