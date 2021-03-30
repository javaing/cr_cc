package com.aliee.quei.mo.data.repository

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.util.Log
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.service.RecommendService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable

class RecommendRepository : BaseRepository() {
    private val service = RetrofitClient.createService(RecommendService::class.java)

    /**
     * 通过推荐位ID获取推荐书籍
     */
    fun getRecommend(
            lifecycleOwner: LifecycleOwner,
            id: String
    ): Observable<MutableList<RecommendBookBean>> {
        return service.getRecommend(id)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    it[id]
                }
                .map {
                    it.list.forEach { it.rid = id }
                    it.list
                }
    }

    /**
     * 获取首页推荐位的数据
     */
    fun getRecommendBatch(
            lifecycleOwner: LifecycleOwner,
            ids: String
    ): Observable<MutableList<RecommendListBean>> {
        return service.getRecommend(ids)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    val data = it

                    val result = it.keys.map { key ->
                        val rp = BeanConstants.RecommendPosition.getByRid(key) //
                        val list: MutableList<RecommendBookBean>? = data[key]?.list
                        list?.forEach {
                            it.rid = rp?.rid
                        }
                        RecommendListBean(key, rp?.title ?: "", list)
                    }
                    result as MutableList<RecommendListBean>
                }
    }

    fun getListByConversionRate(
            lifecycleOwner: LifecycleOwner,
            page: Int,
            pageSize: Int,
            sortField: String = "rate_order"
    ): Observable<ListBean<RecommendBookBean>> {
        return service.getComicList(pageSize, sortField, page)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    val result = ListBean<RecommendBookBean>()
                    result.list = it.map {
                        RecommendBookBean(
                                it.author,
                                it.thumb,
                                it.id,
                                it.description,
                                it.status,
                                it.thumbX,
                                it.title,
                                it.typename
                        )
                    }
                    result
                }
    }

    fun appUpdateOp(lifecycle: LifecycleOwner, uid: Int, utemp: Int, opType: Int): Observable<String> {
        return service.appUpdateOp(uid, utemp, opType)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycle, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
    }

    fun appDrainage(lifecycle: LifecycleOwner, uid: Int, utemp: Int): Observable<String> {
        return service.appDrainage(uid, utemp)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycle, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
    }

    fun adZone(lifecycle: LifecycleOwner, status: Int): Observable<List<AdZoneBean>> {
        return service.getadzone(status)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycle, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
    }

}