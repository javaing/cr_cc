package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.BaseResponse
import com.aliee.quei.mo.data.bean.ListBean
import com.aliee.quei.mo.data.bean.TicketBean
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TicketService{
    @POST("${ApiConstants.API_VERSION}activity/ticketlist")
    fun getTicketList() :Observable<BaseResponse<ListBean<TicketBean>>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}activity/tickreceive")
    fun ticketReceive(@Field("ticketid") ticketId : Int): Observable<BaseResponse<TicketBean>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}bookbean/recevie")
    fun claimReward(@Field("book_bean")coin : Int,@Field("event") event : String): Observable<BaseResponse<Int>>
}