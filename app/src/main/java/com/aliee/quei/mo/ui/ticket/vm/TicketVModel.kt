package com.aliee.quei.mo.ui.ticket.vm

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MediatorLiveData
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.bean.ListBean
import com.aliee.quei.mo.data.bean.TicketBean
import com.aliee.quei.mo.data.repository.TicketRepository

class TicketVModel () :BaseViewModel() {
    private val ticketRepository = TicketRepository()
    val ticketLiveData = MediatorLiveData<UIDataBean<ListBean<TicketBean>>>()
    val ticketReceiveLiveData = MediatorLiveData<UIDataBean<TicketBean>>()
    val claimReadLiveData = MediatorLiveData<UIDataBean<Int>>()

    fun getTicketList(lifecycleOwner: LifecycleOwner) {
        ticketRepository.getTicketList(lifecycleOwner)
            .subscribe(StatusResourceObserver(ticketLiveData))
    }

    fun ticketReceive(lifecycleOwner: LifecycleOwner,ticketBean: TicketBean) {
        ticketRepository.ticketReceive(lifecycleOwner,ticketBean)
            .subscribe(StatusResourceObserver(ticketReceiveLiveData))
    }

    fun claimReward(lifecycleOwner: LifecycleOwner,amount :Int,event : String,tid : Int){
        ticketRepository.claimReward(lifecycleOwner,amount,event,tid)
            .subscribe(StatusResourceObserver(claimReadLiveData))
    }
}