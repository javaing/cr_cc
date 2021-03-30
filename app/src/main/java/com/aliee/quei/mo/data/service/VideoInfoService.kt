package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.*

interface VideoInfoService {
    @FormUrlEncoded
    @POST("${ApiConstants.VIDEO_API_PATH}catelist")
    fun getGuessLike(@Field("type") type: Int = 0, @Field("tag") id: Int? = 1, @Field("page") page: Int = 1,
                     @Field("pageCount") pageCount: Int = 20): Observable<BaseResponse<MutableList<VideoBean>>>

    @FormUrlEncoded
    @POST("${ApiConstants.VIDEO_API_PATH}getVideoDomainType")
    fun getVideoDomainType(@Field("typeString") typeString: String = "20"): Observable<BaseResponse<VideoDomainType>>

    @FormUrlEncoded
    @POST("${ApiConstants.VIDEO_API_PATH}player")
    fun getVideoDomain(@Field("vid") vid: Int, @Field("tname") tname: String, @Field("client") client: Int = 2): Observable<Any>

    @GET
    fun getVideoThumb(@Url url: String): Observable<String>

    @FormUrlEncoded
    @POST("${ApiConstants.VIDEO_API_PATH}addmyvideo")
    fun addMyVideo(@Field("vid") vid: Int): Observable<BaseResponse<String>>

    @FormUrlEncoded
    @POST("${ApiConstants.VIDEO_API_PATH}delmyvideo")
    fun delMyVideo(@Field("vid") vid: Int): Observable<BaseResponse<String>>


    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/domain/getsharelink")
    fun videoShare(@Field("vid") vid: Int): Observable<BaseResponse<String>>

    @FormUrlEncoded
    @POST("${ApiConstants.VIDEO_API_PATH}previewimg")
    fun videoPreViewImg(@Field("vid")vid:Int,@Field("tname")tname:String):Observable<BaseResponse<String>>

    @FormUrlEncoded
    @POST("${ApiConstants.VIDEO_API_PATH}preview")
    fun videoPreView(@Field("vid")vid:Int,@Field("tname")tname:String,@Field("client")client: Int = 2):Observable<BaseResponse<VideoPreview>>
}