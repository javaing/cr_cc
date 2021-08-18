package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.AdBean
import com.aliee.quei.mo.data.bean.AdInfo
import com.aliee.quei.mo.data.bean.BaseResponse
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface AdService {

    @FormUrlEncoded
    @POST("${ApiConstants.API_PAY_VERSION}cartoon/dsp/list")
    suspend fun adList(@Field("groupid") groupid : Int) : BaseResponse<MutableList<AdBean>>

    @GET
    fun adInfo(@Url url: String): Observable<ResponseBody>

    @GET
    suspend fun adInfoK(@Url url: String): ResponseBody
}