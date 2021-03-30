package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.BaseResponse
import com.aliee.quei.mo.data.bean.UserRecoverBean
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
interface AuthService{
    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/user/reg")
    fun register(@Field("phone") phone : String,@Field("password") password : String) : Observable<BaseResponse<Any>>

    /**
     * @param reg  1 => 注册 ；0 => 登录
     */
    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/user/login")
    fun login(@Field("phone") phone : String,@Field("password") password : String) : Observable<BaseResponse<Any>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/user/reset/password")
    fun reset(@Field("phone") phone : String,@Field("passwordOrig") password_orig : String,@Field("password") password : String) : Observable<BaseResponse<Any>>


    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/order/user")
    fun recoverUser(@Field("orderId") tradeNo : String) : Observable<BaseResponse<UserRecoverBean>>

    /**
     *
     */
    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/tempuser/login")
    fun registerToken(@Field("pushToken") str: String): Observable<BaseResponse<Any>>
}