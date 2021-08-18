package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.BaseResponse
import com.aliee.quei.mo.data.bean.CategoryBean
import com.aliee.quei.mo.data.bean.ComicBookBean
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CategoryService {
    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/cartoonType/list")
    suspend fun getCategory(@Field("sort")sort : Int = BeanConstants.SORT_ASC) : BaseResponse<List<CategoryBean>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/cartoon/lists")
    suspend fun getList(@Field("typeId") id : Int,
                @Field("sexy") sex : Int,
                @Field("status") status : Int,
                @Field("page") page : Int,
                @Field("pagesize") pageSize : Int) : BaseResponse<List<ComicBookBean>>
}