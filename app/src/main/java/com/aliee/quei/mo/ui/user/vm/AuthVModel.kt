package com.aliee.quei.mo.ui.user.vm

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.repository.AuthRepository
import com.aliee.quei.mo.data.repository.LaunchRepository

class AuthVModel : BaseViewModel() {


    private val repository = AuthRepository()
    private val tempLogin = LaunchRepository()
    val registerLiveData = MediatorLiveData<UIDataBean<Any>>()
    val loginLiveData = MediatorLiveData<UIDataBean<Any>>()
    val resetLiveData = MediatorLiveData<UIDataBean<Any>>()

    fun register(lifecycleOwner: LifecycleOwner, phone: String, password: String) {
        repository.register(lifecycleOwner, phone, password)
            .subscribe(StatusResourceObserver(registerLiveData))
    }

    fun login(lifecycleOwner: LifecycleOwner, phone: String, password: String) {
        repository.login(lifecycleOwner, phone, password)
            .subscribe(StatusResourceObserver(loginLiveData, silent = false))
    }

    fun reset(lifecycleOwner: LifecycleOwner, phone: String, password_orig: String, password: String) {
        repository.reset(lifecycleOwner, phone, password_orig, password)
                .subscribe(StatusResourceObserver(resetLiveData, silent = false))
    }

    fun tempLogin(lifecycleOwner: LifecycleOwner) {
        tempLogin.registerToken(lifecycleOwner)
    }

}