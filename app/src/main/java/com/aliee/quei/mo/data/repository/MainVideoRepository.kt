package com.aliee.quei.mo.data.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import android.util.Log
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.UIDataBean
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
    private val videoService = RetrofitClient.createVideoService(MainVideoService::class.java)

    suspend fun getLongVideo(): UIDataBean<MutableList<Video>> {
        return try {
            videoService.longVideo().toDataBean()
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }
    }

    /**
     * 随机视频
     */
    suspend fun randomList(): UIDataBean<MutableList<Video>> {
        return try {
            videoService.randomList().toDataBean()
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }
    }

    /**
     * 首页视频
     */
    suspend fun mainVideoList(page: Int, pageCount: Int, totalsec: String): UIDataBean<MutableList<Video>> {
        return try {
            videoService.mainVideoList(page, pageCount, totalsec).toDataBean()
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }
    }

    /**
     * 视频分类，标签
     */
    suspend fun mainTags() {
        try {
            val data : MutableList<Tag>? = videoService.mainTagsK().dataConvert()
            data?.let {
                data.add(0, Tag(-1, "推荐"))
                CommonDataProvider.instance.saveVideoTags(data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 排行榜
     */
    suspend fun rankingList(type: Int, tag: Int, page: Int, pageCount: Int): UIDataBean<MutableList<Video>> {
        return try {
            videoService.videoRankingList(type, tag, page, pageCount).toDataBean()
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }
    }

    /**
     * 观看记录
     */
    suspend fun videoRecommend(page: Int, pageCount: Int): UIDataBean<MutableList<Video>> {
        return try {
            videoService.videoRecommend(page, pageCount).toDataBean()
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }
    }

    /**
     * 删除观看记录
     */

    suspend fun delVideoRecommend(hisId: Int): UIDataBean<String> {
        return try {
            val it = videoService.delRecommend(hisId)
            if (it.code != 0) {
                throw RequestException()
            }
            UIDataBean(Status.Success, it.toString())
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }
    }

    suspend fun getMyVideoList(page: Int, pageCount: Int): UIDataBean<MutableList<Video>> {
        return try {
            videoService.getMyVideoList(page, pageCount).toDataBean()
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }
    }

    suspend fun getGuessLikes(id: Int?, page: Int): UIDataBean<MutableList<Video>> {
        return try {
            videoService.getGuessLike(id = id, page = page).toDataBean()
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }
    }

    suspend fun getSearchVideo(keywords: String, page: Int, pageCount: Int): UIDataBean<MutableList<Video>> {
        return try {
            videoService.getSearchVideo(keywords, page, pageCount).toDataBean()
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }
    }

    suspend fun getHotSearchTag(): UIDataBean<Array<String>> {
        return try {
            videoService.getHotSearchTag().toDataBean()
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }
    }

    //tag 点击统计
    suspend fun analyticsVideoTag(tag: Int): UIDataBean<Any> {
        return try {
            videoService.analyticsVideoTag(tag).toDataBean()
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }
    }

    //视频完成播放统计
    suspend fun videoEndPlay(vid: Int, minsec: String = "", done: Int): UIDataBean<String> {
        return try {
            videoService.analyticsClinetPlayRecord(vid, minsec, done).toDataBean()
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }
    }

    suspend fun getShareDomain(): UIDataBean<String> {
        return try {
            val it = service.getShareDomain().data
            UIDataBean(Status.Success, it?.first()?.domain.toString())
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }

    }

    suspend fun autoPlay(){
        try {
            val data = service.getAutoPlayK().toDataBean().data
            data?.let {
                val conf = Gson().toJson(it)
                CommonDataProvider.instance.saveAutoPlayCount(conf)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}