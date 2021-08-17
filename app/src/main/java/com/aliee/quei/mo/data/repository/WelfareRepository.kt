package com.aliee.quei.mo.data.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.local.TaskBean
import com.aliee.quei.mo.data.local.TaskRewardBean
import com.aliee.quei.mo.data.service.WelfareService
import com.aliee.quei.mo.database.DatabaseProvider
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable
import java.lang.Exception

class WelfareRepository : BaseRepository() {
    private val service = RetrofitClient.createService(WelfareService::class.java)

    fun getCoinRecords(lifecycleOwner: LifecycleOwner,page : Int,pageSize : Int) : Observable<ListBean<WelfareCoinRecordBean>> {
        return service.getCoinRecords(page, pageSize)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleListBean(page, pageSize))
    }

    fun getUserLevel(lifecycleOwner: LifecycleOwner) :Observable<UserLevelBean> {
        return service.getUserLevel()
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
    }

    fun claimReward(lifecycleOwner: LifecycleOwner,taskBean : TaskBean) : Observable<TaskBean> {
        return service.claimReward(taskBean.reward,taskBean.event)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .map{
                if (it.code == 0) {
                    val realm = DatabaseProvider.getRealm()
                    realm.executeTransaction {
                        it.insertOrUpdate(TaskRewardBean(System.currentTimeMillis(),taskBean.reward,taskBean.id))
                    }
                    realm.close()
                    it.data = taskBean
                }
                it
            }
            .compose(handleBean())
    }


    fun getIncome(lifecycleOwner: LifecycleOwner) :Observable<TotalIncomeBean> {
        return service.getIncome()
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
    }

    suspend fun getSignAd():UIDataBean<SignAdBean> {
        return try {
            service.getsignAd().toDataBean()
        } catch (e: Exception){
            UIDataBean(Status.Error)
        }
    }

    suspend fun getWxAd():UIDataBean<WeixinAdBean> {
        return try {
            service.getwxNumber().toDataBean()
        } catch (e: Exception){
            UIDataBean(Status.Error)
        }
    }

    suspend fun getWxAttention(installTime: Long, chapter: String):UIDataBean<WeixinAttentionBean> {
        return try {
            service.getisWxAttention(installTime, chapter).toDataBean()
        } catch (e: Exception){
            UIDataBean(Status.Error)
        }
    }
}