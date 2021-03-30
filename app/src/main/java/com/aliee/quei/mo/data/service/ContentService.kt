package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.BaseResponse
import com.aliee.quei.mo.data.bean.ChapterContentBean
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ContentService {
    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/chapter/chapter")
    fun getChapterContent(@Field("chapterId") chapterId: Int): Observable<BaseResponse<ChapterContentBean>>

   @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/chapter/chapterWithNext")
    fun getNewChapterContent1(@Field("chapterId") chapterId: Int): Observable<BaseResponse<Object>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/chapter/newChapterWithNext")
    fun getChapterContent1(@Field("bookId") bookId: Int,@Field("positionId")positionId:Int): Observable<BaseResponse<Object>>


}