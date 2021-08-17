package com.aliee.quei.mo.utils.rxjava

import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.utils.RxUtils
import com.aliee.quei.mo.utils.ToastUtil
import io.reactivex.FlowableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @Author: YangYang
 * @Date: 2017/12/28
 * @Version: 1.0.0
 * @Description:
 */
object SchedulersUtil {

    fun <T> applySchedulers(): ObservableTransformer<T, T> {
        return ObservableTransformer { observable ->
            observable
                .doOnSubscribe { doCheck() }
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    fun <T> applyFlowableSchedulers(): FlowableTransformer<T, T> {
        return FlowableTransformer { flowable ->
            flowable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    fun <T> applySchedulersIO(): ObservableTransformer<T, T> {
        return ObservableTransformer { observable ->
            observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
        }
    }

    /**
     * 代理检查
     */
    private fun doCheck() {
        if (RxUtils.isWifiProxy(ReaderApplication.instance)) {
            ToastUtil.showToast(ReaderApplication.instance, "检查到使用代理访问，请关闭后重试", 1)
            return
        }
    }

}