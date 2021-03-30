package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CheckInService {
    @POST("${ApiConstants.API_VERSION}fans/signlist")
    fun getWeekCheckStats() : Observable<BaseResponse<List<CheckInStatsBean>>>

    /**
     * @param date yyyy-MM-dd
     */
    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}fans/sign")
    fun checkIn(@Field("date") date : String) : Observable<BaseResponse<CoinBean>>

    @POST("${ApiConstants.API_VERSION}cartoon/event/advertising/latest")
    fun getBulletin() : Observable<BaseResponse<BulletinBean>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/bulletin/list")
    fun getBulletinList(@Field("status") status : Int,
                        @Field("page") page : Int,
                        @Field("pagesize") pageSize : Int) : Observable<BaseResponse<Object>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/bulletin/selectById")
    fun getBulletinDetail(@Field("id") bulletinid : Int) : Observable<BaseResponse<BulletinDetailBean>>

}