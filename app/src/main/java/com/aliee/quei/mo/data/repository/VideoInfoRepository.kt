package com.aliee.quei.mo.data.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import android.util.Log
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.exception.RequestException
import com.aliee.quei.mo.data.service.VideoInfoService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable

class VideoInfoRepository : BaseRepository() {

    private val service = RetrofitClient.createVideoService(VideoInfoService::class.java)


    suspend fun getGuessLikes(id: Int?): UIDataBean<MutableList<VideoBean>> {
        return try {
            service.getGuessLike(id = id).toDataBean()
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }
    }

    //image domain
    suspend fun getVideoDomainType(): UIDataBean<VideoDomainType> {
        return try {
            service.getVideoDomainType().toDataBean()
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }
    }

    //video domain
    suspend fun getVideoPath(vid: Int, tname: String): UIDataBean<Any> {
        return try {
            service.getVideoPath(vid, tname).toDataBean()
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }
    }

    suspend fun addMyVideo(vid: Int): UIDataBean<BaseResponse<String>> {
        return try {
            UIDataBean(Status.Success, service.addMyVideo(vid))
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }
    }

    suspend fun delMyVideo(vid: Int): UIDataBean<String> {
        return try {
            val it = service.delMyVideo(vid)
            if (it.code != 0) {
                throw RequestException(it.code, it.msg.toString())
            }
            UIDataBean(Status.Success, vid.toString())
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }

    }

    fun videoShare(lifecycleOwner: LifecycleOwner,vid:Int):Observable<String> {
        return service.videoShare(vid)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    it
                }
    }


    suspend fun videoPreView(vid:Int,tname:String):UIDataBean<VideoPreview>{
        return try {
            service.videoPreView(vid, tname).toDataBean()
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }

    }


}