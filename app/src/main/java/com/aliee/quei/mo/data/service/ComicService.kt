package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.BaseResponse
import com.aliee.quei.mo.data.bean.ComicBookBean
import com.aliee.quei.mo.data.bean.HistoryLastBean
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Administrator on 2018/4/18 0018.
 */
interface ComicService {

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/cartoon/info")
    fun getComicDetail(@Field("cartoonId") bookid: Int): Observable<BaseResponse<ComicBookBean>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/cartoon/rand")
    fun getRandComic(@Field("bookId") bookid: Int, @Field("id") rid: String): Observable<BaseResponse<ComicBookBean>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/cartoon/search")
    fun search(@Field("keyword") keyword: String): Observable<BaseResponse<List<ComicBookBean>>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/fav/status")
    fun isInShelf(@Field("bookid") bookid: Int): Observable<BaseResponse<Any>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/history/last")
    fun onHistoryChapter(@Field("bookId") bookid: Int): Observable<BaseResponse<HistoryLastBean>>


}