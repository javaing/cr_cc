package com.aliee.quei.mo.ui.pay.vm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.repository.RechargeRepository


/**
 * Created by liyang on 2018/6/5 0005.
 */
class OrderVModel : BaseViewModel(){
    private val rechargeRepository = RechargeRepository()
    val checkH5PayResultLiveData = MediatorLiveData<UIDataBean<Boolean>>()

    fun checkH5PayResult(lifecycleOwner: LifecycleOwner, tradeNo : String){
        var tn = tradeNo
//        if (BuildConfig.DEBUG) {
//            tn = "CT2019042513400048974954"
//        }
        rechargeRepository.checkH5PayResult(lifecycleOwner,tn)
                .subscribe(StatusResourceObserver(checkH5PayResultLiveData))
    }
}