package com.aliee.quei.mo.data.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.aliee.quei.mo.data.bean.BaseResponse
import com.aliee.quei.mo.data.bean.ChapterContentBean
import com.aliee.quei.mo.data.service.ContentService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable

class ContentRepository : BaseRepository() {
    private val service = RetrofitClient.createService(ContentService::class.java)

    fun getChapterContent(lifecycleOwner: LifecycleOwner, chapterId: Int): Observable<ChapterContentBean> {
        return service.getChapterContent(chapterId)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
            .map {
                //  CommonDataProvider.instance.currentReading = it[0].book
                it
            }
    }


    fun getChapterContent1(
        lifecycleOwner: LifecycleOwner,
        chapterId: Int,
        positionId: Int
    ): Observable<BaseResponse<Object>> {
        return service.getChapterContent1(chapterId, positionId)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
            .map {
                //  CommonDataProvider.instance.currentReading = it[0].book
                it
            }
    }

    fun getNewChapterContent1(
        lifecycleOwner: LifecycleOwner,
        chapterId: Int
    ): Observable<BaseResponse<Object>> {
        return service.getNewChapterContent1(chapterId)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
            .map {
                //  CommonDataProvider.instance.currentReading = it[0].book
                it
            }
    }
}