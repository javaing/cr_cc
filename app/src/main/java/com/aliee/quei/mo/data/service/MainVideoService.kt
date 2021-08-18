package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.bean.Tag
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import org.androidannotations.annotations.rest.Post
import retrofit2.http.*

interface MainVideoService {

    @POST("randomlist")
    suspend fun randomList(): BaseResponse<MutableList<Video>>

    @FormUrlEncoded
    @POST("list")
    suspend fun mainVideoList(@Field("page") page: Int,
                      @Field("pageCount") pageCount: Int,
                      @Field("totalsec") totalsec: String):
            BaseResponse<MutableList<Video>>

    @POST("showtags")
    suspend fun mainTagsK(): BaseResponse<MutableList<Tag>>

    @FormUrlEncoded
    @POST("catelist")
    suspend fun videoRankingList(@Field("type") type: Int = 2,
                         @Field("tag") tag: Int,
                         @Field("page") page: Int,
                         @Field("pageCount") pageCount: Int):
            BaseResponse<MutableList<Video>>


    //观看记录
    @FormUrlEncoded
    @POST("getplayhis")
    suspend fun videoRecommend(@Field("page") page: Int,
                       @Field("pageCount") pageCount: Int): BaseResponse<MutableList<Video>>


    //删除观看记录
    @FormUrlEncoded
    @POST("delplayhist")
    suspend fun delRecommend(@Field("hisid") hisid: Int): BaseResponse<String>


    @FormUrlEncoded
    @POST("listmyvideo")
    suspend fun getMyVideoList(@Field("page") page: Int,
                       @Field("pageCount") pageCount: Int): BaseResponse<MutableList<Video>>

    @FormUrlEncoded
    @POST("catelist")
    suspend fun getGuessLike(@Field("type") type: Int = 0, @Field("tag") id: Int? = 1, @Field("page") page: Int = 1,
                     @Field("pageCount") pageCount: Int = 20): BaseResponse<MutableList<Video>>


    @FormUrlEncoded
    @POST("search")
    suspend fun getSearchVideo(@Field("vname") vname: String, @Field("page") page: Int,
                       @Field("pageCount") pageCount: Int): BaseResponse<MutableList<Video>>

    @POST("hot/getkeywords")
    suspend fun getHotSearchTag(): BaseResponse<Array<String>>

    //统计接口
    //tag 点击次数   日榜 50   周榜25   首页推荐 51
    @FormUrlEncoded
    @POST("analytics/tag")
    suspend fun analyticsVideoTag(@Field("tag") tag: Int): BaseResponse<Any>

    //视频完成播放次数
    //vid 视频id，minsec 播放时长，  done 是否完成播放，1 完成，0为完成
    @FormUrlEncoded
    @POST("analytics/clinetplayrecord")
    suspend fun analyticsClinetPlayRecord(@Field("vid") vid: Int, @Field("minsec") minsec: String = "", @Field("done") done: Int): BaseResponse<String>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/domain/get")
    suspend fun getShareDomain(@Field("type") type: Int = 2): BaseResponse<List<ImgDomainConfig>>

    @POST("getlongvideolist")
    suspend fun longVideo(): BaseResponse<MutableList<Video>>

    @POST("${ApiConstants.API_PAY_VERSION}video/cartoon/conf/autoplay")
    suspend fun getAutoPlayK(): BaseResponse<AutoPlayConf>
}