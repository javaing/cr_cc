package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Administrator on 2018/5/8 0008.
 */
interface RechargeService {

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/order/confirm")
    fun createOrder(@Field("paymentId") paymentId: Int, @Field("priceId") productId: Int, @Field("vid") vid: Int = -1): Observable<BaseResponse<H5OrderBean>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/order/status")
    fun checkH5payResult(@Field("orderId") tradeNo: String): Observable<BaseResponse<H5PayResultBean>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/pay/pricelist")
    fun getPriceList(@Field("sort") sort: Int): Observable<BaseResponse<List<PriceBean>>>

    @POST("${ApiConstants.API_VERSION}cartoon/pay/payment")
    fun payWayConfig(): Observable<BaseResponse<List<PayWayBean>>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_PAY_VERSION}cartoon/pay/newPayment")
    fun payWayConfigNew(@Field("checkSum") checkSum: String, @Field("price_id") priceId: Int): Observable<BaseResponse<List<PayWayBean>>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/pay/pricetest")
    fun getPriceTest(@Field("sort") sort: Int): Observable<BaseResponse<List<PriceBean>>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_PAY_VERSION}cartoon/pay/testPayment")
    fun payWayConfigTest(@Field("checkSum") checkSum: String): Observable<BaseResponse<List<PayWayBean>>>

}