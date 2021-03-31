package com.aliee.quei.mo.data.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.aliee.quei.mo.data.local.ReadStatBean
import com.aliee.quei.mo.data.service.StatService
import com.aliee.quei.mo.database.DatabaseProvider
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable
import java.util.*

class StatRepository : BaseRepository() {
    private val service = RetrofitClient.createService(StatService::class.java)

    fun uploadReadStats(lifecycleOwner: LifecycleOwner,bookid : Int,cnumber : Int,total_minute : Int) : Observable<Any> {
        val realm = DatabaseProvider.getRealm()
        realm.executeTransaction {
            val calendar = Calendar.getInstance()
            val bean = ReadStatBean(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.WEEK_OF_YEAR),
                bookid,cnumber,total_minute)
            it.insert(bean)
        }
        realm.close()

        return service.uploadReadTime(bookid,cnumber,total_minute)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
    }
}