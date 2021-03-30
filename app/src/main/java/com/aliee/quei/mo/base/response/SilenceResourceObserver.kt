package com.aliee.quei.mo.base.response

import io.reactivex.observers.ResourceObserver

class SilenceResourceObserver<T> : ResourceObserver<T>() {
    override fun onComplete() {
    }

    override fun onNext(t: T) {
    }

    override fun onError(e: Throwable) {
    }
}