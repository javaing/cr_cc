package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.BaseResponse
import com.aliee.quei.mo.data.bean.ChannelInfoBean
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ChannelService {
    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/publicchannel/info")
    fun getUserChannelInfo(@Field("id")id : String) : Observable<BaseResponse<ChannelInfoBean>>
}