package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.BaseResponse
import com.aliee.quei.mo.data.bean.UserInfoBean
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Administrator on 2018/4/20 0020.
 */
interface UserService{
//    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/user/info")
    suspend fun getUserInfo():BaseResponse<UserInfoBean>

    @POST("${ApiConstants.API_VERSION}fans/sign")
    fun dailySign(): Observable<BaseResponse<Any>>

    @FormUrlEncoded
    @POST("getMemberInfo")
    suspend fun getMemberInfo(@Field("refid")refid:String="",
                         @Field("recommend")recommend:String="",
                         @Field("From")from:String="")
            :BaseResponse<UserInfoBean>
}