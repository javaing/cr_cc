package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.BaseResponse
import com.aliee.quei.mo.data.bean.CatalogListBean
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CatalogService{
    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/chapter/catalog")
    fun getCatalog(@Field("cartoonId") bookid : Int,@Field("page") page : Int,@Field("pagesize")pageSize : Int,@Field("sort")sort : Int): Observable<BaseResponse<CatalogListBean>>
}