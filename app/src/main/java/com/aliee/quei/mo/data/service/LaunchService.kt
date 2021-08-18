package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LaunchService {
    /**
     * @param type 1 充值前下载 2充值后下载
     */
    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/index/reportapp")
    fun uploadChannelInfo(@Field("id") channelId: String, @Field("version") version: String, @Field("rechargeFlag") type: Int): Observable<BaseResponse<Any?>>

    /**
     *
     * 获取图片资源域名
     */

    @POST("${ApiConstants.API_VERSION}cartoon/domain/get")
    fun getImgDomain(): Observable<BaseResponse<List<ImgDomainConfig>>>

    /**
     *
     */
    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/tempuser/login")
    fun registerToken(@Field("pushToken") str: String): Observable<BaseResponse<TempUser>>


    /**
     *
     * 获取影片资源域名
     */
//    @FormUrlEncoded
//    @POST("getVideoDomainType")
//    fun getVideoDomain(@Field("typeString") type: Int): Observable<BaseResponse<VideoDomainType1>>

    @FormUrlEncoded
    @POST("getVideoDomainType")
    suspend fun getVideoDomainKot(@Field("typeString") type: Int): BaseResponse<VideoDomainType1>

}