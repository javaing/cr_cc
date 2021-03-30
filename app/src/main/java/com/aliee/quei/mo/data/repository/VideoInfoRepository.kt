package com.aliee.quei.mo.data.repository

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.util.Log
import com.google.gson.Gson
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.exception.RequestException
import com.aliee.quei.mo.data.service.UserService
import com.aliee.quei.mo.data.service.VideoInfoService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable

class VideoInfoRepository : BaseRepository() {

    private val service = RetrofitClient.createService(VideoInfoService::class.java)


    fun getGuessLikes(lifecycleOwner: LifecycleOwner,id: Int?): Observable<MutableList<VideoBean>> {
        return service.getGuessLike(id = id)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    it
                }
    }

    //image domain
    fun getVideoDomain(lifecycleOwner: LifecycleOwner): Observable<VideoDomainType> {
        return service.getVideoDomainType()
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    it
                }

    }

    //video domain
    fun getVideoDomain(lifecycleOwner: LifecycleOwner, vid: Int, tname: String): Observable<Any> {
        return service.getVideoDomain(vid, tname)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .map {
                    it
                }
    }

    fun getVideoThumb(lifecycleOwner: LifecycleOwner, url: String): Observable<String> {
        return service.getVideoThumb(url)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .map {
                    Log.d("tag", "image:$it")
                    it
                }
    }


    fun addMyVideo(lifecycleOwner: LifecycleOwner, vid: Int): Observable<BaseResponse<String>> {
        return service.addMyVideo(vid)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .map {
                    it
                }
    }

    fun delMyVideo(lifecycleOwner: LifecycleOwner, vid: Int): Observable<String> {
        return service.delMyVideo(vid)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .map {
                    if (it.code != 0) {
                        throw RequestException(it.code, it.msg.toString())
                    }
                    vid.toString()
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


    fun videoPreViewImg(lifecycleOwner: LifecycleOwner,vid:Int,tname:String):Observable<String>{
        return service.videoPreViewImg(vid, tname)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    it
                }
    }
    fun videoPreView(lifecycleOwner: LifecycleOwner,vid:Int,tname:String):Observable<VideoPreview>{
        return service.videoPreView(vid, tname)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    it
                }
    }


}