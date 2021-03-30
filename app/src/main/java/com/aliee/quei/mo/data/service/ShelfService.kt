package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.BaseResponse
import com.aliee.quei.mo.data.bean.ShelfBean
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Administrator on 2018/4/26 0026.
 */
interface ShelfService{

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/fav/add")
    fun addToShelf(@Field("bookid") bookid : Int) : Observable<BaseResponse<Any>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/fav/list")
    fun getShelfList(@Field("page") page : Int,@Field("pagesize") pageSize : Int) : Observable<BaseResponse<List<ShelfBean>>>
}