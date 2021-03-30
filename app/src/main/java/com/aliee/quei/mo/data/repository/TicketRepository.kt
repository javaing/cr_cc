package com.aliee.quei.mo.data.repository

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import com.aliee.quei.mo.data.bean.ListBean
import com.aliee.quei.mo.data.bean.TicketBean
import com.aliee.quei.mo.data.local.TicketClaimRecord
import com.aliee.quei.mo.data.service.TicketService
import com.aliee.quei.mo.database.DatabaseProvider
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable

class TicketRepository : BaseRepository() {
    private val service = RetrofitClient.createService(TicketService::class.java)

    fun getTicketList(lifecycleOwner: LifecycleOwner) : Observable<ListBean<TicketBean>>{
        return service.getTicketList()
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
    }

    fun ticketReceive(lifecycleOwner: LifecycleOwner,ticketBean: TicketBean) : Observable<TicketBean> {
        return service.ticketReceive(ticketBean.id)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .map {
                it.data = ticketBean
                val realm = DatabaseProvider.getRealm()
                if (it.code == 3004 || it.code == 3005) {
                    realm.executeTransaction {
                        val record = TicketClaimRecord(ticketBean.id,System.currentTimeMillis())
                        it.insertOrUpdate(record)
                    }
                }
                it
            }
            .compose(handleBean())
    }

    fun claimReward(lifecycleOwner: LifecycleOwner,amount : Int,event : String,ticketId : Int) : Observable<Int> {
        return service.claimReward(amount,event)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .map{
                if (it.code == 0) {
                    val realm = DatabaseProvider.getRealm()
                    realm.executeTransaction {r->
                        r.where(TicketClaimRecord::class.java)
                            .equalTo("tid",ticketId)
                            .findFirst()?.status = 1
                    }
                    it.data = amount
                }
                it
            }
            .compose(handleBean())
    }

}