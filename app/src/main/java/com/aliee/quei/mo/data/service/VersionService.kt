package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.AppUpdate
import com.aliee.quei.mo.data.bean.BaseResponse
import com.aliee.quei.mo.data.bean.VersionInfoBean
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface VersionService{

//    /**
//     * @param platform：客户端类型，1安卓，默认值1
//     * @param type：app类型，1充值前下载，默认值1
//     */
//    @FormUrlEncoded
//    @POST("${ApiConstants.API_VERSION}cartoon/client/update")
//    fun getVersionInfo(@Field("platform") platform : Int = 1,@Field("type")type : Int = 1,@Field("appid")appid : String,@Field("version")version : String) : Observable<BaseResponse<VersionInfoBean>>



//    @FormUrlEncoded
//    @POST("${ApiConstants.API_PAY_VERSION}cartoon/update/appop")
//    fun updateAppop(@Field("opType")opType:Int,@Field("uid")uid:Int,@Field("utemp")utemp:Int): Observable<String>
//
//
//    @FormUrlEncoded
//    @POST("${ApiConstants.API_PAY_VERSION}cartoon/update/appget")
//    fun updateAppget(@Field("uid")uid:Int,@Field("utemp")utemp:Int) : Observable<BaseResponse<AppUpdate>>


    /**
     * @param platform：客户端类型，1安卓，默认值1
     * @param type：app类型，1充值前下载，默认值1
     */
    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/client/update")
    suspend fun getVersionInfoK(@Field("platform") platform : Int = 1,@Field("type")type : Int = 1,@Field("appid")appid : String,@Field("version")version : String) : BaseResponse<VersionInfoBean>


    @FormUrlEncoded
    @POST("${ApiConstants.API_PAY_VERSION}cartoon/update/appop")
    suspend fun updateAppopK(@Field("opType")opType:Int,@Field("uid")uid:Int,@Field("utemp")utemp:Int): BaseResponse<String>


    @FormUrlEncoded
    @POST("${ApiConstants.API_PAY_VERSION}cartoon/update/appget")
    suspend fun updateAppgetK(@Field("uid")uid:Int,@Field("utemp")utemp:Int) : BaseResponse<AppUpdate>

}