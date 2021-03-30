package com.aliee.quei.mo.data.repository

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import com.aliee.quei.mo.data.bean.ComicBookBean
import com.aliee.quei.mo.data.bean.HistoryLastBean
import com.aliee.quei.mo.data.service.ComicService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable

class ComicRepository : BaseRepository(){
    private val service = RetrofitClient.createService(ComicService::class.java)

    fun getComicDetail(lifecycleOwner: LifecycleOwner,bookid : Int) : Observable<ComicBookBean> {
        return service.getComicDetail(bookid)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
    }

    fun getRandComic(lifecycleOwner: LifecycleOwner,bookid: Int,rid : String) : Observable<ComicBookBean> {
        return service.getRandComic(bookid,rid)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
    }

    fun getHistoryChapter(lifecycleOwner: LifecycleOwner,bookid: Int):Observable<HistoryLastBean>{
        return service.onHistoryChapter(bookid)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())

    }

    fun isInShelf(lifecycleOwner: LifecycleOwner,bookid: Int) : Observable<Boolean> {
        return service.isInShelf(bookid)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .map {
                it.code == 1010
            }
    }


    fun searchByKey(lifecycle: LifecycleOwner, key: String): Observable<List<ComicBookBean>>{
        return service.search(key)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycle, Lifecycle.Event.ON_DESTROY)
            .compose(handleList())
    }



}