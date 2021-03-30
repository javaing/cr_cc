package com.aliee.quei.mo.data.repository

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.net.Uri
import com.elvishew.xlog.XLog
import com.aliee.quei.mo.config.PayWayConfig
import com.aliee.quei.mo.data.Global
import com.aliee.quei.mo.data.bean.H5OrderBean
import com.aliee.quei.mo.data.bean.PayWayBean
import com.aliee.quei.mo.data.bean.PriceBean
import com.aliee.quei.mo.data.service.RechargeService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable
import retrofit2.http.Field


/**
 * Created by liyang on 2018/6/5 0005.
 */
class RechargeRepository : BaseRepository(){
    private val service = RetrofitClient.createService(RechargeService::class.java)

    fun getPriceList(lifecycleOwner: LifecycleOwner, sort : Int) : Observable<List<PriceBean>> {
        return service.getPriceList(sort)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
    }

    fun getPriceTest(lifecycleOwner: LifecycleOwner, sort : Int) : Observable<List<PriceBean>> {
        return service.getPriceTest(sort)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
    }

    /**
     * @param money 金额（分）
     * @param lang 语言
     * @param app 应用来源 1 => wap,2 => app, 3 =>  公众号
     */
    fun createH5Order(lifecycleOwner: LifecycleOwner,payment : Int,productId : Int,vid: Int) : Observable<H5OrderBean> {
        return service.createOrder(payment,productId,vid)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .map {
                Global[Global.KEY_TRADE_NO] = it.data?.orderId.toString()
                it
            }
            .compose(handleBean())
            .map {
                val bean = it
                val url = bean.payurl
                url?.let {
                    val uri = Uri.parse(it)
                    val scheme = uri.scheme
                    val a = uri.authority

//                    bean.referer = "$scheme://$a/"
                }
                XLog.st(1).e(bean)
                bean
            }
    }

    fun checkH5PayResult(lifecycleOwner: LifecycleOwner, tradeNO: String) : Observable<Boolean>{
        return service.checkH5payResult(tradeNO)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    it.status == 2
                }
    }

    fun getPayConfig(lifecycleOwner: LifecycleOwner, checkSum: String, priceId: Int) : Observable<List<PayWayBean>> {
        return service.payWayConfigNew(checkSum, priceId)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
            .map {
                PayWayConfig.payWayList = it
                it
            }
    }

    fun getPayConfigTest(lifecycleOwner: LifecycleOwner, checkSum: String) : Observable<List<PayWayBean>> {
        return service.payWayConfigTest(checkSum)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    PayWayConfig.payWayList = it
                    it
                }
    }

}