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
    @FormUrlEncoded
    @POST("${ApiConstants.API_PAY_VERSION}cartoon/domain/getVideoDomainType")
    fun getVideoDomain(@Field("typeString") type: Int): Observable<BaseResponse<VideoDomainType1>>

    /**
     * 视频登录
     */
    @FormUrlEncoded
    @POST("${ApiConstants.VIDEO_API_PATH}login")
    //app= 1
    //tname= u_temp_user_0
    //refid
    //linkid=0
    //recommend
    //from
    fun videoLogin(
                   @Field("tname")tname:String = "u_temp_user_0",
                   @Field("refid")refid:String = "",
                   @Field("recommend")recommend:String = "",
                   @Field("from")from:String = ""
    ):Observable<BaseResponse<VideoAuth>>

}