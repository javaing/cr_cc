package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.*

interface VideoInfoService {
    @FormUrlEncoded
    @POST("getVideoDomainType")
    suspend fun getVideoDomainType(@Field("typeString") typeString: String = "20"): BaseResponse<VideoDomainType>

    @FormUrlEncoded
    @POST("player")
    suspend fun getVideoPath(@Field("vid") vid: Int, @Field("tname") tname: String, @Field("client") client: Int = 2): BaseResponse<Any>

    @GET
    fun getVideoThumb(@Url url: String): Observable<String>

    @FormUrlEncoded
    @POST("addmyvideo")
    suspend fun addMyVideo(@Field("vid") vid: Int): BaseResponse<String>

    @FormUrlEncoded
    @POST("delmyvideo")
    suspend fun delMyVideo(@Field("vid") vid: Int): BaseResponse<String>


    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/domain/getsharelink")
    fun videoShare(@Field("vid") vid: Int): Observable<BaseResponse<String>>

    @FormUrlEncoded
    @POST("previewimg")
    fun videoPreViewImg(@Field("vid")vid:Int,@Field("tname")tname:String):Observable<BaseResponse<String>>

    @FormUrlEncoded
    @POST("preview")
    suspend fun videoPreView(@Field("vid")vid:Int,@Field("tname")tname:String,@Field("client")client: Int = 2):BaseResponse<VideoPreview>



    @FormUrlEncoded
    @POST("catelist")
    suspend fun getGuessLike(@Field("type") type: Int = 0, @Field("tag") id: Int? = 1, @Field("page") page: Int = 1,
                             @Field("pageCount") pageCount: Int = 20): BaseResponse<MutableList<VideoBean>>

}