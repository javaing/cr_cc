package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.net.ApiConstants
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.*

interface RecommendService {

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/recommend/lists")
    suspend fun getRecommendK(@Field("ids") ids: String): BaseResponse<TreeMap<String, RecommendPositionList>>

    /**
     * @param sortField 排序字段订单转化率rate_order，rate_click收费章节
     * @param sort    排序方式，默认desc
     */
    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/statiscartoon/minlist")
    suspend fun getComicList(@Field("num") pageSize: Int, @Field("sortField") sortField: String, @Field("page") page: Int, @Field("sort") sort: Int = 1): BaseResponse<List<ComicBookBean>>

    /**
     * 导流事件接口记录
     */
    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/appupdateop")
    suspend fun appUpdateOp(@Field("uid") uid: Int, @Field("utemp") utemp: Int, @Field("opType") opType: Int): BaseResponse<String>

    /**
     * 导流事件是否弹出更新提示
     */
    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/appdrainage")
    suspend fun appDrainage(@Field("uid") uid: Int, @Field("utemp") utemp: Int): BaseResponse<String>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}adzone/selectZoneIdByStatus?status=1")
    suspend fun getadzone(@Field("status") status: Int): BaseResponse<List<AdZoneBean>>

}