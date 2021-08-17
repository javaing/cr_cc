package com.aliee.quei.mo.data.repository

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.aliee.quei.mo.BuildConfig
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.data.bean.TempUser
import com.aliee.quei.mo.data.bean.VideoDomainType1
import com.aliee.quei.mo.data.bean.toDataBean
import com.aliee.quei.mo.data.service.LaunchService
import com.aliee.quei.mo.net.ApiConstants
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.SharedPreUtils
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable

class LaunchRepository : BaseRepository() {
    private val service = RetrofitClient.createService(LaunchService::class.java)
    private val serviceVideo = RetrofitClient.createVideoService(LaunchService::class.java)

    fun uploadChannelInfo(lifecycleOwner: LifecycleOwner, channelId: String, version: String): Observable<Any?> {
        var flag = 1
        if (BuildConfig.FLAVOR.equals("forceLogin")) {
            flag = 2
        }
        return service.uploadChannelInfo(channelId, version, flag)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .map {
                    if (it.code == 0) {

                        SharedPreUtils.getInstance().putBoolean("hasUpload", true)
                    }
                    it
                }
                .compose(handleBean())
    }

    fun getImgDomain(lifecycleOwner: LifecycleOwner): Observable<Any> {
        return service.getImgDomain()
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    CommonDataProvider.instance.saveImgDomain(it[0].domain.toString())
                    Log.d("tag---url", it[0].domain.toString())
                    var domains = ""
                    it.forEach { config ->
                        domains += "${config.domain},"
                    }
                    if (domains.endsWith(",")) {
                        domains = domains.substring(0, domains.length - 1)
                    }
                    CommonDataProvider.instance.saveApiDomain(domains)
                    it
                }
    }

    fun registerToken(lifecycleOwner: LifecycleOwner): Observable<TempUser> {
        //return service.registerToken(ReaderApplication.jpushid)
        return service.registerToken("dsadsa131231231")
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .compose(handleBean())
                .map {
                    CommonDataProvider.instance.setToken(it.servertoken)
                    CommonDataProvider.instance.setRegisterToken(it.servertoken)
                    CommonDataProvider.instance.saveUserTempType(it.istemp)
                    it
                }
    }

    suspend fun getVideoDomain(): UIDataBean<VideoDomainType1> {
        return try {
            serviceVideo.getVideoDomainKot(ApiConstants.VIDEO_TYPESTRING.toInt()).toDataBean()
        } catch (e:Exception) {
            UIDataBean(Status.Error)
        }
    }


}