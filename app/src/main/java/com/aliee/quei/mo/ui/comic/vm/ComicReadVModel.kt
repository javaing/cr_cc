package com.aliee.quei.mo.ui.comic.vm

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MediatorLiveData
import com.bumptech.glide.Glide
import com.elvishew.xlog.XLog
import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.*
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.repository.*
import com.aliee.quei.mo.data.service.OtherService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.net.URLEncoder
import java.util.concurrent.ExecutionException

class ComicReadVModel : BaseViewModel(){

    private val contentRepository = ContentRepository()
    private val historyRepository = HistoryRepository()
    private val catalogRepository = CatalogRepository()
    private val shelfRepository = ShelfRepository()
    private val comicRepository = ComicRepository()
    private val recommendRepository = RecommendRepository()
    private val statRepository = StatRepository()
    private val userInfoRepository = UserInfoRepository()


    private var batchImgSize = 0
    val comicDetailLiveData = MediatorLiveData<UIDataBean<ComicBookBean>>()

  //  val contentLiveData = MediatorLiveData<UIDataBean<List<ChapterContentBean>>>()
    val contentLiveData = MediatorLiveData<UIDataBean<BaseResponse<Object>>>()
    val downloadImgLiveData = MediatorLiveData<UIDataBean<List<String>>>()
    val catalogLiveData = MediatorLiveData<UIListDataBean<CatalogItemBean>>()
    val addShelfLiveData = MediatorLiveData<UIDataBean<Any>>()
    val isInShelfLiveData = MediatorLiveData<UIDataBean<Boolean>>()
    val chapterEndRecommendLiveData = MediatorLiveData<UIDataBean<List<RecommendBookBean>>>()
    val uploadReadTimeLiveData = MediatorLiveData<UIDataBean<Any>>()
    val balanceLiveData = MediatorLiveData<UIDataBean<UserInfoBean>>()
    val addHistoryLiveData = MediatorLiveData<UIDataBean<Any>>()

    private val downloadedList = mutableListOf<String>()

    fun getContent(lifecycleOwner: LifecycleOwner,chapterId : Int,positionId:Int) {
        downloadDisposable?.dispose()
        downloadDisposable = null
        contentRepository.getChapterContent1(lifecycleOwner, chapterId,positionId)
            .subscribe(StatusResourceObserver(contentLiveData))
    }
    fun getNewContent(lifecycleOwner: LifecycleOwner,chapterId :Int) {
        downloadDisposable?.dispose()
        downloadDisposable = null
        contentRepository.getNewChapterContent1(lifecycleOwner, chapterId)
            .subscribe(StatusResourceObserver(contentLiveData))
    }
    private var downloadDisposable : Disposable? = null
    @SuppressLint("CheckResult")
    fun downloadImage(activity: Activity, lifecycleOwner: LifecycleOwner, imgs : List<String>) {
        downloadDisposable?.dispose()
        downloadDisposable = null
        downloadImgLiveData.value = UIDataBean(Status.Start)
        val list = imgs.toMutableList()
        Observable.fromIterable(list)
            .subscribeOn(Schedulers.io())
            .map {
                CommonDataProvider.instance.getImgDomain() + "/" + it
            }
            .map {
                try {
                    val v = Glide.with(activity)
                        .load(it)
                        .downloadOnly(720,720)
                        .get()
                    v.absolutePath
                } catch (e : Exception) {
                    XLog.st(1).e(e)
                    var url = it
                    if (e is ExecutionException) {
                        RetrofitClient.createService(OtherService::class.java)
                            .reportErr(URLEncoder.encode(it, "utf-8"))
                            .subscribe ({
                                try {
                                    val u = it.data?.url
                                    if (!u.isNullOrBlank()) {
                                        url = u
                                        val v = Glide.with(activity)
                                            .load(u)
                                            .downloadOnly(720,720)
                                            .get()
                                        url = v.absolutePath
                                    }
                                } catch (e : Exception){
                                    e.printStackTrace()
                                }
                            },{
                                it.printStackTrace()
                            })
                    }
                    url
//                    it
                }

            }
            .observeOn(AndroidSchedulers.mainThread())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .subscribe({
                downloadedList.add(it)
                if (downloadedList.size > batchImgSize) {
                    downloadImgLiveData.value = UIDataBean(Status.Success,downloadedList)
                    downloadedList.clear()
//                    if (batchImgSize < 3) {
//                        batchImgSize ++
//                    }
                }
            },{
                XLog.st(1).e(it)
            },{
                downloadImgLiveData.value = UIDataBean(Status.Success,downloadedList)
                downloadImgLiveData.value = UIDataBean(Status.Complete)
                downloadedList.clear()
            },{
                downloadDisposable = it
            })
    }

    fun addHistory(bookid : Int,chapterId: Int) {
        historyRepository.addHistory(ReaderApplication.instance,bookid,chapterId)
            .subscribe(StatusResourceObserver(addHistoryLiveData))
    }

    fun getCatalog(lifecycleOwner: LifecycleOwner, bookid: Int) {
        catalogRepository.getCatalog(lifecycleOwner,bookid,1,1,BeanConstants.SORT_ASC)
            .subscribe(ListStatusResourceObserver(catalogLiveData))
    }

    fun addToShelf(lifecycleOwner: LifecycleOwner,bookid: Int) {
        shelfRepository.addToShelf(lifecycleOwner,bookid)
            .subscribe(StatusResourceObserver(addShelfLiveData))
    }

    fun isInShelf(lifecycleOwner: LifecycleOwner,bookid: Int){
        comicRepository.isInShelf(lifecycleOwner, bookid)
            .subscribe(StatusResourceObserver(isInShelfLiveData))
    }

    fun getComicDetail(lifecycleOwner: LifecycleOwner,bookid : Int){
        comicRepository.getComicDetail(lifecycleOwner,bookid)
            .subscribe(StatusResourceObserver(comicDetailLiveData))
    }

    fun getChapterEndRecommend(lifecycleOwner: LifecycleOwner,bookid: Int) {
        recommendRepository.getRecommend(lifecycleOwner, BeanConstants.RecommendPosition.CHAPTER_END.rid)
            .subscribe(StatusResourceObserver(chapterEndRecommendLiveData))
    }

    fun uploadReadTime(lifecycleOwner: LifecycleOwner, bookid: Int, readChapterCount: Int, totalMinute: Int) {
        statRepository.uploadReadStats(lifecycleOwner,bookid,readChapterCount,totalMinute)
            .subscribe(StatusResourceObserver(uploadReadTimeLiveData))
    }

    fun getBalance(lifecycleOwner: LifecycleOwner) {
        userInfoRepository.getUserInfo(lifecycleOwner)
            .subscribe(StatusResourceObserver(balanceLiveData))
    }


}