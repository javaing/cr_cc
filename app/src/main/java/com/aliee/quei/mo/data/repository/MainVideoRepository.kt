package com.aliee.quei.mo.data.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import android.util.Log
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.exception.RequestException
import com.aliee.quei.mo.data.service.LaunchService
import com.aliee.quei.mo.data.service.MainVideoService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.google.gson.Gson
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable
import kotlin.math.log

class MainVideoRepository : BaseRepository() {
    private val service = RetrofitClient.createService(MainVideoService::class.java)

    fun getLongVideo(lifecycleOwner: LifecycleOwner): Observable<MutableList<Video>> {
        return service.longVideo()
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    it
                }
    }

    /**
     * 随机视频
     */
    fun randomList(lifecycleOwner: LifecycleOwner): Observable<MutableList<Video>> {
        return service.randomList()
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    it
                }
    }

    /**
     * 首页视频
     */
    fun mainVideoList(lifecycleOwner: LifecycleOwner, page: Int, pageCount: Int, totalsec: String): Observable<MutableList<Video>> {
        return service.mainVideoList(page, pageCount, totalsec)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    it
                }
    }

    /**
     * 视频分类，标签
     */
    fun mainTags(lifecycleOwner: LifecycleOwner): Observable<MutableList<Tag>> {
        return service.mainTags()
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    val tags: MutableList<Tag> = it
                    tags.add(0, Tag(-1, "推荐"))
                    CommonDataProvider.instance.saveVideoTags(tags)
                    tags
                }
    }

    /**
     * 排行榜
     */
    fun rankingList(lifecycleOwner: LifecycleOwner, type: Int, tag: Int, page: Int, pageCount: Int): Observable<MutableList<Video>> {
        return service.videoRankingList(type, tag, page, pageCount)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    it
                }
    }

    /**
     * 观看记录
     */
    fun videoRecommend(lifecycleOwner: LifecycleOwner, page: Int, pageCount: Int): Observable<MutableList<Video>> {
        return service.videoRecommend(page, pageCount)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    it
                }
    }

    /**
     * 删除观看记录
     */

    fun delVideoRecommend(lifecycleOwner: LifecycleOwner, hisId: Int): Observable<String> {
        return service.delRecommend(hisId)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .map {
                    if (it.code != 0) {
                        throw RequestException(it.code, it.msg.toString())
                    }
                    it.toString()
                }
    }


    fun getMyVideoList(lifecycleOwner: LifecycleOwner, page: Int, pageCount: Int): Observable<MutableList<Video>> {
        return service.getMyVideoList(page, pageCount)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    it
                }
    }

    fun getGuessLikes(lifecycleOwner: LifecycleOwner, id: Int?, page: Int): Observable<MutableList<Video>> {
        return service.getGuessLike(id = id, page = page)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    it
                }
    }

    fun getSearchVideo(lifecycleOwner: LifecycleOwner, keywords: String, page: Int, pageCount: Int): Observable<MutableList<Video>> {
        return service.getSearchVideo(keywords, page, pageCount)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())

    }

    fun getHotSearchTag(lifecycleOwner: LifecycleOwner): Observable<Array<String>> {
        return service.getHotSearchTag()
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    it
                }
    }

    //tag 点击统计
    fun analyticsVideoTag(lifecycleOwner: LifecycleOwner, tag: Int): Observable<Any> {
        return service.analyticsVideoTag(tag)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
    }

    //视频完成播放统计
    fun videoEndPlay(lifecycleOwner: LifecycleOwner, vid: Int, minsec: String = "", done: Int): Observable<String> {
        return service.analyticsClinetPlayRecord(vid, minsec, done)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
    }

    /* fun getShareDomain(lifecycleOwner: LifecycleOwner):Observable<<List<ImgDomainConfig>>{
         return service.getShareDomain()
                 .compose(SchedulersUtil.applySchedulers())
                 .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
                 .compose(handleBean())
     }*/
    fun getShareDomain(lifecycleOwner: LifecycleOwner): Observable<String> {
        return service.getShareDomain()
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    /*CommonDataProvider.instance.saveImgDomain(it[0].domain.toString())
                    Log.d("tag---url", it[0].domain.toString())
                    var domains = ""
                    it.forEach { config ->
                        domains += "${config.domain},"
                    }
                    if (domains.endsWith(",")) {
                        domains = domains.substring(0, domains.length - 1)
                    }
                    CommonDataProvider.instance.saveApiDomain(domains)*/
                    it[0].domain.toString()
                }

    }

    fun getAutoPlay(lifecycleOwner: LifecycleOwner): Observable<AutoPlayConf> {
        return service.getAutoPlay()
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    val conf = Gson().toJson(it)
                    CommonDataProvider.instance.saveAutoPlayCount(conf)
                    it
                }
    }
}