package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.local.TaskBean
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface WelfareService {
    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}bookbean/log")
    fun getCoinRecords(@Field("page")page : Int,@Field("pagesize") pageSize : Int) : Observable<BaseResponse<ListBean<WelfareCoinRecordBean>>>

    @POST("${ApiConstants.API_VERSION}fans/level")
    fun getUserLevel() : Observable<BaseResponse<UserLevelBean>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}bookbean/recevie")
    fun claimReward(@Field("book_bean")coin : Int,@Field("event") event : String): Observable<BaseResponse<TaskBean>>

    @POST("${ApiConstants.API_VERSION}activity/alldata")
    fun getAllTasks(): Observable<BaseResponse<ListBean<TaskBean>>>

    @POST("${ApiConstants.API_VERSION}bookbean/income")
    fun getIncome() : Observable<BaseResponse<TotalIncomeBean>>

    @POST("${ApiConstants.API_VERSION}cartoon/activity/user/sign")
    suspend fun getsignAd(): BaseResponse<SignAdBean>

    @POST("${ApiConstants.API_VERSION}cartoon/activity/WXAttention")
    suspend fun getwxNumber(): BaseResponse<WeixinAdBean>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/activity/user/isWXAReceivable")
    suspend fun getisWxAttention(@Field("installTime")installTime : Long,@Field("chapter")chapter : String): BaseResponse<WeixinAttentionBean>
}