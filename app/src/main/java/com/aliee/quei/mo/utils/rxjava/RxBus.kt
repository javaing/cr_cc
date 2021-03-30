package com.aliee.quei.mo.utils.rxjava

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import com.trello.rxlifecycle2.LifecycleTransformer
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.FlowableProcessor
import io.reactivex.processors.PublishProcessor


/**
 * @Author: YangYang
 * @Date: 2017/12/28
 * @Version: 1.0.0
 * @Description:
 */
class RxBus {
    var _bus: FlowableProcessor<Any>? = null

    init {
        _bus = PublishProcessor.create<Any>().toSerialized()
    }

    companion object {
        @Volatile
        var INSTANCE: RxBus? = null

        fun getInstance(): RxBus {
            if (INSTANCE == null) {
                synchronized(RxBus::class) {
                    if (INSTANCE == null) {
                        INSTANCE = RxBus()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    fun post(o: Any) {
        _bus?.onNext(o)
    }

    fun toObservable(lifecycleTransformer: LifecycleTransformer<Any>): Flowable<Any> {
        return _bus
                ?.compose(lifecycleTransformer)
                ?.onBackpressureBuffer()!!
    }

    fun toMainThreadObservable(lifecycle: LifecycleOwner, event: Lifecycle.Event): Flowable<Any> {
        return _bus
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.bindUntilEvent(lifecycle, event)!!
    }

    fun toUiThreadObservableWithBackPressure(lifecycleTransformer: LifecycleTransformer<Any>): Flowable<Any> {
        return _bus
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.compose(lifecycleTransformer)
                ?.onBackpressureBuffer()!!
    }

    fun toMainThreadObservable(): Flowable<Any> {
        return _bus
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.onBackpressureBuffer()!!
    }
}