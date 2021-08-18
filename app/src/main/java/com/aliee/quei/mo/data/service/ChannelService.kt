package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.BaseResponse
import com.aliee.quei.mo.data.bean.ChannelHideBean
import com.aliee.quei.mo.data.bean.ChannelInfoBean
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ChannelService {
//    @FormUrlEncoded
//    @POST("${ApiConstants.API_VERSION}cartoon/publicchannel/info")
//    fun getUserChannelInfo(@Field("id")id : String) : Observable<BaseResponse<ChannelInfoBean>>

    //{"code":0,"msg":"成功","data":[{"id":17,"hideVideo":1},{"id":32,"hideVideo":1},{"id":35,"hideVideo":1},{"id":50,"hideVideo":1}]}
    //hidevideo 2是app不开  3是都不开之类
    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/publicchannel/hide")
    suspend fun getUserChannelHide(@Field("id")id : String) : BaseResponse<List<ChannelHideBean>>


    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/publicchannel/info")
    suspend fun getUserChannelInfo(@Field("id")id : String) : BaseResponse<ChannelInfoBean>

}