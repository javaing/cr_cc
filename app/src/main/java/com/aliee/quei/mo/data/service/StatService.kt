package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.BaseResponse
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface StatService {

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}report/read")
    fun uploadReadTime(@Field("bookid") bookid : Int,@Field("cnumber") cnumber : Int,@Field("total_time") total_time : Int) : Observable<BaseResponse<Any>>
}