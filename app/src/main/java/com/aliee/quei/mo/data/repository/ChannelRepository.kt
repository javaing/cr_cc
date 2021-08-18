package com.aliee.quei.mo.data.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.base.response.UIListDataBean
import com.aliee.quei.mo.data.Channel
import com.aliee.quei.mo.data.bean.ChannelHideBean
import com.aliee.quei.mo.data.bean.ChannelInfoBean
import com.aliee.quei.mo.data.bean.toDataBean
import com.aliee.quei.mo.data.service.ChannelService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable

class ChannelRepository : BaseRepository() {
    private var service = RetrofitClient.createService(ChannelService::class.java)
    private val channelId = Channel.channelName

    suspend fun getUserChannelInfo():UIDataBean<ChannelInfoBean> {
        return try {
            service.getUserChannelInfo(channelId).toDataBean()
        } catch (e:Exception) {
            UIDataBean(Status.Error)
        }
    }

    suspend fun getChannelHide () : UIDataBean<List<ChannelHideBean>> {
        return try {
            service.getUserChannelHide(channelId).toDataBean()
        } catch (e:Exception) {
            UIDataBean(Status.Error)
        }
    }
}