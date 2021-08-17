package com.aliee.quei.mo.ui.main.vm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.local.TaskBean
import com.aliee.quei.mo.data.repository.CheckInRepository
import com.aliee.quei.mo.data.repository.TicketRepository
import com.aliee.quei.mo.data.repository.UserInfoRepository
import com.aliee.quei.mo.data.repository.WelfareRepository
import com.aliee.quei.mo.data.service.UserService
import com.aliee.quei.mo.net.retrofit.RetrofitClient

class WelfareVModel : BaseViewModel() {
    private val ticketRepository = TicketRepository()
    private val checkInRepository = CheckInRepository()
    private val welfareRepository = WelfareRepository()
    private val userService = RetrofitClient.createService(UserService::class.java)

    val ticketLiveData = MediatorLiveData<UIDataBean<ListBean<TicketBean>>>()
    val checkInLiveData = MediatorLiveData<UIDataBean<Int>>()
    val checkStatsLiveData = MediatorLiveData<UIDataBean<List<CheckInStatsBean>>>()
    val levelLiveData = MediatorLiveData<UIDataBean<UserLevelBean>>()
    val claimLiveData = MediatorLiveData<UIDataBean<TaskBean>>()
    val userInfoLiveData = MediatorLiveData<UIDataBean<UserInfoBean>>()
    val totalIncomeLiveData = MediatorLiveData<UIDataBean<TotalIncomeBean>>()

    fun getTickets(lifecycleOwner: LifecycleOwner) {
        ticketRepository.getTicketList(lifecycleOwner)
            .subscribe(StatusResourceObserver(ticketLiveData))
    }

    fun checkIn(lifecycleOwner: LifecycleOwner,date : String = "") {
        checkInRepository.checkIn(lifecycleOwner,date)
            .subscribe(StatusResourceObserver(checkInLiveData))
    }

    fun getCheckInStats(lifecycleOwner: LifecycleOwner) {
        checkInRepository.getDailyCheckInStats(lifecycleOwner)
            .subscribe(StatusResourceObserver(checkStatsLiveData))
    }

    fun getUserLevel(lifecycleOwner: LifecycleOwner) {
        welfareRepository.getUserLevel(lifecycleOwner)
            .subscribe(StatusResourceObserver(levelLiveData))
    }

    fun claimReward(lifecycleOwner: LifecycleOwner,taskBean: TaskBean) {
        welfareRepository.claimReward(lifecycleOwner,taskBean)
            .subscribe(StatusResourceObserver(claimLiveData))
    }

    fun getUserInfo() {
//        userInfoRepository.getUserInfo(lifecycleOwner)
//            .subscribe(StatusResourceObserver(userInfoLiveData))
        viewModelLaunch ({
            userInfoLiveData.value= userService.getUserInfo().toDataBean()
        }, {})
    }

    fun getIncome(lifecycleOwner: LifecycleOwner) {
        welfareRepository.getIncome(lifecycleOwner)
            .subscribe(StatusResourceObserver(totalIncomeLiveData))
    }
}