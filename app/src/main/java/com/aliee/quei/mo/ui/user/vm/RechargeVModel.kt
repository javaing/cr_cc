package com.aliee.quei.mo.ui.user.vm

import android.annotation.SuppressLint
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.repository.RechargeRepository
import com.aliee.quei.mo.data.repository.UserInfoRepository
import com.aliee.quei.mo.data.service.UserService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import retrofit2.http.Field

class RechargeVModel : BaseViewModel() {
    private val rechargeRepository = RechargeRepository()
    private val userService = RetrofitClient.createService(UserService::class.java)

    val priceListLiveData = MediatorLiveData<UIDataBean<List<PriceBean>>>()
    val createOrderLiveData = MediatorLiveData<UIDataBean<H5OrderBean>>()
    val balanceLiveData = MediatorLiveData<UIDataBean<UserInfoBean>>()
    val payWayLiveData = MediatorLiveData<UIDataBean<List<PayWayBean>>>()
    val payWayTestLiveData = MediatorLiveData<UIDataBean<List<PayWayBean>>>()

    fun getList(lifecycleOwner: LifecycleOwner) {
        rechargeRepository.getPriceList(lifecycleOwner, BeanConstants.SORT_ASC)
                .subscribe(StatusResourceObserver(priceListLiveData))
    }

    fun getTest(lifecycleOwner: LifecycleOwner) {
        rechargeRepository.getPriceTest(lifecycleOwner, BeanConstants.SORT_ASC)
                .subscribe(StatusResourceObserver(priceListLiveData))
    }

    fun createOrder(lifecycleOwner: LifecycleOwner, paymentId: Int, productId: Int, vid: Int) {
        rechargeRepository.createH5Order(lifecycleOwner, paymentId, productId, vid)
                .subscribe(StatusResourceObserver(createOrderLiveData, silent = false))
    }

    fun getBalance() {
//        userInfoRepository.getUserInfo(lifecycleOwner)
//                .subscribe(StatusResourceObserver(balanceLiveData))
        viewModelLaunch ({
            balanceLiveData.value=userService.getUserInfo().toDataBean()
        }, {})
    }

    @SuppressLint("CheckResult")
    fun getPayWayConfig(lifecycleOwner: LifecycleOwner, checkSum: String, priceId: Int) {
        rechargeRepository.getPayConfig(lifecycleOwner, checkSum, priceId)
                .subscribe(StatusResourceObserver(payWayLiveData))
    }

    @SuppressLint("CheckResult")
    fun getPayWayConfigTest(lifecycleOwner: LifecycleOwner, checkSum: String) {
        rechargeRepository.getPayConfigTest(lifecycleOwner, checkSum)
                .subscribe(StatusResourceObserver(payWayTestLiveData))
    }

}