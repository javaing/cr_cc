package com.aliee.quei.mo.ui.user.vm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.bean.UserRecoverBean
import com.aliee.quei.mo.data.repository.AuthRepository

class RecoverUserVModel : BaseViewModel(){
    private val authRepository = AuthRepository()

    val recoverUserLiveData = MediatorLiveData<UIDataBean<UserRecoverBean>>()
    fun recoverUser(lifecycleOwner: LifecycleOwner,tradeNo : String) {
        authRepository.recoverUser(lifecycleOwner, tradeNo)
            .subscribe(StatusResourceObserver(recoverUserLiveData))
    }
}