package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.BaseResponse
import com.aliee.quei.mo.data.bean.PictureBean
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OtherService {
    //TODO
    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/imgerror/report")
    fun reportErr(@Field("picurl")url : String) : Observable<BaseResponse<PictureBean>>
}