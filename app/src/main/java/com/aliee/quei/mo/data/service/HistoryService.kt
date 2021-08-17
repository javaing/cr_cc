package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.BaseResponse
import com.aliee.quei.mo.data.bean.HistoryBean
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Administrator on 2018/4/26 0026.
 */
interface HistoryService{
    @POST("${ApiConstants.API_VERSION}cartoon/history/lists")
    suspend fun loadHistory() : BaseResponse<List<HistoryBean>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/history/remove")
    suspend fun delHistory(@Field("bookId")bookid : Int) : BaseResponse<String>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/history/join")
    suspend fun addHistory(@Field("bookId")bookid : Int,@Field("chapterId")chapterid : Int) : BaseResponse<Any>
}