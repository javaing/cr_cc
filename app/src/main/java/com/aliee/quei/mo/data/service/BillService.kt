package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface BillService{

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/tradeRecord/lists")
    fun getBillList(@Field("page")page : Int,@Field("pagesize")pageSize : Int) : Observable<BaseResponse<ListBean<BillRecordBean>>>


    @POST("${ApiConstants.API_PAY_VERSION}cartoon/cashback/lists")
    fun getBonusList() : Observable<BaseResponse<List<BonusRecordBean>>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_PAY_VERSION}cartoon/recommend/parent")
    fun getBonusID(@Field("recommendcode")recommendcode : String) : Observable<BaseResponse<BonusIDBean>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/order/code")
    fun sendExchange(@Field("code")exchangecode : String) : Observable<BaseResponse<Any>>
}