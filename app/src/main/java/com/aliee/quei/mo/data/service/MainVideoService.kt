package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import org.androidannotations.annotations.rest.Post
import retrofit2.http.*

interface MainVideoService {

    @POST("${ApiConstants.VIDEO_API_PATH}randomlist")
    fun randomList(): Observable<BaseResponse<MutableList<Video>>>

    @FormUrlEncoded
    @POST("${ApiConstants.VIDEO_API_PATH}list")
    fun mainVideoList(@Field("page") page: Int,
                      @Field("pageCount") pageCount: Int,
                      @Field("totalsec") totalsec: String):
            Observable<BaseResponse<MutableList<Video>>>

    @POST("${ApiConstants.VIDEO_API_PATH}showtags")
    fun mainTags(): Observable<BaseResponse<MutableList<Tag>>>

    @FormUrlEncoded
    @POST("${ApiConstants.VIDEO_API_PATH}catelist")
    fun videoRankingList(@Field("type") type: Int = 2,
                         @Field("tag") tag: Int,
                         @Field("page") page: Int,
                         @Field("pageCount") pageCount: Int):
            Observable<BaseResponse<MutableList<Video>>>


    //观看记录
    @FormUrlEncoded
    @POST("${ApiConstants.VIDEO_API_PATH}getplayhis")
    fun videoRecommend(@Field("page") page: Int,
                       @Field("pageCount") pageCount: Int): Observable<BaseResponse<MutableList<Video>>>


    //删除观看记录
    @FormUrlEncoded
    @POST("${ApiConstants.VIDEO_API_PATH}delplayhist")
    fun delRecommend(@Field("hisid") hisid: Int): Observable<BaseResponse<String>>


    @FormUrlEncoded
    @POST("${ApiConstants.VIDEO_API_PATH}listmyvideo")
    fun getMyVideoList(@Field("page") page: Int,
                       @Field("pageCount") pageCount: Int): Observable<BaseResponse<MutableList<Video>>>

    @FormUrlEncoded
    @POST("${ApiConstants.VIDEO_API_PATH}catelist")
    fun getGuessLike(@Field("type") type: Int = 0, @Field("tag") id: Int? = 1, @Field("page") page: Int = 1,
                     @Field("pageCount") pageCount: Int = 20): Observable<BaseResponse<MutableList<Video>>>


    @FormUrlEncoded
    @POST("${ApiConstants.VIDEO_API_PATH}search")
    fun getSearchVideo(@Field("vname") vname: String, @Field("page") page: Int,
                       @Field("pageCount") pageCount: Int): Observable<BaseResponse<MutableList<Video>>>

    @POST("${ApiConstants.VIDEO_API_PATH}hot/getkeywords")
    fun getHotSearchTag(): Observable<BaseResponse<Array<String>>>

    //统计接口
    //tag 点击次数   日榜 50   周榜25   首页推荐 51
    @FormUrlEncoded
    @POST("${ApiConstants.VIDEO_API_PATH}analytics/tag")
    fun analyticsVideoTag(@Field("tag") tag: Int): Observable<BaseResponse<Any>>

    //视频完成播放次数
    //vid 视频id，minsec 播放时长，  done 是否完成播放，1 完成，0为完成
    @FormUrlEncoded
    @POST("${ApiConstants.VIDEO_API_PATH}analytics/clinetplayrecord")
    fun analyticsClinetPlayRecord(@Field("vid") vid: Int, @Field("minsec") minsec: String = "", @Field("done") done: Int): Observable<BaseResponse<String>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/domain/get")
    fun getShareDomain(@Field("type") type: Int = 2): Observable<BaseResponse<List<ImgDomainConfig>>>

    @POST("${ApiConstants.VIDEO_API_PATH}getlongvideolist")
    fun longVideo(): Observable<BaseResponse<MutableList<Video>>>

    @POST("${ApiConstants.API_PAY_VERSION}video/cartoon/conf/autoplay")
    fun getAutoPlay(): Observable<BaseResponse<AutoPlayConf>>
}